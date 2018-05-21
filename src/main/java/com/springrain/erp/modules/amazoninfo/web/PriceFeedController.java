package com.springrain.erp.modules.amazoninfo.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.endpoint.Client;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProduct2;
import com.springrain.erp.modules.amazoninfo.entity.Price;
import com.springrain.erp.modules.amazoninfo.entity.PriceFeed;
import com.springrain.erp.modules.amazoninfo.entity.ProductPriceApproval;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.PriceFeedService;
import com.springrain.erp.modules.amazoninfo.service.ProductPriceApprovalService;
import com.springrain.erp.modules.amazoninfo.service.ProductPriceService;
import com.springrain.erp.modules.amazoninfo.service.SaleProfitService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;
import com.springrain.magento.MagentoClientService;

/**
 * 亚马逊帖子上架Controller
 * @author tim
 * @version 2014-08-06
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/priceFeed")
public class PriceFeedController extends BaseController {
	@Autowired
	private PriceFeedService priceFeedService;
	
	@Autowired
	private AmazonProductService amazonProductService;
	
	@Autowired
	private PsiProductGroupUserService groupUserService;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;

	@Autowired
	private ProductPriceApprovalService productPriceApprovalService;
	
	@Autowired
	private ProductPriceService productPriceService;
	
	@Autowired
	private SaleProfitService saleProfitService;
	
	@Autowired
	private PsiProductService  productService;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	private static Logger logger = LoggerFactory.getLogger(PriceFeedController.class);
	
	@Autowired
	private SystemService service;
	
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
	public PriceFeed get(@RequestParam(required=false) Integer id) {
		if (id!=null&&id>0){
			return priceFeedService.get(id);
		}else{
			return new PriceFeed();
		}
	}
	
	@RequiresPermissions("amazoninfo:priceFeed:view")
	@RequestMapping(value = {"getPrice"})
	@ResponseBody
	public AmazonProduct2 getPrice(String countrySku) {
		if(!countrySku.contains("@")){
			return null;
		}
		String arr[] =countrySku.split("@");
		String account=arr[0];
		Map<String,String> accountCountryMap=amazonAccountConfigService.findCountryByAccount();
		String country=accountCountryMap.get(account);
		
		String sku=arr[1];
		AmazonProduct2 product = amazonProduct2Service.getProductByAccount(account, sku);
		Float price = productPriceApprovalService.findPriceBySkuAndCountry(sku, country, "1"); //拿到审批价格
		if (price != null && price > 0) {
			if (product.getWarnPrice()!=null && product.getWarnPrice() > price) {
				product.setWarnPrice(price);
			} else if(product.getHighWarnPrice() != null && product.getHighWarnPrice() < price){
				product.setHighWarnPrice(price);
			}
		}
		//计算利润率供参考By leehong 2016-8-31
		Map<String, Integer> commissionMap = productPriceService.findCommission();	//佣金比例
		Map<String, String> skuNameMap = saleProfitService.findSkuNames();
		String productName = skuNameMap.get(sku);
		product.setEan(""); //用ean属性存放利润率传递到前端
		//取保本价
		Float costPrice = productPriceService.findPriceByCountryAndName(country, productName);
		Float salePrice = product.getSalePrice();
		if (salePrice == null) {
			salePrice = product.getPrice();
		}
		if(costPrice != null && salePrice != null){
			int commission = 0;
			try {
				commission = commissionMap.get(productName + "_" + country);
			} catch (NullPointerException e) {}
			float rate = (salePrice-costPrice)*(100-commission)/salePrice;
			String temp = country.toUpperCase();
			if("UK".equals(temp)){
				temp = "GB";
			}
			if("COM".equals(temp)){
				temp = "US";
			}
			CountryCode vatCode = CountryCode.valueOf(temp);
			if(vatCode!=null){
				float vat = vatCode.getVat()/100f;
				rate = ((salePrice-costPrice)/(1+vat)*100-(salePrice-costPrice)*commission)/salePrice;
			}
			product.setEan(String.format("%.2f", rate));
		}
		if (product.getSalePrice() == null) {
			product.setSalePrice(product.getPrice());
		}
		Float avgPrice = productPriceService.findAvgPriceByCountryAndSku(country, sku);
		if (avgPrice == null) {
			avgPrice = product.getSalePrice();
		}
		avgPrice = avgPrice * 1.1f;
		Float highPrice = productPriceApprovalService.findPriceBySkuAndCountry(sku, country, "2"); //拿到涨价审批价格
		if (highPrice != null && highPrice > avgPrice) {
			avgPrice = highPrice;
		}
		//Fnsku属性涨价10%的上限
		product.setFnsku(String.format("%.2f", avgPrice));
		return product;
	}
	
	@RequestMapping(value = {"getSkus"})
	@ResponseBody
	public List<Map<String, String>> getSkus(String accountName,String productName) {
		List<Map<String, String>> skus = Lists.newArrayList();
		//获取管辖的产品线sku
		Map<String,Map<String,List<String>>> productLineMap=groupUserService.getProductGroupCountry();
		Set<String>  typesSet = Sets.newHashSet();
		Set<String>  country_types = Sets.newHashSet();
		Map<String,String> accountCountryMap=amazonAccountConfigService.findCountryByAccount();
		if(productLineMap!=null&&productLineMap.size()>0&&productLineMap.get(UserUtils.getUser().getId())!=null&&productLineMap.get(UserUtils.getUser().getId()).size()>0){
			for(String account:accountName.split(",")){
				String country1=accountCountryMap.get(account);
				if(productLineMap.get(UserUtils.getUser().getId()).get(country1.startsWith("com")?"com":country1)!=null){
					typesSet.addAll(productLineMap.get(UserUtils.getUser().getId()).get(country1.startsWith("com")?"com":country1));
					for(String type:typesSet){
						country_types.add(country1+"_"+type);
					}
				}
			}
		}
		if(StringUtils.isEmpty(productName)){
			return null;
		}
		
		//查出平台负责人
		Map<String,User> platMangerMap=this.groupUserService.getCountryManager();
		
		//如果是平台负责人
		for(String account:accountName.split(",")){
			String country1=accountCountryMap.get(account);
			if(platMangerMap.get(country1)!=null&&platMangerMap.get(country1).getId().equals(UserUtils.getUser().getId())){
				List<Dict> dics =DictUtils.getDictList("product_type");
				for(Dict dic :dics){
					if("0".equals(dic.getDelFlag())){
						country_types.add(country1+"_"+dic.getValue());
					}
				}
			}
		}
		List<Map<String, String>> rs = amazonProductService.findSkuForEditPrice(accountName,typesSet,country_types);
		for (Map<String, String> map : rs) {
			if (map.get("value").contains(productName+"[")) {
				skus.add(map);
			}
		}
		return skus;
	}
	
	
	@RequiresPermissions("amazoninfo:priceFeed:view")
	@RequestMapping(value = {"list", ""})
	public String list(PriceFeed priceFeed, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(priceFeed.getRequestDate()==null){
			priceFeed.setRequestDate(DateUtils.addMonths(today,-1));
			priceFeed.setEndDate(today);
		}
		User user = UserUtils.getUser();
		if (!user.isAdmin()&& priceFeed.getCreateBy()==null){
			priceFeed.setCreateBy(user);
		}; 
		if(UserUtils.hasPermission("it:special:permission")){
			priceFeed.setCreateBy(null);
		}
		Page<PriceFeed> page = new Page<PriceFeed>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("id desc");
		}else{
			page.setOrderBy(orderBy+",id desc");
		}	
        page = priceFeedService.find(page, priceFeed); 
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
        model.addAttribute("cuser",user);
		return "modules/amazoninfo/priceFeedList";
	}

	
	@RequiresPermissions("amazoninfo:priceFeed:view")
	@RequestMapping(value = "form")
	public String form(PriceFeed priceFeed, Model model) throws UnsupportedEncodingException {
		if(StringUtils.isNotEmpty(priceFeed.getReason())){
			priceFeed.setReason(URLDecoder.decode(priceFeed.getReason(), "UTF-8"));
		}
		Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountry();
		model.addAttribute("accountMap", accountMap);
		if(priceFeed.getPrices()!=null&&priceFeed.getPrices().size()>0){
			for(Iterator<Price> iterator = priceFeed.getPrices().iterator(); iterator.hasNext();) {
				Price price = (Price) iterator.next();
				if(StringUtils.isEmpty(price.getSku())){
					iterator.remove();
				}else{
					int i= price.getSku().indexOf("@");
					if(i>0){
						String countryFlag = price.getSku().substring(0, i);
						if(StringUtils.isEmpty(priceFeed.getAccountName()) || !priceFeed.getAccountName().contains(countryFlag)){
							iterator.remove();
						}
					}
				}
			}
		}
		//查出平台负责人
		Map<String,User> platMangerMap=this.groupUserService.getCountryManager();
		Map<String,String> accountCountryMap=amazonAccountConfigService.findCountryByAccount();
		if(StringUtils.isNotEmpty(priceFeed.getAccountName())){
			//获取管辖的产品线sku
			Map<String,Map<String,List<String>>> productLineMap=groupUserService.getProductGroupCountry();
			Set<String>  typesSet = Sets.newHashSet();
			Set<String>  country_types = Sets.newHashSet();
			if(productLineMap!=null&&productLineMap.size()>0&&productLineMap.get(UserUtils.getUser().getId())!=null&&productLineMap.get(UserUtils.getUser().getId()).size()>0){
				for(String accountName:priceFeed.getAccountName().split(",")){
					String country=accountCountryMap.get(accountName);
					if(productLineMap.get(UserUtils.getUser().getId()).get(country.startsWith("com")?"com":country)!=null){
						typesSet.addAll(productLineMap.get(UserUtils.getUser().getId()).get(country.startsWith("com")?"com":country));
						for(String type:typesSet){
							country_types.add(country+"_"+type);
						}
					}
					//如果是平台负责人
					if(platMangerMap.get(country)!=null&&platMangerMap.get(country).getId().equals(UserUtils.getUser().getId())){
						List<Dict> dicts =DictUtils.getDictList("product_type");
						for(Dict dic :dicts){
							if("0".equals(dic.getDelFlag())){
								country_types.add(country+"_"+dic.getValue());
							}
						}
					}
				}
			}
			model.addAttribute("sku", amazonProductService.findSkuForEditPrice(priceFeed.getAccountName(),typesSet,country_types));
		}else{
			//根据上贴权限设置默认的country,如果只有一个国家，就默认这个国家
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					if (StringUtils.isEmpty(priceFeed.getCountry())) {
						priceFeed.setCountry(dict.getValue());
						for (Map.Entry<String,String> entry: accountCountryMap.entrySet()) {
							if(entry.getValue().equals(priceFeed.getCountry())){
								priceFeed.setAccountName(entry.getKey());
								break;
							}
						}
						break;
					} else {
						priceFeed.setCountry(null);	//多个国家不设置默认值
						break;
					}
					
				}
			}
			if (StringUtils.isNotEmpty(priceFeed.getCountry())) {
				//获取管辖的产品线sku
				Map<String,Map<String,List<String>>> productLineMap=groupUserService.getProductGroupCountry();
				Set<String>  typesSet = Sets.newHashSet();
				Set<String>  country_types = Sets.newHashSet();
				if(productLineMap!=null&&productLineMap.size()>0&&productLineMap.get(UserUtils.getUser().getId())!=null&&productLineMap.get(UserUtils.getUser().getId()).size()>0){
					for(String accountName:priceFeed.getAccountName().split(",")){
						String country=accountCountryMap.get(accountName);
						if(productLineMap.get(UserUtils.getUser().getId()).get(country.startsWith("com")?"com":country)!=null){
							typesSet.addAll(productLineMap.get(UserUtils.getUser().getId()).get(country.startsWith("com")?"com":country));
							for(String type:typesSet){
								country_types.add(country+"_"+type);
							}
						}
						//如果是平台负责人
						if(platMangerMap.get(country)!=null&&platMangerMap.get(country).getId().equals(UserUtils.getUser().getId())){
							for(Dict dic :dicts){
								if("0".equals(dic.getDelFlag())){
									country_types.add(country+"_"+dic.getValue());
								}
							}
						}
					}
				}
				model.addAttribute("sku", amazonProductService.findSkuForEditPrice(priceFeed.getAccountName(),typesSet,country_types));
			}
		}
		Date saleEndDate = DateUtils.addYears(new Date(), 10);
//		if(StringUtils.isNotEmpty(priceFeed.getCountry())&&priceFeed.getCountry().contains("jp")){
//			saleEndDate = DateUtils.addMonths(new Date(), 1);
//		}
		model.addAttribute("saleStartDate", DateUtils.addDays(new Date(),-1));
		model.addAttribute("saleEndDate",saleEndDate);
		model.addAttribute("priceFeed", priceFeed);
		return "modules/amazoninfo/priceFeedForm";
	}

	@RequiresPermissions("amazoninfo:priceFeed:view")
	@RequestMapping(value = "view")
	public String view(PriceFeed priceFeed, Model model) {
		model.addAttribute("priceFeed", priceFeed);
		return "modules/amazoninfo/priceFeedView";
	}
	private static DateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmssSS"); 
	
	
	private static String header ="<table width=\"96%\" border=\"1\" style='font-size:12px;table-layout:fixed;empty-cells:show; border-collapse: collapse;margin:0 auto;border:1px solid #9db3c5;color:#666;word-wrap:break-word;word-break:break-all'>"+
			  "<tr>"+
			   " <th width=\"7%\" style='background-repeat::repeat-x;height:40px;color:#a7d1fd;border:1px solid #a7d1fd; padding:0 2px 0;'>Country</th>"+
			  "  <th width=\"10%\" style='background-repeat::repeat-x;height:30px;color:#a7d1fd;border:1px solid #a7d1fd; padding:0 2px 0;'>Sku</th>"+
			    "<th width=\"10%\" style='background-repeat::repeat-x;height:30px;color:#a7d1fd;border:1px solid #a7d1fd; padding:0 2px 0;'>改回原价</th>"+
			   " <th width=\"10%\" style='background-repeat::repeat-x;height:30px;color:#a7d1fd;border:1px solid #a7d1fd; padding:0 2px 0;'>改回销售价格</th>"+
			 " </tr>";
	
	@RequiresPermissions("amazoninfo:priceFeed:view")
	@RequestMapping(value = "save")
	public String save(final PriceFeed priceFeed, Model model, RedirectAttributes redirectAttributes) {
		StringBuffer rs = new StringBuffer("");
		Map<String,List<Price>>  countryMap = Maps.newHashMap();
		if(priceFeed.getPrices().size()==0){
			addMessage(redirectAttributes, "要修改的明细项为空，请检查！");
			return "redirect:"+Global.getAdminPath()+"/amazoninfo/priceFeed/?repage";
		}
		Map<String,AmazonAccountConfig> configMap=amazonAccountConfigService.findConfigByAccountName();
		if(priceFeed.getId()==null){
			//新增    分出国家和sku
			for (Iterator<Price> iterator =  priceFeed.getPrices().iterator(); iterator.hasNext();) {
				Price price = iterator.next();
				String countrySku = price.getSku();
				if(countrySku.contains("@")){
					int i= countrySku.indexOf("@");
					String accountName=countrySku.substring(0, i);
					//String countryFlag = "us".equals(countrySku.substring(0, i))?"com":countrySku.substring(0, i);
					String sku =countrySku.substring(i+1);
					List<Price> list = null;
					if(countryMap.get(accountName)!=null){
						list=countryMap.get(accountName);
					}else{
						list=Lists.newArrayList();
					}
					countryMap.put(accountName, list);
					price.setSku(sku);//放入正确的sku
					price.setPrice(price.getSalePrice());//设置相同的价格
					list.add(price);
				}else{
					throw new RuntimeException("sku["+countrySku+"]填写错误，正确格式为：xx@sku");
				}
			}
			for (Map.Entry<String,List<Price>> entry : countryMap.entrySet()) { 
			    String accountName = entry.getKey();
			    final AmazonAccountConfig config=configMap.get(accountName);
				//按不同国家生成      PriceFeed
				final PriceFeed  priceF = new PriceFeed();
				priceF.setReason(priceFeed.getReason());
				priceF.setAccountName(accountName);
				priceF.setCountry(config.getCountry());
				for(Price priceTemp:entry.getValue()){
					priceTemp.setPriceFeed(priceF);
					priceF.getPrices().add(priceTemp);
				}
				this.saveData(priceF,rs,config);
			}
		}else{
			//重新提交
			final AmazonAccountConfig config=configMap.get(priceFeed.getAccountName());
			this.saveData(priceFeed,rs,config);
		}
		addMessage(redirectAttributes, rs.toString());
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/priceFeed/?repage";
	}
	
	private void saveData(final PriceFeed priceFeed,StringBuffer rs,final AmazonAccountConfig config){
		String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/pricefeeds/";
		String dateStr = format2.format(new Date());
		priceFeed.setResultFile(dateStr);
		File dir = new File(ctxPath+dateStr);
		dir.mkdirs();
		rs.append(submit(priceFeed,dir,config));
		if("包邮调价".equals(priceFeed.getReason())){
			new Thread(){
				public void run() {
					String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/pricefeeds/";
					PriceFeed temp = new PriceFeed();
					temp.setCreateBy(service.getUser("1"));
					List<Price> prices = Lists.newArrayList();
					List<AmazonProduct2> products = Lists.newArrayList();
					List<AmazonProduct2> noPrice = Lists.newArrayList();
					for (Price tempPrice : priceFeed.getPrices()) {
						AmazonProduct2 temp2 = amazonProduct2Service.findWarnPriceProduct(priceFeed.getAccountName(),tempPrice.getSku());
						Price price = new Price();
						if(temp2!=null&&temp2.getPrice()!=null){
							price.setPrice(temp2.getPrice());
							price.setSalePrice(temp2.getSalePrice());
							//去获取实时价格
							try {
								String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
								Client client = BaseService.getCxfClient(interfaceUrl);
								Object[] str = new Object[]{Global.getConfig("ws.key"), tempPrice.getSku(), config.getAccountName()};
								Object[] rs = client.invoke("getPriceBySku", str);
								if (rs != null && rs[0] != null) {
									price.setSalePrice((Float)rs[0]);
								}
							} catch (Exception e) {
								logger.error(tempPrice.getSku()+"获取实时价格出错了", e);
							}
							if(price.getSalePrice()==null){
								noPrice.add(temp2);
								continue;
							}
							products.add(temp2);
						}else{
							continue;
						}
						temp2.setSalePrice(price.getPrice());
						temp2.setWarnPrice(price.getSalePrice());
						price.setSku(tempPrice.getSku());
						price.setPriceFeed(temp);
						price.setSaleStartDate(new Date(110, 0, 1));
						price.setSaleEndDate(new Date(120, 0,1));
						prices.add(price);
					}
					temp.setPrices(prices);
					if(products.size()==0){
						String skus = "";
						StringBuffer buf= new StringBuffer();
						for (AmazonProduct2 amazonProduct2 : noPrice) {
							buf.append(amazonProduct2.getSku());
						}
						skus=buf.toString();
						noteClaimer(priceFeed.getCreateBy().getEmail(),"产品包邮价格还原提醒邮件","产品["+skus+"]帖子状态异常原价格或销售价格无法获取，请自行改回!");
						return;
					}
					try {
						Thread.sleep(850000);
					} catch (InterruptedException e) {}
					String dateStr = format2.format(new Date());
					temp.setCountry(priceFeed.getCountry());
					temp.setAccountName(priceFeed.getAccountName());
					temp.setResultFile(dateStr);
					File dir = new File(ctxPath+dateStr);
					dir.mkdirs();
					submitSyn(temp,dir,config);
					temp = priceFeedService.get(temp.getId());
					int i = 0;
					StringBuilder buf=new StringBuilder("hi,"+priceFeed.getCreateBy().getName()+" <br/> 以下产品系统尝试改为包邮前原价"+("3".equals(temp.getState())?"成功":"失败")+"<br/><br/><br/>");
					buf.append(header);
					for (AmazonProduct2 amazonProduct2 : products) {
						buf.append(amazonProduct2.render(i%2==0));
						i++;
					}
					buf.append("</table><br/>");
					if(noPrice.size()>0){
						StringBuilder buff=new StringBuilder();
						for (AmazonProduct2 amazonProduct2 : noPrice) {
							buff.append(amazonProduct2.getSku());
						}
						buf.append("<span style='color:red'>产品["+buff.toString()+"]帖子状态异常原价格或销售价格无法获取，请自行改回!</span><br/>");
					}
					noteClaimer(priceFeed.getCreateBy().getEmail(),"产品包邮价格还原提醒邮件",buf.toString());
				};
			}.start();
		}
	}
	
	private  String submit(final PriceFeed priceFeed,final File dir,final AmazonAccountConfig config){
		String rs = "正在发到服务器，请等待结果。。。";
		try {
			if(priceFeed.getId()==null){
				if(priceFeed.getCreateBy()==null){
					priceFeed.setCreateBy(UserUtils.getUser());
				}
				priceFeed.setRequestDate(new Date());
			}
			priceFeed.setState("1");
			priceFeedService.save(priceFeed);
			new Thread(){
				public void run() {
					//远程webservice提交到亚马逊
					try {
						String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
						Client client = BaseService.getCxfClient(interfaceUrl);
						Object[] str = new Object[]{Global.getConfig("ws.key"), priceFeed.getId()};
						client.invoke("submitPriceFeed", str);
					} catch (Exception e) {
						priceFeed.setState("4");
						priceFeedService.save(priceFeed);
						logger.error(config.getAccountName()+"修改价格出错了", e);
					}
					PriceFeed feed = priceFeedService.get(priceFeed.getId());
					if ("3".equals(feed.getState())) {
						submitToInateck(priceFeed);
					}
				};
			}.start();
		} catch (Exception e) {
			logger.error("修改价格出错了!", e);
			rs = "修改价格出错了!";
		}
		return rs;
	}
	
	/**
	 * 价格修改提交到官网(FBA贴)
	 * @param priceFeed
	 * @param dir
	 * @return
	 */
	private void submitToInateck(final PriceFeed priceFeed){
		if("包邮调价".equals(priceFeed.getReason())){
			return;
		}
		try {
			new Thread(){
				public void run() {
					String country  = priceFeed.getCountry();
					Map<String, String> countrySkuAndAsinMap =  amazonProduct2Service.getAllAsinByCountrySku();
					//获取sku对应的asin,并拿到asin的最低价
					Map<String, Float> asinPriceMap = Maps.newHashMap();
					List<Price> list = priceFeed.getPrices();
					List<String> skuList = Lists.newArrayList();
					for (Price price : list) {
						String sku = price.getSku();
						AmazonProduct2 product = amazonProduct2Service.getProduct(country ,sku);
						//非fba贴忽略
						if (product.getIsFba() == null || "0".equals(product.getIsFba())) {
							continue;
						}
						skuList.add(sku);
						String asin = countrySkuAndAsinMap.get(country + "_" + price.getSku());
						if (StringUtils.isNotEmpty(asin)) {
							Float salePrice = price.getSalePrice();
							if (salePrice == null) {
								salePrice = price.getPrice();
							}
							Float lowPrice = asinPriceMap.get(asin);
							if (lowPrice == null || lowPrice > salePrice) {
								asinPriceMap.put(asin, salePrice);
							}
						}
					}
					if (asinPriceMap.size() > 0) {
						List<AmazonProduct2> product2s = amazonProduct2Service.findByAsinAndCountry(country, asinPriceMap.keySet(), "1");
						for (AmazonProduct2 amazonProduct2 : product2s) {
							String asin = amazonProduct2.getAsin();
							String sku = amazonProduct2.getSku();
							if (!skuList.contains(sku)) {	//未包含,比较价格
								Float salesPrice = amazonProduct2.getSalePrice();
								if (salesPrice == null) {
									salesPrice = amazonProduct2.getSalePrice();
								}
								if (asinPriceMap.get(asin) > salesPrice) {
									asinPriceMap.put(asin, salesPrice);
								}
							}
						}
						MagentoClientService.catalogProductPriceUpdate(country, asinPriceMap);
					}
				};
			}.start();
		} catch (Exception e) {
			logger.error("修改官网价格出错了!" + e.getMessage(), e);
		}
		return;
	}
	
	private void submitSyn(PriceFeed priceFeed,File dir,final AmazonAccountConfig config){
		try {
			if(priceFeed.getId()==null){
				priceFeed.setRequestDate(new Date());
			}
			priceFeed.setState("1");
			priceFeed.setReason("包邮改价自动改回原价");
			priceFeedService.save(priceFeed);
			try {
				String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
				Client client = BaseService.getCxfClient(interfaceUrl);
				Object[] str = new Object[]{Global.getConfig("ws.key"), priceFeed.getId()};
				client.invoke("submitPriceFeed", str);
			} catch (Exception e) {
				logger.error(config.getAccountName()+"修改价格出错了", e);
			}
		} catch (Exception e) {
			logger.error(config.getAccountName()+"修改价格出错了", e);
		}
	}
	
	public static String getResultStr(File result,List<Price> prices){
		String rs = "";
		StringBuffer buf= new StringBuffer();
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(result);
			Element root = doc.getRootElement();
			List<Element> msgs = root.elements("Message");
			for (int i = 0; i < msgs.size(); i++) {
				Element el = (Element)msgs.get(i).selectSingleNode("//ProcessingSummary");
				buf.append(prices.get(i).getSku() +":<br/>"+HtmlUtils.htmlEscape(el.asXML())+"<br/>");
			}
			rs = buf.toString();
			return rs;
		} catch (DocumentException e) {
			return "解析结果文件出错！请下载查看";
		}
	}
	
	@RequestMapping("/download")   
    public ModelAndView download(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/pricefeeds/";  
        String downLoadPath = ctxPath + fileName;
        fileName = fileName.substring(fileName.lastIndexOf("/")+1);
        try {   
            long fileLength = new File(downLoadPath).length();   
            response.setContentType("application/x-msdownload;");   
            response.setHeader("Content-disposition", "attachment; filename="  
                    + URLEncoder.encode(fileName,"utf-8"));   
            response.setHeader("Content-Length", String.valueOf(fileLength));   
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));   
            bos = new BufferedOutputStream(response.getOutputStream());   
            byte[] buff = new byte[2048];   
            int bytesRead;   
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {   
                bos.write(buff, 0, bytesRead);   
            }   
            bos.flush();
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            if (bis != null)   
                bis.close();   
            if (bos != null)   
                bos.close();   
        }   
        return null;   
    }   
	
	@Autowired
	private MailManager mailManager;
	
	public  void noteClaimer(final String address,final String subject,final String content){
		new Thread(){
			@Override
			public void run() {
				try{
					MailInfo mailInfo = new MailInfo(address, subject, new Date());
					mailInfo.setContent(HtmlUtils.htmlUnescape(content));
					boolean rs = mailManager.send(mailInfo);
					int i = 0 ;
					while(!rs && i<3){
						Thread.sleep(5000);
						rs = mailManager.send(mailInfo);
						i++;
					}
					if(!rs){
						logger.error("包邮改回提醒邮件发送失败-->"+content);
					}
				} catch (Exception e) {
					logger.error("包邮改回提醒邮件发送失败:"+e.getMessage());
				}
			}
		}.start();
	}
	
	//导出
	@RequestMapping(value = "exportPriceFeed")
	public String exportPriceFeed(PriceFeed priceFeed, HttpServletRequest request,HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()&& priceFeed.getCreateBy()==null){
			priceFeed.setCreateBy(user);
		}; 
		if(UserUtils.hasPermission("it:special:permission")){
			priceFeed.setCreateBy(null);
		}
		Page<PriceFeed> page = new Page<PriceFeed>(request, response);
		page.setOrderBy("id desc");
		page.setPageSize(60000);
        page = priceFeedService.find(page, priceFeed);
        List<PriceFeed> list = page.getList();
        if (list == null || list.size() == 0) {
			addMessage(redirectAttributes, "没有符合条件的导出数据!");
			return "redirect:"+Global.getAdminPath()+"/amazoninfo/priceFeed/?repage";
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
		contentStyle.setWrapText(true);
		
		HSSFCell cell = null;
		
		List<String> title = Lists.newArrayList("产品名","编号", "平台", "sku", "价格", "销售价格", "开始时间", "结束时间", "提交人", "提交时间", "结果摘要", "改价原由", "状态");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int rowIndex = 1;
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Dict> dictList = DictUtils.getDictList("platform");
		Map<String, String> platformMap = Maps.newHashMap();
		for (Dict dict : dictList) {
			platformMap.put(dict.getValue(), dict.getLabel());
		}
		Map<String,String> skuNameMap =productService.getProductNameBySku();
		for (int i = 0; i < list.size(); i++) {
			PriceFeed feed = list.get(i);
			for (Price price : feed.getPrices()) {
				int j = 0;
				row = sheet.createRow(rowIndex++);
				row.setHeight((short) 500);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(skuNameMap.get(price.getSku())!=null?skuNameMap.get(price.getSku()):"");
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(feed.getId());
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(platformMap.get(feed.getCountry()));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(price.getSku());
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(price.getPrice()==null?"":(price.getPrice()+""));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(price.getSalePrice()==null?"":(price.getSalePrice()+""));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(format.format(price.getSaleStartDate()));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(format.format(price.getSaleEndDate()));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(feed.getCreateBy()==null?"":feed.getCreateBy().getName());
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(format1.format(feed.getRequestDate()));
				String result = "";
				if (feed.getResult() != null && feed.getResult().contains("&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;")) {
					result = "修改成功";
				} else if(feed.getResult() != null && !feed.getResult().contains("&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;")){
					result = "修改失败";
				}
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(result);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(feed.getReason());
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(feed.getStateStr());
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

			String fileName = "产品价格修改记录" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("产品价格修改记录导出异常", e);
		}
		return null;
	}
	
	@RequestMapping(value = {"getProfitRate"})
	@ResponseBody
	public String getProfitRate(String countrySku, Float salePrice) {
		Map<String, Integer> commissionMap = productPriceService.findCommission();	//佣金比例
		Map<String, String> skuNameMap = saleProfitService.findSkuNames();
		String[] arr = countrySku.split("@");
		String accountName=arr[0];
		Map<String,String> accountCountryMap=amazonAccountConfigService.findCountryByAccount();
		String country=accountCountryMap.get(accountName);
		String productName = skuNameMap.get(arr[1]);
		//取保本价
		Float price = productPriceService.findPriceByCountryAndName(country, productName);
		if(price != null){
			//增值税率,欧洲有增值税,其他三国无增值税
			float vat = 0f;
			String temp = country.toUpperCase();
			if("UK".equals(temp)){
				temp = "GB";
			}
			if("COM".equals(temp) || country.startsWith("com")){
				temp = "US";
			}
			CountryCode vatCode = CountryCode.valueOf(temp);
			if (vatCode != null) {
				vat = vatCode.getVat()/100f;
			}
			
			int commission = 0;
			try {
				commission = commissionMap.get(productName + "_" + country);
			} catch (NullPointerException e) {}
			float rate = ((salePrice-price)*100/(1+vat)-(salePrice-price)*commission)/salePrice;
			return String.format("%.2f", rate);
		}
		return "保本价为空";
	}
	
	@RequestMapping(value = "approval2")
	public String approval(ProductPriceApproval productPriceApproval, String state, HttpServletRequest request, 
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		productPriceApproval = productPriceApprovalService.get(productPriceApproval.getId());
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
			}
			
			PriceFeed feed=new PriceFeed();
			feed.setCountry(productPriceApproval.getCountry());
			feed.setAccountName(productPriceApproval.getAccountName());
			feed.setCreateBy(productPriceApproval.getCreateBy());
			feed.setReason(productPriceApproval.getReason());
			List<Price> priceList=Lists.newArrayList();
			Price price=new Price();
			
			price.setPrice(productPriceApproval.getPrice());
			price.setSalePrice(productPriceApproval.getPrice());
			price.setSaleStartDate(productPriceApproval.getSaleStartDate());
			price.setSaleEndDate(productPriceApproval.getSaleEndDate());
			price.setSku(productPriceApproval.getSku());
			price.setPriceFeed(feed);
			priceList.add(price);
			feed.setPrices(priceList);
			StringBuffer rs = new StringBuffer("");
			Map<String,AmazonAccountConfig> configMap=amazonAccountConfigService.findConfigByAccountName();
			final AmazonAccountConfig config=configMap.get(feed.getAccountName());
			this.saveData(feed,rs,config);
		}
		addMessage(redirectAttributes, "审批操作成功！");
        return "redirect:"+Global.getAdminPath()+"/amazoninfo/productPriceApproval/?repage";
	}

	@RequestMapping(value = "batchApproval2")
	public String batchApproval2(@RequestParam("eid[]")String[] eid, String state, HttpServletRequest request, 
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		User user = UserUtils.getUser();
		Set<String> emailSet = Sets.newHashSet();
		Map<String,AmazonAccountConfig> configMap=amazonAccountConfigService.findConfigByAccountName();
		List<ProductPriceApproval> list = Lists.newArrayList();
		Map<Integer, Float> priceMap = Maps.newHashMap();
		boolean flag = false;
		//List<PriceFeed> feedList=Lists.newArrayList();
		Map<String,PriceFeed> map=Maps.newHashMap();
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
			productPriceApprovalService.save(product);
			
			Price price=new Price();
			price.setPrice(product.getPrice());
			price.setSalePrice(product.getPrice());
			price.setSaleStartDate(product.getSaleStartDate());
			price.setSaleEndDate(product.getSaleEndDate());
			price.setSku(product.getSku());
			
			PriceFeed priceFeed=map.get(product.getAccountName());
			List<Price> priceList=null;
			if(priceFeed==null){
				priceFeed=new PriceFeed(); 
				priceFeed.setCountry(product.getCountry());
				priceFeed.setAccountName(product.getAccountName());
				priceFeed.setCreateBy(user);
				priceFeed.setReason(product.getReason());
				priceList=Lists.newArrayList();
			}else{
				priceList=priceFeed.getPrices();
			}
			price.setPriceFeed(priceFeed);
			priceList.add(price);
			priceFeed.setPrices(priceList);
			map.put(product.getAccountName(),priceFeed);
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
			toAddress = buf.toString();
        	toAddress = toAddress.substring(0, toAddress.length()-1);
        	final MailInfo mailInfo = new MailInfo(toAddress, "产品价格审批提醒("+DateUtils.getDate("yyyy/MM/dd")+")", new Date());
        	String result = "1".equals(state)?"审批通过并且一键修改价格":"被否决";
			String contents = "<p><span style='font-size:20px'>Hi,<br/>&nbsp;&nbsp;&nbsp;&nbsp;你提交的产品价格审批申请已经"+result+"，请知悉。</span>";
			mailInfo.setContent(contents);
			new Thread(){
			    public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
		if (flag) {
			addMessage(redirectAttributes, "部分操作失败,系统自动跳过审批人和提交人相同的审批信息,请仔细核对！");
		} else {
			addMessage(redirectAttributes, "审批操作成功！");
		}
		if(map!=null&&map.size()>0){
			for ( Map.Entry<String,PriceFeed> entity: map.entrySet()) {
				PriceFeed priceFeed=entity.getValue();
				final AmazonAccountConfig config=configMap.get(entity.getKey());
				StringBuffer rs = new StringBuffer("");
				this.saveData(priceFeed,rs,config);
			}
		}
	
        return "redirect:"+Global.getAdminPath()+"/amazoninfo/priceFeed/?repage";
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
