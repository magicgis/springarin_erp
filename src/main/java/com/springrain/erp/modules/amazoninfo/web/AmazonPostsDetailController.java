/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.Client;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
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
import com.springrain.erp.common.utils.Reflections;
import com.springrain.erp.common.utils.excel.ExportTransportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.AmazonCatalogRank;
import com.springrain.erp.modules.amazoninfo.entity.AmazonEan;
import com.springrain.erp.modules.amazoninfo.entity.AmazonNewReleasesRank;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsChange;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsDetail;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsFeed;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsRelationshipChange;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsRelationshipFeed;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProduct2;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProductCatalog;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProductTypeCharge;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProductTypeCode;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsDetailService;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsFeedService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductCatalogService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.magento.MagentoClientService;

@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/amazonPortsDetail")
public class AmazonPostsDetailController extends BaseController {

	@Autowired
	private AmazonPostsDetailService amazonPostsDetailService;
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	@Autowired
	private AmazonProductService amazonProductService;
	@Autowired
	private AmazonPostsFeedService amazonPostsFeedService;
	@Autowired
	private PsiInventoryService psiInventoryService;
	@Autowired
	private PsiProductService productService;
	@Autowired
	private AmazonProductCatalogService amazonProductCatalogService;
	@Autowired
	private EventService eventService;
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private MailManager  mailManager;
	
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	private final static Logger logger = LoggerFactory.getLogger(AmazonPostsDetailController.class);
	
	
	@RequestMapping(value = {"list", ""})
	public String list(AmazonPostsDetail amazonPostsDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isBlank(amazonPostsDetail.getCountry())){
			amazonPostsDetail.setCountry("de");
		}
		long a = System.currentTimeMillis();
        Page<AmazonPostsDetail> page = amazonPostsDetailService.find(new Page<AmazonPostsDetail>(request, response), amazonPostsDetail); 
        System.out.println(System.currentTimeMillis()-a);
        model.addAttribute("page", page);
		return "modules/amazoninfo/amazonPostsDetail";
	}
	
	@RequiresPermissions("amazoninfo:posts:view")
	@RequestMapping(value = {"form"})
	public String form(AmazonPostsDetail amazonPostsDetail,String flag, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountry();
		model.addAttribute("accountMap", accountMap);
		
		if(StringUtils.isNotEmpty(amazonPostsDetail.getAccountName())){
			List<AmazonPostsDetail> list=amazonPostsDetailService.getProductNameList1(amazonPostsDetail.getAccountName());
			model.addAttribute("list", list);
			for (Map.Entry<String,List<String>> entry: accountMap.entrySet()) {
				List<String> accountList=entry.getValue();
				boolean breakFlag=false;
				for (String account: accountList) {
					if(account.equals(amazonPostsDetail.getAccountName())){
						amazonPostsDetail.setCountry(entry.getKey());
						breakFlag=true;
						break;
					}
				}
				if(breakFlag){
					break;
				}
			}
			model.addAttribute("amazonPostsDetail", amazonPostsDetail);
		}else{
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					amazonPostsDetail.setCountry(dict.getValue());
					amazonPostsDetail.setAccountName(accountMap.get(dict.getValue()).get(0));
					List<AmazonPostsDetail> list=amazonPostsDetailService.getProductNameList1(amazonPostsDetail.getAccountName());
					model.addAttribute("list", list);
					model.addAttribute("amazonPostsDetail", amazonPostsDetail);
					break;
				}
			}
			if(StringUtils.isEmpty(amazonPostsDetail.getAccountName())){
				amazonPostsDetail.setCountry("");
				amazonPostsDetail.setAccountName("");
				model.addAttribute("amazonPostsDetail", amazonPostsDetail);
			}
		}
		model.addAttribute("flag",flag);
		return "modules/amazoninfo/amazonPostsForm";
	}
	
	@RequiresPermissions("amazoninfo:posts:view")
	@RequestMapping(value = {"commonForm"})
	public String commonForm(AmazonPostsDetail amazonPostsDetail,HttpServletRequest request, HttpServletResponse response, Model model) {
		  if(StringUtils.isNotBlank(amazonPostsDetail.getAccountName())){
			Set<String> countrys=Sets.newHashSet(amazonPostsDetail.getAccountName().split(","));
			Map<String,Map<String,AmazonPostsDetail>> postsMap=amazonPostsDetailService.findCountryProductName(Sets.newHashSet(countrys),null);
		  	model.addAttribute("postsMap",postsMap);
		  }
			
			model.addAttribute("amazonPostsDetail", amazonPostsDetail);
			Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountry();
			model.addAttribute("accountMap", accountMap);
			
		    return "modules/amazoninfo/amazonPostsCommonForm";
	}
	
	@RequestMapping(value = "getAsinByCountry")
	@ResponseBody
	public List<AmazonPostsDetail> getAsinByCountry(String country){
		return amazonPostsDetailService.getProductNameList(country);
	}
	
	@RequestMapping(value = "getAsinByCountry1")
	@ResponseBody
	public List<AmazonPostsDetail> getAsinByCountry1(String accountName){
		return amazonPostsDetailService.getProductNameList1(accountName);
	}
	
	@RequestMapping(value = "getRegularContent")
	@ResponseBody
	public Map<String,Object> getRegularContent(String accountName,String asin){
		AmazonPostsDetail detail=amazonPostsDetailService.getDetailByAsinAndCountry(accountName, asin);
		//String rs="{\"msg\":\"true\",\"title\":\""+detail.getTitle()+"\"}";
		//return rs;
		Map<String,Object> map=Maps.newHashMap();
		if(detail!=null){
			map.put("title", detail.getTitle());
		}
		return map;
	}
	
	@RequestMapping(value = "getAllContent")
	@ResponseBody
	public Map<String,Object> getAllContent(String accountName,String asin,String isPrice,String selCountry){
		AmazonPostsDetail detail=amazonPostsDetailService.getDetailByAsinAndCountry(accountName, asin);
		Map<String,Object> map=Maps.newHashMap();
		if(detail==null){
			return map;
		}
		String country=amazonAccountConfigService.getByName(accountName).getCountry();
		
		map.put("title", HtmlUtils.htmlUnescape(detail.getTitle()));
		map.put("description", HtmlUtils.htmlUnescape(detail.getDescription()));
		map.put("feature1", HtmlUtils.htmlUnescape(detail.getFeature1()));
		map.put("feature2", HtmlUtils.htmlUnescape(detail.getFeature2()));
		map.put("feature3", HtmlUtils.htmlUnescape(detail.getFeature3()));
		map.put("feature4",  HtmlUtils.htmlUnescape(detail.getFeature4()));
		map.put("feature5",  HtmlUtils.htmlUnescape(detail.getFeature5()));
		map.put("keyword1",  HtmlUtils.htmlUnescape(detail.getKeyword1()));
		map.put("keyword2", HtmlUtils.htmlUnescape(detail.getKeyword2()));
		map.put("keyword3",HtmlUtils.htmlUnescape(detail.getKeyword3()));
		map.put("keyword4",  HtmlUtils.htmlUnescape(detail.getKeyword4()));
		map.put("keyword5", HtmlUtils.htmlUnescape(detail.getKeyword5()));
		map.put("brand", detail.getBrand());
		map.put("ean", detail.getEan());
		map.put("asin", detail.getAsin());
		map.put("manufacturer", detail.getManufacturer());
		map.put("partNumber", detail.getPartNumber());
		map.put("packageLength", detail.getPackageLength());
		map.put("packageWidth", detail.getPackageWidth());
		map.put("packageHeight", detail.getPackageHeight());
		map.put("packageWeight", detail.getPackageWeight());
		List<AmazonCatalogRank> rank=detail.getRankItems();
		Collections.sort(rank);
		if(rank!=null){
			for (int i=1;i<=rank.size();i++) {
				if(!rank.get(i-1).getCatalog().endsWith("_on_website")){
					map.put("catalog"+i,rank.get(i-1).getCatalog());
				}
			}
		}
		
		if("1".equals(isPrice)){
			String name=amazonProductService.findProductName(asin,selCountry);
			Float price=amazonProduct2Service.getLatestPrice(selCountry,name);
			if(price!=null){
				map.put("oldPrice",price);
				Float crossFee=amazonProduct2Service.getCrossFee(country, name);
				Object[] localFee=amazonProduct2Service.getLocalFee(country, name);
				if(crossFee==null||localFee==null){
					price=price+2;
				}else{
					Float fee=0f;
					if("uk".equals(country)){
						fee=(crossFee-((BigDecimal)localFee[0]).floatValue())*AmazonProduct2Service.getRateConfig().get("USD/GBP");
					}else{
						fee=(crossFee-((BigDecimal)localFee[0]).floatValue())*AmazonProduct2Service.getRateConfig().get("USD/EUR");
					}
					if(fee<=0){
						fee=2f;
					}
					price=price+fee;
				}
				if(!(price+"").endsWith(".99")){
					if(!(price+"").contains(".")){
						price=Float.parseFloat(price+"."+"99");
					}else{
						price=Float.parseFloat((price+"").replace(".", "/").split("/")[0]+"."+"99");
					}
				}
				
				Float tempPrice=price*1.8f;
				try{
					if(!(tempPrice+"").endsWith(".99")){
						if(!(tempPrice+"").contains(".")){
							tempPrice=Float.parseFloat(tempPrice+"."+"99");
						}else{
							tempPrice=Float.parseFloat((tempPrice+"").replace(".", "/").split("/")[0]+"."+"99");
						}
					}
				}catch(Exception e){
					logger.info(e.getMessage(),e);
				}
				map.put("price",tempPrice);
				map.put("salePrice",price);
			}
			
		}
		return map;
	}
	
	@RequestMapping(value = "getContent")
	@ResponseBody
	public List<Object> getContent(String accountName,String asin,String name){
		AmazonPostsDetail detail=amazonPostsDetailService.getDetailByAsinAndCountry(accountName, asin);
	/*	Field f;*/
		List<Object> list=Lists.newArrayList();
		try {
			if("feature".equals(name)){
				for(int i=1;i<=5;i++){
					/*f = detail.getClass().getDeclaredField(name+i);
					f.setAccessible(true);
					list.add((String) f.get(detail));*/
					list.add(Reflections.invokeGetter(detail, name+i));
				}
			}else if("catalog".equals(name)){
				List<AmazonCatalogRank> rank=detail.getRankItems();
				Collections.sort(rank);
				if(rank!=null){
					for (AmazonCatalogRank amazonCatalogRank : rank) {
						if(!amazonCatalogRank.getCatalog().endsWith("_on_website")){
							list.add(amazonCatalogRank.getCatalog());
						}
					}
				}
				
			}else if("packageDimensions".equals(name)){
				list.add(detail.getPackageLength());
				list.add(detail.getPackageWidth());
				list.add(detail.getPackageHeight());
				list.add(detail.getPackageWeight());
			}else if("keyword".equals(name)){
				if(StringUtils.isBlank((String)Reflections.invokeGetter(detail,"keyword1"))){
					AmazonPostsDetail posts=amazonPostsDetailService.getKeyWord(accountName, asin);
					list.add(posts.getKeyword1());
					list.add(posts.getKeyword2());
					list.add(posts.getKeyword3());
					list.add(posts.getKeyword4());
					list.add(posts.getKeyword5());
				}else{
					for(int i=1;i<=5;i++){
						list.add(Reflections.invokeGetter(detail, name+i));
					}
				}
			}else{
				/*f = detail.getClass().getDeclaredField(name);
				f.setAccessible(true);
				list.add((Object) f.get(detail));*/
				if("description".equals(name)){
					if(StringUtils.isBlank((String)Reflections.invokeGetter(detail, name))){
						list.add(amazonPostsDetailService.getDescription(accountName, asin));
					}else{
						list.add(Reflections.invokeGetter(detail, name));
					}
				}else{
					list.add(Reflections.invokeGetter(detail, name));
				}
				
			}
			return list;
			//return (String) f.get(detail);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequiresPermissions("amazoninfo:posts:view")
	@RequestMapping(value = {"editPostsChange"})
	public String editPostsChange(AmazonPostsFeed amazonPostsFeed, HttpServletRequest request, HttpServletResponse response, Model model) {
		amazonPostsFeed=amazonPostsFeedService.get(amazonPostsFeed.getId());
		model.addAttribute("amazonPostsFeed", amazonPostsFeed);
		List<AmazonPostsDetail> list=amazonPostsDetailService.getProductNameList1(amazonPostsFeed.getAccountName());
		model.addAttribute("list", list);
	    AmazonPostsDetail amazonPostsDetail=new AmazonPostsDetail();
	    amazonPostsDetail.setCountry(amazonPostsFeed.getCountry());
	    amazonPostsDetail.setAccountName(amazonPostsFeed.getAccountName());
		model.addAttribute("amazonPostsDetail", amazonPostsDetail);
		
		Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountry();
		model.addAttribute("accountMap", accountMap);
		
		model.addAttribute("flag","2");
		return "modules/amazoninfo/amazonPostsForm";
	}
	
	@RequiresPermissions("amazoninfo:posts:view")
	@RequestMapping(value = {"savePostsChange"})
	public String savePostsChange(AmazonPostsFeed amazonPostsFeed, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		final AmazonAccountConfig  config=amazonAccountConfigService.getByName(amazonPostsFeed.getAccountName());
		if(config==null){
			throw new RuntimeException(amazonPostsFeed.getAccountName()+" 查询不到账号信息");
		}
		amazonPostsFeed.setCountry(config.getCountry());
		    
		if(StringUtils.isBlank(amazonPostsFeed.getOperateType())){
			amazonPostsFeed.setOperateType("0");
		}else{
			amazonPostsFeed.setOperateType(amazonPostsFeed.getOperateType());
		}
		
		if(amazonPostsFeed.getId()!=null&&amazonPostsFeed.getId()>0){
			amazonPostsFeedService.deleteByFeedId(amazonPostsFeed.getId());
		}
		
		boolean flag=false;
		boolean eanFlag=true;
		for (AmazonPostsChange item : amazonPostsFeed.getItems()) {
			if(StringUtils.isNotBlank(item.getTitle())){
				flag=true;
			}
			item.setCountry(amazonPostsFeed.getCountry());
			item.setDescription(HtmlUtils.htmlUnescape(item.getDescription()));
			if("1".equals(amazonPostsFeed.getOperateType())){//新增普通帖
				if(StringUtils.isBlank(item.getEan())){//ean为空 新分配一个ean
					String ean=amazonPostsDetailService.findActiveEan();
					if(StringUtils.isBlank(ean)){
						eanFlag=false;
						break;
					}else{
						item.setEan(ean);
						String asin=amazonProduct2Service.getAsinByEan(ean);
						if(StringUtils.isNotBlank(asin)){
							item.setAsin(asin);
						}
					}
				}
			}else{
				if(StringUtils.isBlank(item.getAsin())){
					if("1".equals(amazonPostsFeed.getOperateType())||"6".equals(amazonPostsFeed.getOperateType())||"5".equals(amazonPostsFeed.getOperateType())){
						String asin=amazonProduct2Service.getAsinByEan(item.getEan());
						if(StringUtils.isNotBlank(asin)){
							item.setAsin(asin);
						}
					}
				}
			}
			item.setAmazonPostsFeed(amazonPostsFeed);
		}
		if(eanFlag){
			if(flag){
				if(StringUtils.isBlank(amazonPostsFeed.getOperateType())||"0".equals(amazonPostsFeed.getOperateType())||"6".equals(amazonPostsFeed.getOperateType())){
					amazonPostsDetailService.updateSelectItems(amazonPostsFeed);
				}
				//0：编辑  1:新增普通帖  2：新建父帖 3：删帖  4帖子类型转换 5帖子一键还原  6复制贴 7 cross帖 8新建本地帖
				submit(amazonPostsFeed,config);
			}else{
				addMessage(redirectAttributes, "提交失败,请重新提交");
			}
		}else{
			addMessage(redirectAttributes, "有效Ean不足,提交失败");
		}
		
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonPortsDetail/changePostsList/?country="+amazonPostsFeed.getCountry();
		
	}
	
	@RequiresPermissions("amazoninfo:posts:view")
	@RequestMapping(value = {"saveEnglishPostsChange"})
	public String saveEnglishPostsChange(AmazonPostsFeed amazonPostsFeed,String[] accountName,HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		amazonPostsFeed.setOperateType("0");
		
		Map<String,Map<String,AmazonPostsDetail>> postsMap=amazonPostsDetailService.findCountryProductName(Sets.newHashSet(accountName),null);
		
		for (String account:accountName) {
			    AmazonPostsFeed comFeed=new AmazonPostsFeed();
			    comFeed.setAccountName(account);
			    final AmazonAccountConfig  config=amazonAccountConfigService.getByName(account);
			    comFeed.setCountry(config.getCountry());
				comFeed.setOperateType("0");
				List<AmazonPostsChange> items=Lists.newArrayList();
				for (AmazonPostsChange item : amazonPostsFeed.getItems()) {
					
					if(postsMap.get(item.getProductName())!=null&&postsMap.get(item.getProductName()).get(account)!=null){
						AmazonPostsDetail detail=postsMap.get(item.getProductName()).get(account);
						item.setComSku(detail.getSku());
						item.setComAsin(detail.getAsin());
					}
					AmazonPostsChange comItem=new AmazonPostsChange();
					if(StringUtils.isNotBlank(item.getTitle())&&StringUtils.isNotBlank(item.getComSku())){
						comItem.setCountry(config.getCountry());
						comItem.setAsin(item.getComAsin());
						comItem.setProductName(item.getProductName());
						comItem.setTitle(item.getTitle());
						comItem.setSku(item.getComSku());
						comItem.setStudio(item.getStudio());
						if(StringUtils.isNotBlank(item.getDescription())){
							comItem.setDescription(HtmlUtils.htmlUnescape(item.getDescription()));
						}
					    if(StringUtils.isNotBlank(item.getKeyword1())){
					    	comItem.setKeyword1(item.getKeyword1());
					    	comItem.setKeyword2(item.getKeyword2());
					    	comItem.setKeyword3(item.getKeyword3());
					    	comItem.setKeyword4(item.getKeyword4());
					    	comItem.setKeyword5(item.getKeyword5());
					    }
					    if(StringUtils.isNotBlank(item.getFeature1())){
					    	comItem.setFeature1(item.getFeature1());
					    	comItem.setFeature2(item.getFeature2());
					    	comItem.setFeature3(item.getFeature3());
					    	comItem.setFeature4(item.getFeature4());
					    	comItem.setFeature5(item.getFeature5());
					    }
						if(StringUtils.isNotBlank(item.getBrand())){
							comItem.setBrand(item.getBrand());
						}
						if(StringUtils.isNotBlank(item.getManufacturer())){
							comItem.setManufacturer(item.getManufacturer());
						}
						
						if(item.getPackageLength()!=null){
							comItem.setPackageLength(item.getPackageLength());
							comItem.setPackageWidth(item.getPackageWidth());
							comItem.setPackageHeight(item.getPackageHeight());
							comItem.setPackageWeight(item.getPackageWeight());
						}
						comItem.setAmazonPostsFeed(comFeed);
						items.add(comItem);
					}
				}
				comFeed.setItems(items);
				if(comFeed!=null&&comFeed.getItems()!=null&&comFeed.getItems().size()>0){
					if(StringUtils.isBlank(comFeed.getOperateType())||"0".equals(comFeed.getOperateType())||"6".equals(comFeed.getOperateType())){
						amazonPostsDetailService.updateSelectItems(comFeed);
					}
					//0：编辑  1:新增普通帖  2：新建父帖 3：删帖  4帖子类型转换 5帖子一键还原  6复制贴 7 cross帖 8新建本地帖
					submit(comFeed,config);
				}
			}
		
		
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonPortsDetail/changePostsList/?country="+amazonPostsFeed.getCountry();
		
	}

	@RequestMapping(value = "viewDetail")
	public String viewDetail(AmazonPostsFeed amazonPostsFeed, Model model) {
		amazonPostsFeed=amazonPostsFeedService.get(amazonPostsFeed.getId());
		model.addAttribute("amazonPostsFeed", amazonPostsFeed);
		return "modules/amazoninfo/amazonPostsFeedView";
	}
	
	
	@RequestMapping(value = {"changePostsList"})
	public String changePostsList(AmazonPostsFeed amazonPostsFeed, HttpServletRequest request, HttpServletResponse response, Model model){
		Page<AmazonPostsFeed> page = new Page<AmazonPostsFeed>(request, response);
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (amazonPostsFeed.getCreateDate() == null) {
			amazonPostsFeed.setCreateDate(DateUtils.addMonths(today, -1));
		}
		if (amazonPostsFeed.getEndDate()== null) {
			amazonPostsFeed.setEndDate(today);
		}
		
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("createDate desc");
		} else {
			page.setOrderBy(orderBy + ",createDate desc");
		}
		
		User user = UserUtils.getUser();
		if (amazonPostsFeed.getCreateUser()==null){
			amazonPostsFeed.setCreateUser(user);
		} 
		
		page = amazonPostsFeedService.find(page,amazonPostsFeed);
		page.setOrderBy(orderBy);
		model.addAttribute("page", page);
		model.addAttribute("amazonPostsFeed", amazonPostsFeed);
		model.addAttribute("cuser",user);
		return "modules/amazoninfo/amazonPostsFeedList";
	}
	
	/*public static void main(String[] args) {
		AmazonPostsDetail detail=new AmazonPostsDetail();
		detail.setDescription("sdfsdfsdf");
		detail.setKeyword1("df");
		Field f;
		try {
			f = detail.getClass().getDeclaredField("keyword1");
			f.setAccessible(true);
			System.out.println(f.get(detail));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	//viewParent
	@RequestMapping(value = {"viewParent"})
	public String viewParent(AmazonPostsDetail amazonPortsDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		AmazonPostsDetail portsDetail=amazonPostsDetailService.get(amazonPortsDetail.getId());
		model.addAttribute("portsDetail",portsDetail);
		return "modules/amazoninfo/amazonParentPostsDetail";
	}
	
	@RequestMapping(value = {"queryParentDetail"})
	public String queryParentDetail(AmazonPostsDetail amazonPortsDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		AmazonPostsDetail portsDetail=amazonPostsDetailService.getPortsDetail(amazonPortsDetail);
		model.addAttribute("portsDetail",portsDetail);
		return "modules/amazoninfo/amazonParentPostsDetail";
	}
	
	
	@RequestMapping(value = {"viewByName"})
	public String viewByName(String productName,String country,String asin,Date date, HttpServletRequest request, HttpServletResponse response, Model model) {
		productName = HtmlUtils.htmlUnescape(productName);
		AmazonPostsDetail portsDetail=amazonPostsDetailService.getDetailByProductName(productName,country,asin,date);
		if(portsDetail!=null){
			List<String> asinList=amazonProduct2Service.getAllAsin(portsDetail.getProductName(),StringUtils.isBlank(portsDetail.getCountry())?"de":portsDetail.getCountry());
			Set<String> catalog=Sets.newHashSet();
			for (AmazonCatalogRank rank : portsDetail.getRankItems()) {
				catalog.add(rank.getCatalog());
			}
			if(catalog!=null&&catalog.size()>0){
				Map<String,Integer> map=amazonPostsDetailService.getRank(catalog,portsDetail.getQueryTime(),portsDetail.getProductName(),portsDetail.getCountry());
				model.addAttribute("map",map);
			}
			model.addAttribute("asinList",asinList);
		}else{
			portsDetail=new AmazonPostsDetail();
			portsDetail.setProductName(productName);
			portsDetail.setAsin(asin);
			portsDetail.setQueryTime(new Date());
			List<String> asinList=amazonProduct2Service.getAllAsin(portsDetail.getProductName(),StringUtils.isBlank(portsDetail.getCountry())?"de":portsDetail.getCountry());
			model.addAttribute("asinList",asinList);
		}
		model.addAttribute("portsDetail",portsDetail);
		model.addAttribute("amazonPortsDetail",portsDetail);
		if(StringUtils.isNotBlank(asin)){
			List<String> reviewList=eventService.findReviewLink(asin,country);
			model.addAttribute("reviewList",reviewList);
		}
		if(portsDetail!=null&&portsDetail.getQueryTime()!=null){
			Map<String,String> changeMap=amazonPostsFeedService.findLatestPosts(portsDetail);
			model.addAttribute("changeMap", changeMap);
			//List<AmazonPostsFeed> changeList=amazonPostsFeedService.findBeforePosts(DateUtils.addDays(portsDetail.getQueryTime(),-1),portsDetail);
			//model.addAttribute("changeList",changeList);
		}
		
		return "modules/amazoninfo/amazonChildPostsDetail";
	}
	
	@RequestMapping(value = {"exportParentModelExcel"})
	public String exportParentModelExcel(String country,String asin,Date date, HttpServletRequest request, HttpServletResponse response, Model model) {
		AmazonPostsDetail detail=amazonPostsDetailService.getDetailByAsinAndCountry(country, asin);//List<AmazonPostsDetail> children
		List<AmazonPostsDetail> children=Lists.newArrayList();
		detail.setParentage("parent");
		String flag="Size-Color";
		for (AmazonPostsDetail portsDetail : detail.getChildren()) {
			
			List<AmazonCatalogRank> rankItems=portsDetail.getRankItems();
			if(rankItems!=null){
				List<String> tempItems=Lists.newArrayList();
				if(rankItems!=null&&rankItems.size()>0){
					for (AmazonCatalogRank rank : rankItems) {
						if(!rank.getCatalog().contains("_on_website")){
							tempItems.add(rank.getCatalog());
						}
					}
				}
				if(tempItems!=null&&tempItems.size()>0){
					portsDetail.setCatalog1(tempItems.get(0));
					if(tempItems.size()>1){
						portsDetail.setCatalog2(tempItems.get(1));
					}
				}
			}
			if("ca".equals(country)){
				portsDetail.setProductIdType("EAN");
				if(StringUtils.isBlank(portsDetail.getEan())){
					portsDetail.setProductIdType("ASIN");
					portsDetail.setEan(portsDetail.getAsin());
				}
				portsDetail.setCurrency("CAD");
				portsDetail.setConditionType("New");
				portsDetail.setProductTaxCode("A_GEN_TAX");
				portsDetail.setFulfillmentCenterId("AMAZON_NA");
				portsDetail.setUnit("cm");
			}else if("de,es,it,fr".contains(country)){
				portsDetail.setProductIdType("EAN");
				if(StringUtils.isBlank(portsDetail.getEan())){
					portsDetail.setProductIdType("ASIN");
					portsDetail.setEan(portsDetail.getAsin());
				}
				portsDetail.setCurrency("EUR");
				portsDetail.setConditionType("New");
				portsDetail.setProductTaxCode("cn");
				portsDetail.setFulfillmentCenterId("AMAZON_EU");
				portsDetail.setUnit("CM");
			}else if("jp".equals(country)){
				portsDetail.setProductIdType("ASIN");
				portsDetail.setCurrency("JPY");
				portsDetail.setConditionType("New");
				portsDetail.setFulfillmentCenterId("AMAZON_JP");
			}else if("uk".equals(country)){
				portsDetail.setProductIdType("ASIN");
				portsDetail.setCurrency("GBP");
				portsDetail.setConditionType("New");
				portsDetail.setFulfillmentCenterId("AMAZON_EU");
			}else if("com".equals(country)){
				portsDetail.setProductIdType("EAN");
				if(StringUtils.isBlank(portsDetail.getEan())){
					portsDetail.setProductIdType("ASIN");
					portsDetail.setEan(portsDetail.getAsin());
				}
				portsDetail.setCurrency("USD");
				portsDetail.setConditionType("New");
				portsDetail.setProductTaxCode("A_GEN_NOTAX");
				portsDetail.setFulfillmentCenterId("AMAZON_NA");
			}
			final AmazonAccountConfig  config=amazonAccountConfigService.getByName(detail.getAccountName());
			List<String> rs=Lists.newArrayList();
			try{
		    	String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
				Client client = BaseService.getCxfClient(interfaceUrl);
				Object[] str = new Object[]{Global.getConfig("ws.key"),portsDetail.getSku(),detail.getAccountName()};
				Object[] res = client.invoke("findParentSku", str);
				rs = (List<String>)res[0];
		    }catch(Exception e){
		    	logger.error(config.getAccountName()+" findParentSku："+e.getMessage(), e);
		    }
			
			if(rs!=null&&rs.size()>0){
				if(portsDetail.getParentPortsDetail()==null){
					portsDetail.setParentPortsDetail(new AmazonPostsDetail());
				}
				portsDetail.setParentage("child");
				//portsDetail.setParentSku(portsDetail.getParentPortsDetail().getSku());
				portsDetail.setRelationshipType("variation");
				if("1".equals(portsDetail.getBySize())&&"1".equals(portsDetail.getByColor())){
					portsDetail.setVariationTheme("Size-Color");
					flag="Size-Color";
				}else if("1".equals(portsDetail.getBySize())){
					portsDetail.setVariationTheme("Size");
					flag="Size";
				}else if("1".equals(portsDetail.getByColor())){
					portsDetail.setVariationTheme("Color");
					flag="Color";
				}
				portsDetail.setParentSku(rs.get(0));
				portsDetail.setSize(rs.get(1));
				portsDetail.setColor(rs.get(2));
			}
			
			String[] skuArr=portsDetail.getSku().split(",");
			for (String sku : skuArr) {
				children.add(new AmazonPostsDetail(portsDetail.getParentPortsDetail(),portsDetail.getAsin(),
						portsDetail.getCountry(),portsDetail.getProductName(),portsDetail.getBinding(), portsDetail.getBrand(),
						portsDetail.getLabel(),portsDetail.getManufacturer(),portsDetail.getPublisher(), portsDetail.getStudio(),
						portsDetail.getTitle(),portsDetail.getPackageQuantity(),portsDetail.getPackageHeight(),
						portsDetail.getPackageLength(), portsDetail.getPackageWidth(),portsDetail.getPackageWeight(),
						portsDetail.getProductGroup(), portsDetail.getProductTypeName(),portsDetail.getFeature1(),
						portsDetail.getFeature2(),portsDetail.getFeature3(),portsDetail.getFeature4(),portsDetail.getFeature5(),
						portsDetail.getSize(),portsDetail.getColor(),portsDetail.getPartNumber(),
						portsDetail.getEan(),sku,
						portsDetail.getDescription(),portsDetail.getKeyword1(), portsDetail.getKeyword2(),
						portsDetail.getKeyword3(),portsDetail.getKeyword4(),portsDetail.getKeyword5(),portsDetail.getCatalog1(),
						portsDetail.getCatalog2(), portsDetail.getParentage(),portsDetail.getParentSku(),
						portsDetail.getRelationshipType(),portsDetail.getVariationTheme(),
						portsDetail.getProductIdType(),portsDetail.getCurrency(),portsDetail.getConditionType(),
						portsDetail.getProductTaxCode(),portsDetail.getFulfillmentCenterId(), portsDetail.getUnit()));
			}
		}
		detail.setChildren(children);
		detail.setVariationTheme(flag);
		
		//下载excel文档
		ExportTransportExcel ete = new ExportTransportExcel();
		Workbook workbook = null;
		String modelName = "posts/POSTS-"+StringUtils.upperCase(("com".equals(country)?"us":country));//模板文件名称
		String xmlName = "posts/PARENT-POSTS-"+StringUtils.upperCase(("com".equals(country)?"us":country));//模板文件名称
		workbook = ete.writeData(detail, xmlName,modelName, 0);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		String fileName = modelName + sdf.format(new Date()) + ".xlsx";
		try {
			//fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition","attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			workbook.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value = {"exportModelExcel"})
	public String exportModelExcel(String productName,String country,String asin,Date date, HttpServletRequest request, HttpServletResponse response, Model model) {
		//productName = HtmlUtils.htmlUnescape(productName);
		//AmazonPostsDetail portsDetail=amazonPostsDetailService.getDetailByProductName(productName,country,asin,date);
		AmazonPostsDetail portsDetail=amazonPostsDetailService.getDetailByAsinAndCountry(country, asin);
		if(portsDetail!=null){
			List<AmazonPostsDetail> children=Lists.newArrayList();
			List<AmazonCatalogRank> rankItems=portsDetail.getRankItems();
			if(rankItems!=null){
				List<String> tempItems=Lists.newArrayList();
				if(rankItems!=null&&rankItems.size()>0){
					for (AmazonCatalogRank rank : rankItems) {
						if(!rank.getCatalog().contains("_on_website")){
							tempItems.add(rank.getCatalog());
						}
					}
				}
				if(tempItems!=null&&tempItems.size()>0){
					portsDetail.setCatalog1(tempItems.get(0));
					if(tempItems.size()>1){
						portsDetail.setCatalog2(tempItems.get(1));
					}
				}
			}
			
			
			if("ca".equals(country)){
				portsDetail.setProductIdType("EAN");
				if(StringUtils.isBlank(portsDetail.getEan())){
					portsDetail.setProductIdType("ASIN");
					portsDetail.setEan(portsDetail.getAsin());
				}
				portsDetail.setCurrency("CAD");
				portsDetail.setConditionType("New");
				portsDetail.setProductTaxCode("A_GEN_TAX");
				portsDetail.setFulfillmentCenterId("AMAZON_NA");
				portsDetail.setUnit("cm");
			}else if("de,es,it,fr".contains(country)){
				portsDetail.setProductIdType("EAN");
				if(StringUtils.isBlank(portsDetail.getEan())){
					portsDetail.setProductIdType("ASIN");
					portsDetail.setEan(portsDetail.getAsin());
				}
				portsDetail.setCurrency("EUR");
				portsDetail.setConditionType("New");
				portsDetail.setProductTaxCode("cn");
				portsDetail.setFulfillmentCenterId("AMAZON_EU");
				portsDetail.setUnit("CM");
			}else if("jp".equals(country)){
				portsDetail.setProductIdType("ASIN");
				portsDetail.setCurrency("JPY");
				portsDetail.setConditionType("New");
				portsDetail.setFulfillmentCenterId("AMAZON_JP");
			}else if("uk".equals(country)){
				portsDetail.setProductIdType("ASIN");
				portsDetail.setCurrency("GBP");
				portsDetail.setConditionType("New");
				portsDetail.setFulfillmentCenterId("AMAZON_EU");
			}else if("com".equals(country)){
				portsDetail.setProductIdType("EAN");
				if(StringUtils.isBlank(portsDetail.getEan())){
					portsDetail.setProductIdType("ASIN");
					portsDetail.setEan(portsDetail.getAsin());
				}
				portsDetail.setCurrency("USD");
				portsDetail.setConditionType("New");
				portsDetail.setProductTaxCode("A_GEN_NOTAX");
				portsDetail.setFulfillmentCenterId("AMAZON_NA");
			}
			final AmazonAccountConfig  config=amazonAccountConfigService.getByName(portsDetail.getAccountName());
			List<String> rs=Lists.newArrayList();
			try{
		    	String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
				Client client = BaseService.getCxfClient(interfaceUrl);
				Object[] str = new Object[]{Global.getConfig("ws.key"),portsDetail.getSku(),portsDetail.getAccountName()};
				Object[] res = client.invoke("findParentSku", str);
				rs = (List<String>)res[0];
		    }catch(Exception e){
		    	logger.error(config.getAccountName()+" findParentSku："+e.getMessage(), e);
		    }
			if(rs!=null&&rs.size()>0){
				if(portsDetail.getParentPortsDetail()==null){
					portsDetail.setParentPortsDetail(new AmazonPostsDetail());
				}
				portsDetail.setParentage("child");
				//portsDetail.setParentSku(portsDetail.getParentPortsDetail().getSku());
				portsDetail.setRelationshipType("variation");
				if("1".equals(portsDetail.getBySize())&&"1".equals(portsDetail.getByColor())){
					portsDetail.setVariationTheme("Size-Color");
				}else if("1".equals(portsDetail.getBySize())){
					portsDetail.setVariationTheme("Size");
				}else if("1".equals(portsDetail.getByColor())){
					portsDetail.setVariationTheme("Color");
				}
				portsDetail.setParentSku(rs.get(0));
				portsDetail.setSize(rs.get(1));
				portsDetail.setColor(rs.get(2));
			}
			String[] skuArr=portsDetail.getSku().split(",");
			
			for (String sku : skuArr) {
				children.add(new AmazonPostsDetail(portsDetail.getParentPortsDetail(),portsDetail.getAsin(),
						portsDetail.getCountry(),portsDetail.getProductName(),portsDetail.getBinding(), portsDetail.getBrand(),
						portsDetail.getLabel(),portsDetail.getManufacturer(),portsDetail.getPublisher(), portsDetail.getStudio(),
						portsDetail.getTitle(),portsDetail.getPackageQuantity(),portsDetail.getPackageHeight(),
						portsDetail.getPackageLength(), portsDetail.getPackageWidth(),portsDetail.getPackageWeight(),
						portsDetail.getProductGroup(), portsDetail.getProductTypeName(),portsDetail.getFeature1(),
						portsDetail.getFeature2(),portsDetail.getFeature3(),portsDetail.getFeature4(),portsDetail.getFeature5(),
						portsDetail.getSize(),portsDetail.getColor(),portsDetail.getPartNumber(),
						portsDetail.getEan(),sku,
						portsDetail.getDescription(),portsDetail.getKeyword1(), portsDetail.getKeyword2(),
						portsDetail.getKeyword3(),portsDetail.getKeyword4(),portsDetail.getKeyword5(),portsDetail.getCatalog1(),
						portsDetail.getCatalog2(), portsDetail.getParentage(),portsDetail.getParentSku(),
						portsDetail.getRelationshipType(),portsDetail.getVariationTheme(),
						portsDetail.getProductIdType(),portsDetail.getCurrency(),portsDetail.getConditionType(),
						portsDetail.getProductTaxCode(),portsDetail.getFulfillmentCenterId(), portsDetail.getUnit()));
				
			}
			portsDetail.setChildren(children);
			
			//下载excel文档
			ExportTransportExcel ete = new ExportTransportExcel();
			Workbook workbook = null;
			String modelName = "posts/POSTS-"+StringUtils.upperCase(("com".equals(country)?"us":country));//模板文件名称
			workbook = ete.writeData(portsDetail, modelName,modelName, 0);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = modelName + sdf.format(new Date()) + ".xlsx";
			try {
				//fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition","attachment;filename=" + fileName);
				OutputStream out = response.getOutputStream();
				workbook.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@RequestMapping(value = {"view"})
	public String view(AmazonPostsDetail amazonPortsDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		AmazonPostsDetail portsDetail=amazonPostsDetailService.get(amazonPortsDetail.getId());
		List<String> asinList=amazonProduct2Service.getAllAsin(portsDetail.getProductName(),portsDetail.getCountry());
		Set<String> catalog=Sets.newHashSet();
		for (AmazonCatalogRank rank : portsDetail.getRankItems()) {
			catalog.add(rank.getCatalog());
		}
		if(catalog!=null&&catalog.size()>0){
			Map<String,Integer> map=amazonPostsDetailService.getRank(catalog,portsDetail.getQueryTime(),portsDetail.getProductName(),portsDetail.getCountry());
			model.addAttribute("map",map);
		}
		model.addAttribute("portsDetail",portsDetail);
		model.addAttribute("amazonPortsDetail",portsDetail);
		model.addAttribute("asinList",asinList);
		return "modules/amazoninfo/amazonChildPostsDetail";
	}
	
	@RequestMapping(value = "getAsin")
	@ResponseBody
	public List<String> getEventType(String country,String productName){
		try {
			productName=URLDecoder.decode(productName, "utf-8");
			List<String> asinList=amazonProduct2Service.getAllAsin(productName,country);
			return asinList;
		} catch (UnsupportedEncodingException e) {}
		return null;
	}
	
	@RequestMapping(value = {"queryPortsDetail"})
	public String queryPortsDetail(AmazonPostsDetail amazonPortsDetail, Model model){
		AmazonPostsDetail queryPortsDetail=new AmazonPostsDetail();
		List<String> asinList=amazonProduct2Service.getAllAsin(amazonPortsDetail.getProductName(),amazonPortsDetail.getCountry());
		model.addAttribute("asinList",asinList);
		if(StringUtils.isNotBlank(amazonPortsDetail.getAsin())){
			queryPortsDetail=amazonPostsDetailService.getPortsDetail(amazonPortsDetail);
			Set<String> catalog=Sets.newHashSet();
			if(queryPortsDetail!=null){
				for (AmazonCatalogRank rank : queryPortsDetail.getRankItems()) {
					catalog.add(rank.getCatalog());
				}
				if(catalog!=null&&catalog.size()>0){
					Map<String,Integer> map=amazonPostsDetailService.getRank(catalog,amazonPortsDetail.getQueryTime(),queryPortsDetail.getProductName(),queryPortsDetail.getCountry());
					model.addAttribute("map",map);
				}
			}
		}else if(StringUtils.isBlank(amazonPortsDetail.getAsin())&&asinList!=null&&asinList.size()>0){
			amazonPortsDetail.setAsin(asinList.get(0));
			queryPortsDetail=amazonPostsDetailService.getPortsDetail(amazonPortsDetail);
			Set<String> catalog=Sets.newHashSet();
			if(queryPortsDetail!=null){
				for (AmazonCatalogRank rank : queryPortsDetail.getRankItems()) {
					catalog.add(rank.getCatalog());
				}
				if(catalog!=null&&catalog.size()>0){
					Map<String,Integer> map=amazonPostsDetailService.getRank(catalog,amazonPortsDetail.getQueryTime(),queryPortsDetail.getProductName(),queryPortsDetail.getCountry());
					model.addAttribute("map",map);
				}
			}
		}else{
			queryPortsDetail.setProductName(amazonPortsDetail.getProductName());
			queryPortsDetail.setAsin("");
			queryPortsDetail.setQueryTime(new Date());
		}
		model.addAttribute("portsDetail",queryPortsDetail);
		model.addAttribute("amazonPortsDetail",amazonPortsDetail);
		if(StringUtils.isNotBlank(amazonPortsDetail.getAsin())){
			List<String> reviewList=eventService.findReviewLink(amazonPortsDetail.getAsin(),amazonPortsDetail.getCountry());
			model.addAttribute("reviewList",reviewList);
		}
		if(queryPortsDetail!=null&&queryPortsDetail.getQueryTime()!=null){
			Map<String,String> changeMap=amazonPostsFeedService.findLatestPosts(queryPortsDetail);
			model.addAttribute("changeMap", changeMap);
			//List<AmazonPostsFeed> changeList=amazonPostsFeedService.findBeforePosts(DateUtils.addDays(queryPortsDetail.getQueryTime(),-1),queryPortsDetail);
			//model.addAttribute("changeList",changeList);
		}
		
		return "modules/amazoninfo/amazonChildPostsDetail";
	}
	
	//帖子编辑
	private   String submit(final AmazonPostsFeed amazonPostsFeed,final AmazonAccountConfig  config){
		String rs = "正在发到服务器，请等待结果。。。";
		Hibernate.initialize(amazonPostsFeed.getItems());
		try {
			amazonPostsFeed.setCreateUser(UserUtils.getUser());
			amazonPostsFeed.setCreateDate(new Date());
			amazonPostsFeed.setState("1");
			amazonPostsFeedService.save(amazonPostsFeed);
			if("1".equals(amazonPostsFeed.getOperateType())){//
				 for (AmazonPostsChange item : amazonPostsFeed.getItems()) {
					 amazonPostsDetailService.updateEanIsUse(item.getEan(),item.getProductName(),amazonPostsFeed.getCountry(),config.getAccountName());
				 }
			}
			new Thread(){
				public void run() {
					    String country = config.getCountry();
					    try{
					    	String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
							Client client = BaseService.getCxfClient(interfaceUrl);
							Object[] str = new Object[]{Global.getConfig("ws.key"),amazonPostsFeed.getId(),config.getAccountName()};
							Object[] res = client.invoke("submitPosts", str);
							List<String> rs = (List<String>)res[0];
							
							if(rs!=null&&rs.size()>1){
								String dateStr = rs.get(0);
								String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/postsChange/";
								File dir = new File(ctxPath+dateStr);
								dir.mkdirs();
								rs.remove(0);
								for (String temp: rs) {
									 if(temp.contains(";;;")){
										  String[] arr = temp.split(";;;");
										  File result =  new File(dir,arr[0]+".xml");
										  PrintStream ps = new PrintStream(new FileOutputStream(result));  
								          ps.println(arr[1]);
								          ps.close();
									 }
								}
							}
					    }catch(Exception e){
					    	logger.error(config.getAccountName()+"编辑帖子出错："+e.getMessage(), e);
					    }
						try{
							if("0".equals(amazonPostsFeed.getOperateType())){
								Map<String, String> asinDesMap=Maps.newHashMap();
								for (AmazonPostsChange item : amazonPostsFeed.getItems()) {
									if(StringUtils.isNotBlank(item.getAsin())&&StringUtils.isNotBlank(item.getFeature1())){
										String info="<ul><li>"+item.getFeature1()+"</li>"+(StringUtils.isBlank(item.getFeature2())?"":"<li>"+item.getFeature2()+"</li>")
												+(StringUtils.isBlank(item.getFeature3())?"":"<li>"+item.getFeature3()+"</li>")
												+(StringUtils.isBlank(item.getFeature4())?"":"<li>"+item.getFeature4()+"</li>")
												+(StringUtils.isBlank(item.getFeature5())?"":"<li>"+item.getFeature5()+"</li>")+"</ul>";
										logger.info("官网描述更新"+country+"=="+item.getAsin()+"=="+info);
										asinDesMap.put(item.getAsin(),info);
									}
								}
								if(asinDesMap!=null&&asinDesMap.size()>0){
									MagentoClientService.catalogProductDescriptionUpdate(country, asinDesMap);
								}
							}
						}catch(Exception e){
							logger.warn("官网描述更新"+e.getMessage(), e);
						}
				};
			}.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rs = amazonPostsFeed.getId()+"编辑帖子出错了!";
		}
		return rs;
	}
	
	@RequestMapping("/download")   
    public ModelAndView download(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/postsChange/";  
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
	
	@RequestMapping("/downloadRelation")   
    public ModelAndView downloadRelation(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/postsRelationChange/";  
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
	
	//编辑帖子关系
	private   String submit(final AmazonPostsRelationshipFeed amazonPostsRelationshipFeed,final File dir,final AmazonAccountConfig  config){
		String rs = "正在发到服务器，请等待结果。。。";
		Hibernate.initialize(amazonPostsRelationshipFeed.getItems());
		try {
			if(amazonPostsRelationshipFeed.getId()==null){
				amazonPostsRelationshipFeed.setCreateUser(UserUtils.getUser());
				amazonPostsRelationshipFeed.setCreateDate(new Date());
			}
			amazonPostsRelationshipFeed.setState("1");
			amazonPostsFeedService.save(amazonPostsRelationshipFeed);
			new Thread(){
				public void run() {
					try{
				    	String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
						Client client = BaseService.getCxfClient(interfaceUrl);
						Object[] str = new Object[]{Global.getConfig("ws.key"),amazonPostsRelationshipFeed.getId(),config.getAccountName()};
						Object[] res = client.invoke("submitChangeRelation", str);
						List<String> rs = (List<String>)res[0];
						
						if(rs!=null&&rs.size()>1){
							String dateStr = rs.get(0);
							String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/postsRelationChange/";
							File dir = new File(ctxPath+dateStr);
							dir.mkdirs();
							rs.remove(0);
							for (String temp: rs) {
								 if(temp.contains(";;;")){
									  String[] arr = temp.split(";;;");
									  File result =  new File(dir,arr[0]+".xml");
									  PrintStream ps = new PrintStream(new FileOutputStream(result));  
							          ps.println(arr[1]);
							          ps.close();
								 }
							}
						}
				    }catch(Exception e){
				    	logger.error(config.getAccountName()+"编辑帖子关系出错："+e.getMessage(), e);
				    }
				};
			}.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rs = amazonPostsRelationshipFeed.getId()+"帖子关系出错了!";
		}
		return rs;
	}
	
	
	

	@RequiresPermissions("amazoninfo:posts:view")
	@RequestMapping(value = {"formRelation"})
	public String formRelation(String type,AmazonPostsDetail amazonPostsDetail,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountry();
		model.addAttribute("accountMap", accountMap);
		
		if(StringUtils.isNotEmpty(amazonPostsDetail.getAccountName())){
			List<String> list=amazonPostsDetailService.getParentSku(amazonPostsDetail.getAccountName());
			List<AmazonPostsDetail> childList=Lists.newArrayList();
			if(StringUtils.isBlank(type)||"2".equals(type)){
				childList=amazonPostsDetailService.getChildSku(amazonPostsDetail.getAccountName());
			}else{
				childList=amazonPostsDetailService.getChildSku2(amazonPostsDetail.getAccountName());
			}
			for (Map.Entry<String,List<String>> entry: accountMap.entrySet()) {
				List<String> accountList=entry.getValue();
				boolean breakFlag=false;
				for (String account: accountList) {
					if(account.equals(amazonPostsDetail.getAccountName())){
						amazonPostsDetail.setCountry(entry.getKey());
						breakFlag=true;
						break;
					}
				}
				if(breakFlag){
					break;
				}
			}
			model.addAttribute("list", list);
			model.addAttribute("childList",childList);
			model.addAttribute("amazonPostsDetail", amazonPostsDetail);
			if(list!=null&&list.size()>0){
				model.addAttribute("sizeOrColor",amazonPostsDetailService.getSizeOrColor(amazonPostsDetail.getAccountName(),list.get(0)));
			}
		}else{
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					amazonPostsDetail.setCountry(dict.getValue());
					amazonPostsDetail.setAccountName(accountMap.get(dict.getValue()).get(0));
					List<String> list=amazonPostsDetailService.getParentSku(amazonPostsDetail.getAccountName());
					List<AmazonPostsDetail> childList=Lists.newArrayList();
					if(StringUtils.isBlank(type)||"2".equals(type)){
						childList=amazonPostsDetailService.getChildSku(amazonPostsDetail.getAccountName());
					}else{
						childList=amazonPostsDetailService.getChildSku2(amazonPostsDetail.getAccountName());
					}
					model.addAttribute("list", list);
					model.addAttribute("childList",childList);
					model.addAttribute("amazonPostsDetail", amazonPostsDetail);
					if(list!=null&&list.size()>0){
						model.addAttribute("sizeOrColor",amazonPostsDetailService.getSizeOrColor(amazonPostsDetail.getAccountName(),list.get(0)));
					}
					break;
				}
			}
			
			if(StringUtils.isEmpty(amazonPostsDetail.getAccountName())){
				amazonPostsDetail.setCountry("");
				amazonPostsDetail.setAccountName("");
				model.addAttribute("amazonPostsDetail", amazonPostsDetail);
			}
		}
		model.addAttribute("type", type);
		Map<String,List<String>> catalogMap=AmazonPostsChange.catalogTypeMap;
		model.addAttribute("catalogMap", catalogMap);
		List<User> userList = systemService.findUserByPermission("amazoninfo:feedSubmission:");
		model.addAttribute("allUser", userList);
		
		return "modules/amazoninfo/postsRelationFeedForm";
	}
	
	@RequiresPermissions("amazoninfo:posts:view")
	@RequestMapping(value = {"saveRelation"})
	public String saveRelation(AmazonPostsRelationshipFeed amazonPostsRelationshipFeed, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(amazonPostsRelationshipFeed!=null&&amazonPostsRelationshipFeed.getId()!=null&&amazonPostsRelationshipFeed.getId()>0){
			amazonPostsRelationshipFeed=amazonPostsFeedService.getAmazonPostsRelationshipFeed(amazonPostsRelationshipFeed.getId());
		}
		if(amazonPostsRelationshipFeed!=null){
			Map<String,String> nameMap=amazonProduct2Service.findNameBySkuByAccount(amazonPostsRelationshipFeed.getAccountName());
			final AmazonAccountConfig  config=amazonAccountConfigService.getByName(amazonPostsRelationshipFeed.getAccountName());
			amazonPostsRelationshipFeed.setCountry(config.getCountry());
			for (AmazonPostsRelationshipChange item : amazonPostsRelationshipFeed.getItems()) {
				if(nameMap!=null&&nameMap.get(item.getSku())!=null){
					item.setProductName(nameMap.get(item.getSku()));
				}
				item.setAmazonPostsRelationshipFeed(amazonPostsRelationshipFeed);
			}
			if("0".equals(amazonPostsRelationshipFeed.getState())){//待审核
				if(amazonPostsRelationshipFeed.getId()==null){
					amazonPostsRelationshipFeed.setCreateUser(UserUtils.getUser());
					amazonPostsRelationshipFeed.setCreateDate(new Date());
				}
				amazonPostsRelationshipFeed.setState("0");
				amazonPostsFeedService.save(amazonPostsRelationshipFeed);
				sendCheckEmail(amazonPostsRelationshipFeed);
			}else{
				String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/postsRelationChange/";
				String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
				amazonPostsRelationshipFeed.setResultFile(dateStr);
				File dir = new File(ctxPath+dateStr);
				dir.mkdirs();
				submit(amazonPostsRelationshipFeed,dir,config);
			}
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonPortsDetail/postsRelationList/";
	}
	
	private  boolean sendCheckEmail(AmazonPostsRelationshipFeed feed){
		String toAddress=systemService.getUser(feed.getCheckUser().getId()).getEmail();
		StringBuffer content= new StringBuffer("");

		content.append("<p>请审核"+systemService.getUser(feed.getCreateUser().getId()).getName()+"提交的"+("com".equals(feed.getCountry())?"US":feed.getCountry().toUpperCase())+"绑帖   <a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/amazonPortsDetail/postsRelationList?parentSku="+feed.getParentSku()+"&country="+feed.getCountry()+"'>序号:"+feed.getId()+",ParentSku:"+feed.getParentSku()+"</a>  申请</p>");
		content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
		for (AmazonPostsRelationshipChange item:feed.getItems()) {
			content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
			content.append("<td>新绑定父SKU:"+feed.getParentSku()+",SKU:"+item.getSku()+(StringUtils.isNotBlank(item.getSize())?(",Size:"+item.getSize()):"")+(StringUtils.isNotBlank(item.getColor())?(",Color:"+item.getColor()):"")+"</tr>");
		}
		content.append("</table>");
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();   
			final MailInfo mailInfo = new MailInfo(toAddress,"绑帖审核"+feed.getId()+":"+feed.getParentSku()+DateUtils.getDate("-yyyy/MM/dd"),date);
			mailInfo.setContent(content.toString());
			new Thread(){
				public void run(){   
					 mailManager.send(mailInfo);
				}
			}.start();
		}
		return true;
	}
	
	@RequiresPermissions("amazoninfo:posts:view")
	@RequestMapping(value = {"cancelBanding"})
	public String cancelBanding(AmazonPostsRelationshipFeed amazonPostsRelationshipFeed, HttpServletRequest request, HttpServletResponse response, Model model) {
		amazonPostsRelationshipFeed=amazonPostsFeedService.getAmazonPostsRelationshipFeed(amazonPostsRelationshipFeed.getId());
		amazonPostsRelationshipFeed.setState("5");
		amazonPostsRelationshipFeed.setCheckDate(new Date());
		amazonPostsFeedService.save(amazonPostsRelationshipFeed);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonPortsDetail/postsRelationList/";
	}
	
	@RequiresPermissions("amazoninfo:posts:view")
	@RequestMapping(value = {"checkBanding"})
	public String checkBanding(AmazonPostsRelationshipFeed amazonPostsRelationshipFeed, HttpServletRequest request, HttpServletResponse response, Model model) {
		amazonPostsRelationshipFeed=amazonPostsFeedService.getAmazonPostsRelationshipFeed(amazonPostsRelationshipFeed.getId());
		String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/postsRelationChange/";
		String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
		amazonPostsRelationshipFeed.setResultFile(dateStr);
		File dir = new File(ctxPath+dateStr);
		dir.mkdirs();
		amazonPostsRelationshipFeed.setCheckDate(new Date());
		final AmazonAccountConfig  config=amazonAccountConfigService.getByName(amazonPostsRelationshipFeed.getAccountName());
		submit(amazonPostsRelationshipFeed,dir,config);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonPortsDetail/postsRelationList/";
	}
	
	@RequestMapping(value = {"postsRelationList"})
	public String postsRelationList(AmazonPostsRelationshipFeed amazonPostsRelationshipFeed, HttpServletRequest request, HttpServletResponse response, Model model){
		Page<AmazonPostsRelationshipFeed> page = new Page<AmazonPostsRelationshipFeed>(request, response);
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (amazonPostsRelationshipFeed.getCreateDate() == null) {
			amazonPostsRelationshipFeed.setCreateDate(DateUtils.addMonths(today, -1));
		}
		if (amazonPostsRelationshipFeed.getEndDate()== null) {
			amazonPostsRelationshipFeed.setEndDate(today);
		}
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("createDate desc");
		} else {
			page.setOrderBy(orderBy + ",createDate desc");
		}
		
		User user = UserUtils.getUser();
		if (amazonPostsRelationshipFeed.getCreateUser()==null){
			amazonPostsRelationshipFeed.setCreateUser(user);
		} 
		
		page = amazonPostsFeedService.find(page,amazonPostsRelationshipFeed);
		page.setOrderBy(orderBy);
		model.addAttribute("page", page);
		model.addAttribute("amazonPostsRelationshipFeed", amazonPostsRelationshipFeed);
		model.addAttribute("cuser",user);
		return "modules/amazoninfo/postsRelationList";
	}
	
	@RequestMapping(value = "getChangeDetail")
	@ResponseBody
	public Map<String,Object> getChangeDetail(String accountName,String sku){
		final AmazonAccountConfig  config=amazonAccountConfigService.getByName(accountName);
		
		List<String> rs=Lists.newArrayList();
		try{
	    	String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
			Client client = BaseService.getCxfClient(interfaceUrl);
			Object[] str = new Object[]{Global.getConfig("ws.key"),sku,accountName};
			Object[] res = client.invoke("findParentSku", str);
			rs = (List<String>)res[0];
	    }catch(Exception e){
	    	logger.error(config.getAccountName()+" findParentSku："+e.getMessage(), e);
	    }
		
		Map<String,Object> map=Maps.newHashMap();
		if(rs!=null&&rs.size()>0){
			map.put("parentSku",rs.get(0));
			map.put("size", rs.get(1));
			map.put("color",rs.get(2));
		
		}
		return map;
	}
	
	@RequestMapping(value = "getBySizeOrColor")
	@ResponseBody
	public String getBySizeOrColor(String accountName,String sku){
		sku = HtmlUtils.htmlUnescape(sku);
		return amazonPostsDetailService.getSizeOrColor(accountName, sku);
	}
	
	@RequestMapping(value = "findNameByParentSku")
	@ResponseBody
	public List<String> findNameByParentSku(String parentSku,String accountName){
		return amazonPostsFeedService.findNameByParentSku(parentSku,accountName);
	}
	
	
	
	@RequestMapping(value = "getAsinBySku")
	@ResponseBody
	public Map<String,Object> getAsinBySku(String country,String sku){
		String asin=amazonPostsFeedService.getAsinBySku(country,sku);
		Map<String,Object> map=Maps.newHashMap();
		map.put("asin",asin);
		return map;
	}
	

	@RequestMapping(value = "getNewQuantity")
	@ResponseBody
	public Integer getNewQuantity(String country,String sku,Integer wareHouseId){
		return psiInventoryService.getNewQuantity(country, sku, wareHouseId);
	}
	
	@RequestMapping(value = {"deletePostsForm"})
	public String deletePostsForm(AmazonPostsDetail amazonPostsDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountry();
		model.addAttribute("accountMap", accountMap);
		
		if(StringUtils.isNotEmpty(amazonPostsDetail.getAccountName())){
			List<AmazonPostsDetail> list=amazonPostsDetailService.getAllProductNameList(amazonPostsDetail.getAccountName());
			model.addAttribute("list", list);
			model.addAttribute("amazonPostsDetail", amazonPostsDetail);
		}else{
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					amazonPostsDetail.setAccountName(accountMap.get(dict.getValue()).get(0));
					List<AmazonPostsDetail> list=amazonPostsDetailService.getAllProductNameList(amazonPostsDetail.getAccountName());
					model.addAttribute("list", list);
					model.addAttribute("amazonPostsDetail", amazonPostsDetail);
					break;
				}
			}
			
			if(StringUtils.isEmpty(amazonPostsDetail.getCountry())){
				amazonPostsDetail.setCountry("");
				amazonPostsDetail.setAccountName("");
				model.addAttribute("amazonPostsDetail", amazonPostsDetail);
			}
		}
		return "modules/amazoninfo/amazonPostsDeleteForm";
	}
	
	
	//addParentsPostFrom
	@RequestMapping(value = {"addParentsPostFrom"})
	public String addParentsPostFrom(AmazonPostsDetail amazonPostsDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountry();
		model.addAttribute("accountMap", accountMap);
		if(StringUtils.isNotEmpty(amazonPostsDetail.getAccountName())){
			List<String> parentList=amazonPostsDetailService.getParentSku(amazonPostsDetail.getAccountName());
			model.addAttribute("parentList", parentList);
			model.addAttribute("amazonPostsDetail", amazonPostsDetail);
		}else{
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					amazonPostsDetail.setAccountName(accountMap.get(dict.getValue()).get(0));
					List<String> parentList=amazonPostsDetailService.getParentSku(amazonPostsDetail.getAccountName());
					model.addAttribute("parentList", parentList);
					model.addAttribute("amazonPostsDetail", amazonPostsDetail);
					break;
				}
			}
			if(StringUtils.isEmpty(amazonPostsDetail.getAccountName())){
				amazonPostsDetail.setAccountName("");
				model.addAttribute("amazonPostsDetail", amazonPostsDetail);
			}
		}
		Map<String,List<String>> catalogMap=AmazonPostsChange.catalogTypeMap;
		model.addAttribute("catalogMap", catalogMap);
		
		return "modules/amazoninfo/amazonParentPostsAdd";
	}
	
	@RequestMapping(value = {"addPostFrom"})
	public String addPostFrom(String addType,AmazonPostsDetail amazonPostsDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String,List<String>> catalogMap=AmazonPostsChange.catalogTypeMap;
		model.addAttribute("catalogMap", catalogMap);
		Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountry();
		model.addAttribute("accountMap", accountMap);
		if(StringUtils.isNotEmpty(amazonPostsDetail.getAccountName())){
			if("3".equals(addType)){
				List<AmazonPostsDetail>	list= amazonPostsDetailService.getProductNameList(amazonPostsDetail.getAccountName());
				model.addAttribute("list", list);
		    }else{
		    	List<AmazonPostsDetail>	list= amazonPostsDetailService.getProductNameList1(amazonPostsDetail.getAccountName());
				model.addAttribute("list", list);
		    }
			
			model.addAttribute("amazonPostsDetail", amazonPostsDetail);
			for (Map.Entry<String,List<String>> entry: accountMap.entrySet()) {
				List<String> accountList=entry.getValue();
				boolean flag=false;
				for (String account: accountList) {
					if(account.equals(amazonPostsDetail.getAccountName())){
						amazonPostsDetail.setCountry(entry.getKey());
						flag=true;
						break;
					}
				}
				if(flag){
					break;
				}
			}
		}else{
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					amazonPostsDetail.setAccountName(accountMap.get(dict.getValue()).get(0));
					amazonPostsDetail.setCountry(dict.getValue());
					model.addAttribute("amazonPostsDetail", amazonPostsDetail);
					if("3".equals(addType)){
						List<AmazonPostsDetail>	list= amazonPostsDetailService.getProductNameList(amazonPostsDetail.getAccountName());
						model.addAttribute("list", list);
				    }else{
				    	List<AmazonPostsDetail>	list= amazonPostsDetailService.getProductNameList1(amazonPostsDetail.getAccountName());
						model.addAttribute("list", list);
				    }
					break;
				}
			}
			
			if(StringUtils.isEmpty(amazonPostsDetail.getAccountName())){
				amazonPostsDetail.setAccountName("");
				model.addAttribute("amazonPostsDetail", amazonPostsDetail);
			}
		}
		if(StringUtils.isNotBlank(amazonPostsDetail.getAccountName())){
			model.addAttribute("skuType",amazonAccountConfigService.getByName(amazonPostsDetail.getAccountName()).getSkuIndex());
		}
		
		
		model.addAttribute("addType", addType);
		String suffix=amazonPostsDetail.getAccountName().split("_")[0];
		if("2".equals(addType)){
			if(amazonPostsDetail.getCountry()!=null&&"uk,de,es,it,fr".contains(amazonPostsDetail.getCountry())){
				Set<String> euCountry=Sets.newHashSet("uk","de","es","it","fr");
				List<AmazonProduct2> amazonProductList=amazonProduct2Service.getAmazonProduct(euCountry,suffix);
				model.addAttribute("amazonProductList", amazonProductList);
			}
			return "modules/amazoninfo/amazonPostsCopyAddForm";
		}else if("3".equals(addType)){
			if(StringUtils.isNotBlank(amazonPostsDetail.getAccountName())){
				Set<String> euCountry=Sets.newHashSet("uk","de","es","it","fr");
				List<AmazonPostsDetail> allSku=amazonPostsDetailService.getAllSkuList(euCountry,suffix);
				model.addAttribute("allSku", allSku);
			}
			return "modules/amazoninfo/amazonPostsCrossAddForm";
		}else if("4".equals(addType)){
			Map<String,Map<String,String>> postsType=amazonProduct2Service.getPostsStatu(amazonPostsDetail.getAccountName());
			model.addAttribute("postsType", postsType);
			return "modules/amazoninfo/amazonPostsIsChangeForm";
		}else if("5".equals(addType)){
			return "modules/amazoninfo/amazonPostsRecoveryForm";
		}else if("8".equals(addType)){
			return "modules/amazoninfo/amazonPostsLocalAddForm";
		}else{//新增普通帖
			if(amazonPostsDetail.getCountry()!=null&&"uk,de,es,it,fr".contains(amazonPostsDetail.getCountry())){
				Set<String> euCountry=Sets.newHashSet("uk","de","es","it","fr");
				List<AmazonProduct2> amazonProductList=amazonProduct2Service.getAmazonProduct(euCountry,suffix);
				model.addAttribute("amazonProductList", amazonProductList);
			}
			model.addAttribute("allProduct", productService.getHasPowerAndCode(amazonPostsDetail.getCountry()));//1：带电
			model.addAttribute("typeCode", amazonPostsDetailService.find());
			return "modules/amazoninfo/amazonPostsAddForm";
		}
	}
	
	@RequestMapping(value = "getSkuByCountry")
	@ResponseBody
	public List<AmazonPostsDetail> getskuByCountry(String accountName){
		return amazonPostsDetailService.getChildSku(accountName);
	}
	
	@RequestMapping(value = "getSkuByCountryAsin")
	@ResponseBody
	public List<AmazonPostsDetail> getSkuByCountryAsin(String accountName,String asin){
		return amazonPostsDetailService.getChildSku(accountName,asin);
	}
	
	
	@RequiresPermissions("amazoninfo:posts:view")
	@RequestMapping(value = {"saveOtherPostsChange"})
	public String saveOtherPostsChange(AmazonPostsFeed amazonPostsFeed, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		    boolean flag=false;
		    final AmazonAccountConfig  config=amazonAccountConfigService.getByName(amazonPostsFeed.getAccountName());
		    amazonPostsFeed.setCountry(config.getCountry());
		    for (AmazonPostsChange item : amazonPostsFeed.getItems()) {
		       if("2".equals(amazonPostsFeed.getOperateType())){//add parent
		    	   if(StringUtils.isBlank(item.getTitle())){
		    		   item.setTitle(item.getSku());
		    	   }
		       }
			   if("8".equals(amazonPostsFeed.getOperateType())&&StringUtils.isNotBlank(item.getTitle())){
					flag=true;
			   }
		       item.setCountry(config.getCountry());
			   item.setAmazonPostsFeed(amazonPostsFeed);
		    }
		    
		    
		    if("8".equals(amazonPostsFeed.getOperateType())){
		    	 if(flag){
						submit(amazonPostsFeed,config);
				    }else{
						addMessage(redirectAttributes, "提交失败,请重新提交");
					}
		    }else{
				submit(amazonPostsFeed,config);
		    }
		    return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonPortsDetail/changePostsList/?country="+amazonPostsFeed.getCountry();
	}
	
	
	@RequestMapping(value = {"ghostPostsChange"})
	public String ghostPostsChange(AmazonPostsFeed amazonPostsFeed, HttpServletRequest request, HttpServletResponse response, Model model) {
		    amazonPostsFeed=amazonPostsFeedService.get(amazonPostsFeed.getId());
		    amazonPostsFeed.setResult(null);
		    final AmazonAccountConfig  config=amazonAccountConfigService.getByName(amazonPostsFeed.getAccountName());
			submit(amazonPostsFeed,config);
		    return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonPortsDetail/changePostsList/?country="+amazonPostsFeed.getCountry();
	}
	
	
	@RequestMapping(value = {"changeRecoveryForm"})
	public String changeRecoveryForm(Integer id, HttpServletRequest request, HttpServletResponse response, Model model) {
		AmazonPostsRelationshipFeed relation=amazonPostsFeedService.getAmazonPostsRelationshipFeed(id);
		List<AmazonPostsDetail> list=Lists.newArrayList();
		for (AmazonPostsRelationshipChange item: relation.getItems()) {
			AmazonPostsDetail detail=amazonPostsDetailService.getDetailBySkuAndCountry(relation.getCountry(), item.getSku());
			if(detail==null){
				if(item.getSku().contains(",")){
					String[] skuArr=item.getSku().split(",");
					for (String arr : skuArr) {
						AmazonPostsDetail detail1=amazonPostsDetailService.getDetailBySkuAndCountry(relation.getCountry(),arr);
						if(detail1==null){
							continue;
						}
						List<AmazonCatalogRank> rank=detail1.getRankItems();
						Collections.sort(rank);
						int index=1;
						if(rank!=null){
							for (int i=1;i<=rank.size();i++) {
								if(!rank.get(i-1).getCatalog().endsWith("_on_website")){
									if(index==1){
										detail1.setCatalog1(rank.get(i-1).getCatalog());
									}else if(index==2){
										detail1.setCatalog2(rank.get(i-1).getCatalog());
									}else{
										break;
									}
									index++;
								}
							}
						}
						Float price=amazonProduct2Service.getProductPriceMaxday(relation.getCountry(),arr);
						if(price!=null){
							detail1.setPrice(price);
							detail1.setSalePrice(price);
						}
						list.add(detail1);
					}
				}
			}else{
				detail.setSku(item.getSku());
				List<AmazonCatalogRank> rank=detail.getRankItems();
				Collections.sort(rank);
				int index=1;
				if(rank!=null){
					for (int i=1;i<=rank.size();i++) {
						if(!rank.get(i-1).getCatalog().endsWith("_on_website")){
							if(index==1){
								detail.setCatalog1(rank.get(i-1).getCatalog());
							}else if(index==2){
								detail.setCatalog2(rank.get(i-1).getCatalog());
							}else{
								break;
							}
							index++;
						}
					}
				}
				Float price=amazonProduct2Service.getProductPriceMaxday(relation.getCountry(), item.getSku());
				if(price!=null){
					detail.setPrice(price);
					detail.setSalePrice(price);
				}
				list.add(detail);
			}
		}
		model.addAttribute("list", list);
		model.addAttribute("amazonPostsDetail", relation);
		return "modules/amazoninfo/amazonPostsRecoveryForm2";
	}
	
	
	@RequestMapping(value = "amazonPostAddEdit")
	public String amazonPostAddEdit(AmazonPostsFeed amazonPostsFeed, Model model) {
		amazonPostsFeed=amazonPostsFeedService.get(amazonPostsFeed.getId());
		model.addAttribute("amazonPostsFeed", amazonPostsFeed);
		Map<String,List<String>> catalogMap=AmazonPostsChange.catalogTypeMap;
		model.addAttribute("catalogMap", catalogMap);
		Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountry();
		model.addAttribute("accountMap", accountMap);
		
		return "modules/amazoninfo/amazonPostsAddEdit";
	}
	
	@ResponseBody
	@RequestMapping(value = "getCatalogByCountry")
	public List<AmazonProductCatalog> getCatalogByCountry(String country, Model model){
		List<AmazonProductCatalog> catalogList= amazonProductCatalogService.findAllCatalogByCountry(country);
		return catalogList;
	}
	
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String country,HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		//List<Menu> list=UserUtils.getMenuList();
		List<AmazonProductCatalog> catalogList=amazonProductCatalogService.findAllCatalogByCountry(country);
		for (int i=0; i<catalogList.size(); i++){
			    AmazonProductCatalog e = catalogList.get(i);
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParent()!=null?e.getParent().getId():null);
				map.put("name",e.getCatalogName());
				if("com".equals(country)){
					map.put("title",e.getItemType());
				}else{
					map.put("title",e.getPathId());
				}
				
				mapList.add(map);
		}
		return mapList;
	}
	
	@ResponseBody
	@RequestMapping(value = "treeData2")
	public List<Map<String, Object>> treeData2(String country,HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<AmazonProductCatalog> catalogList=amazonProductCatalogService.findAllCatalogByCountry(country);
		for (int i=0; i<catalogList.size(); i++){
			    AmazonProductCatalog e = catalogList.get(i);
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParent()!=null?e.getParent().getId():null);
				map.put("name",e.getCatalogName());
				map.put("title",e.getPathId());
				mapList.add(map);
		}
		return mapList;
	}
	
	@RequestMapping(value = "findNewReleasesRank")
	public String  findNewReleasesRank(AmazonNewReleasesRank amazonNewReleasesRank, Model model){
		if(StringUtils.isBlank(amazonNewReleasesRank.getCountry())){
			amazonNewReleasesRank.setCountry("de");
		}
		if(amazonNewReleasesRank.getQueryTime()==null){
			Date date=new Date();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			amazonNewReleasesRank.setQueryTime(DateUtils.addDays(date,-15));
			amazonNewReleasesRank.setEndTime(date);
		}
		Map<String,Map<String,Map<String,AmazonNewReleasesRank>>>  rankMap=amazonPostsDetailService.findNewReleasesRank(amazonNewReleasesRank);
		model.addAttribute("rankMap", rankMap);
		Date start = amazonNewReleasesRank.getQueryTime();
		Date end = amazonNewReleasesRank.getEndTime();
		DateFormat formatDay = new SimpleDateFormat("yyyyMMdd");
		List<String> dateList=Lists.newArrayList();
		while(end.after(start)||end.equals(start)){
			String key = formatDay.format(start);
			dateList.add(key);
			start = DateUtils.addDays(start, 1);
		}
		Collections.reverse(dateList);
		model.addAttribute("dateList", dateList);
		return "modules/amazoninfo/amazonNewReleasesRank";
	}
	
	
	@RequestMapping(value = "findEanList")
	public String findEanList(AmazonEan amazonEan,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes,Model model) {
		if(StringUtils.isBlank(amazonEan.getActive())){
			amazonEan.setActive("2");
	    }
        Page<AmazonEan> page = amazonPostsDetailService.find(new Page<AmazonEan>(request, response), amazonEan); 
       
        model.addAttribute("page", page);
        model.addAttribute("amazonEan", amazonEan);
		return "modules/amazoninfo/amazonEanList";
	}
	
	@RequestMapping(value = "updateActive")
	public String updateActive(AmazonEan amazonEan){
		amazonPostsDetailService.updateActive(amazonEan);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonPortsDetail/findEanList?";
	}
	
	@RequestMapping(value = "uploadEanFile")
	@ResponseBody
	public String uploadEanFile(@RequestParam("excel")MultipartFile excelFile,String type,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			    Date date=new Date();
				Workbook workBook = WorkbookFactory.create(excelFile.getInputStream());
				Sheet sheet = workBook.getSheetAt(0);
				sheet.setForceFormulaRecalculation(true);
				List<AmazonEan> eanList=Lists.newArrayList();
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null) {
						continue;
					}
					//String ean= df.format(row.getCell(0).getNumericCellValue());  
					String ean=row.getCell(0).getStringCellValue().trim();
					if(ean.length()==13){//69575 99316 613
						//Integer isExistId=amazonPostsDetailService.isExist(ean);
						AmazonEan oldEan=amazonPostsDetailService.findEan(ean);
						String sku="";
						try{
							sku=row.getCell(1).getStringCellValue();
						}catch(Exception e){}
						if(oldEan==null){
							AmazonEan amazonEan=new AmazonEan();
							if(StringUtils.isNotBlank(sku)){//已使用
								amazonEan.setActive("2");
								PsiSku psiSku=productService.getSkuBySku(sku.trim());
								if(psiSku!=null){
									amazonEan.setProductName(psiSku.getNameWithColor());
									amazonEan.setCountry(psiSku.getCountry());
								}
							}else{
								amazonEan.setActive("0");
							}
							amazonEan.setEan(ean);
							amazonEan.setCreateDate(date);
							eanList.add(amazonEan);
						}else{
							if(StringUtils.isNotBlank(sku)){//已使用
								oldEan.setActive("2");
								PsiSku psiSku=productService.getSkuBySku(sku.trim());
								if(psiSku!=null){
									oldEan.setProductName(psiSku.getNameWithColor());
									oldEan.setCountry(psiSku.getCountry());
								}
							}else{
								oldEan.setActive("0");
							}
							eanList.add(oldEan);
						}
						/*if(isExistId==null){
							AmazonEan amazonEan=new AmazonEan();
							amazonEan.setEan(ean);
							amazonEan.setActive("0");
							amazonEan.setCreateDate(date);
							eanList.add(amazonEan);
						}*/
					}
				}	
				if(eanList!=null&&eanList.size()>0){
					amazonPostsDetailService.saveEan(eanList);
				}
				addMessage(redirectAttributes,"文件上传成功");
		} catch (Exception e) {
			addMessage(redirectAttributes,"文件上传失败"+e.getMessage());
			return "1";
		}
		return "0";
	}
	
	@RequestMapping(value = "findProductTypeCodeList")
	public String findProductTypeCodeList(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes,Model model) {
		Map<String,AmazonProductTypeCode> codeMap= amazonPostsDetailService.find(); 
        model.addAttribute("codeMap", codeMap);
        List<Dict> dictsList= DictUtils.getDictList("product_type");
        model.addAttribute("dictsList", dictsList);
		return "modules/amazoninfo/amazonProductTypeCodeList";
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"updateProductTypeCode"})
	public Integer updateProductTypeCode(AmazonProductTypeCode typeCode) throws UnsupportedEncodingException {
		String type=URLDecoder.decode(typeCode.getProductType(),"utf-8");
		type=HtmlUtils.htmlUnescape(type);
	    if(typeCode.getId()==null){
	    	AmazonProductTypeCode code=new AmazonProductTypeCode();
	    	code.setProductType(type);
	    	code.setCode(typeCode.getCode());
	    	amazonPostsDetailService.saveCode(code);
	    	return code.getId();
	    }else{
	    	AmazonProductTypeCode amazonProductTypeCode=amazonPostsDetailService.findCode(typeCode.getId());
	    	amazonProductTypeCode.setCode(typeCode.getCode());
	    	amazonPostsDetailService.saveCode(amazonProductTypeCode);
	    	return typeCode.getId();
	    }
	}
	
	
	@RequestMapping(value = "findProductTypeChargeList")
	public String findProductTypeChargeList(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes,Model model) {
		Map<String,AmazonProductTypeCharge> codeMap= amazonPostsDetailService.findCharge(); 
        model.addAttribute("codeMap", codeMap);
        List<Dict> dictsList= DictUtils.getDictList("product_type");
        model.addAttribute("dictsList", dictsList);
		return "modules/amazoninfo/amazonProductTypeChargeList";
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"updateProductTypeCharge"})
	public Integer updateProductTypeCharge(AmazonProductTypeCharge typeCharge) throws UnsupportedEncodingException {
		String type=URLDecoder.decode(typeCharge.getProductType(),"utf-8");
		type=HtmlUtils.htmlUnescape(type);
		
		Integer returnId=null;
	    if(typeCharge.getId()==null){
	    	AmazonProductTypeCharge code=new AmazonProductTypeCharge();
	    	code.setProductType(type);
	    	code.setCountry(typeCharge.getCountry());
	    	code.setCommissionPcent(typeCharge.getCommissionPcent());
	    	amazonPostsDetailService.saveCommissionPcent(code);
	    	returnId=code.getId();
	    }else{
	    	AmazonProductTypeCharge amazonProductTypeCharge=amazonPostsDetailService.findCommissionPcent(typeCharge.getId());
	    	amazonProductTypeCharge.setCommissionPcent(typeCharge.getCommissionPcent());
	    	amazonPostsDetailService.saveCommissionPcent(amazonProductTypeCharge);
            returnId=typeCharge.getId();
	    }
	    psiProductEliminateService.updateCommission(productService.findProductIdByType(type),typeCharge.getCountry(),typeCharge.getCommissionPcent());
	    return returnId;
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"findEanByProductName"})
	public String findEanByProductName(String productName,String accountName,String type) {
		productName = HtmlUtils.htmlUnescape(productName);
		if("0".equals(type)){
			return amazonPostsDetailService.findEanByProductName(productName,accountName);
		}else{
			return amazonPostsDetailService.findEuEanByProductName(productName,accountName);
		}
	}
	

	@ResponseBody
	@RequestMapping(value = {"updateEanState"})
	public String updateEanState(String ean,String country) {
		Set<String> countrySet=Sets.newHashSet();
		if("euNoUk".equals(country)){
			countrySet=Sets.newHashSet("de","fr","it","es");
		}else if("eu".equals(country)){
			countrySet=Sets.newHashSet("de","fr","it","es","uk");
		}else{
			countrySet=Sets.newHashSet(country);
		}
		amazonPostsDetailService.updateEan(ean,countrySet);
		return "1";
	}
	
	
	
	@RequestMapping(value = "uploadEanRelationshipFile")
	@ResponseBody
	public String uploadEanRelationshipFile(@RequestParam("excelFile")MultipartFile excelFile,String type,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			    Date date=new Date();

				Workbook workBook = WorkbookFactory.create(excelFile.getInputStream());
				Sheet sheet = workBook.getSheetAt(0);
				sheet.setForceFormulaRecalculation(true);
				List<AmazonEan> eanList=Lists.newArrayList();
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null) {
						continue;
					}
					String name=row.getCell(0).getStringCellValue().trim();
					String country=row.getCell(2).getStringCellValue().trim();
					String ean=row.getCell(3).getStringCellValue().trim();
					
					if(ean.length()==13){//69575 99316 613 0:有效 1:失效 2：已使用
						AmazonEan oldEan=amazonPostsDetailService.findEan(ean);
						if(oldEan==null){
							if(country.contains(",")){
								if(country.contains("de")){
									AmazonEan amazonEan=new AmazonEan();
									amazonEan.setProductName(name);
									amazonEan.setActive("2");
									amazonEan.setEan(ean);
									amazonEan.setCreateDate(date);
									amazonEan.setCountry("de");
									eanList.add(amazonEan);
								}
								if(country.contains("jp")){
									AmazonEan amazonEan=new AmazonEan();
									amazonEan.setProductName(name);
									amazonEan.setActive("2");
									amazonEan.setEan(ean);
									amazonEan.setCreateDate(date);
									amazonEan.setCountry("jp");
									eanList.add(amazonEan);
								}
								if(country.contains("ca")){
									AmazonEan amazonEan=new AmazonEan();
									amazonEan.setProductName(name);
									amazonEan.setActive("2");
									amazonEan.setEan(ean);
									amazonEan.setCreateDate(date);
									amazonEan.setCountry("ca");
									eanList.add(amazonEan);
								}
								if(country.contains("com")){
									AmazonEan amazonEan=new AmazonEan();
									amazonEan.setProductName(name);
									amazonEan.setActive("2");
									amazonEan.setEan(ean);
									amazonEan.setCreateDate(date);
									amazonEan.setCountry("com");
									eanList.add(amazonEan);
								}
							}else{
								AmazonEan amazonEan=new AmazonEan();
								amazonEan.setProductName(name);
								amazonEan.setActive("2");
								amazonEan.setEan(ean);
								amazonEan.setCreateDate(date);
								amazonEan.setCountry(country);
								eanList.add(amazonEan);
							}
						}
					}
				}	
				if(eanList!=null&&eanList.size()>0){
					amazonPostsDetailService.saveEan(eanList);
				}
				addMessage(redirectAttributes,"文件上传成功");
		} catch (Exception e) {
			addMessage(redirectAttributes,"文件上传失败"+e.getMessage());
			return "1";
		}
		return "0";
	}
	
	
	@RequestMapping(value = "findCompareContent")
	@ResponseBody
	public String findCompareContent(String accountName,String asin,String keyword){
		AmazonPostsDetail detail=amazonPostsDetailService.getDetailByAsinAndCountry(accountName, asin);
		String returnInfo="";
		if(detail!=null){
			String[] arrStr=keyword.toLowerCase().split(" ");
			if(StringUtils.isNotBlank(detail.getTitle())){
				String info="";
				for (String arr : arrStr) {
					if(detail.getTitle().toLowerCase().contains(arr)){
						info+=arr+" ";
					}
				}	
				if(StringUtils.isNotBlank(info)){
					returnInfo+="标题含有关键字 "+info+"<br/>";
				}
			}
			if(StringUtils.isNotBlank(detail.getDescription())){
				String info="";
				for (String arr : arrStr) {
					if(detail.getDescription().toLowerCase().contains(arr)){
						info+=arr+" ";
					}
				}	
				if(StringUtils.isNotBlank(info)){
					returnInfo+="描述含有关键字 "+info+"<br/>";
				}
			}
			if(StringUtils.isNotBlank(detail.getFeature1())){
				String info="";
				for (String arr : arrStr) {
					if(detail.getFeature1().toLowerCase().contains(arr)){
						info+=arr+" ";
					}
				}	
				if(StringUtils.isNotBlank(info)){
					returnInfo+="卖点1含有关键字 "+info+"<br/>";
				}
			}
			if(StringUtils.isNotBlank(detail.getFeature2())){
				String info="";
				for (String arr : arrStr) {
					if(detail.getFeature2().toLowerCase().contains(arr)){
						info+=arr+" ";
					}
				}	
				if(StringUtils.isNotBlank(info)){
					returnInfo+="卖点2含有关键字 "+info+"<br/>";
				}
			}
			if(StringUtils.isNotBlank(detail.getFeature3())){
				String info="";
				for (String arr : arrStr) {
					if(detail.getFeature3().toLowerCase().contains(arr)){
						info+=arr+" ";
					}
				}	
				if(StringUtils.isNotBlank(info)){
					returnInfo+="卖点3含有关键字 "+info+"<br/>";
				}
			}
			if(StringUtils.isNotBlank(detail.getFeature4())){
				String info="";
				for (String arr : arrStr) {
					if(detail.getFeature4().toLowerCase().contains(arr)){
						info+=arr+" ";
					}
				}	
				if(StringUtils.isNotBlank(info)){
					returnInfo+="卖点4含有关键字 "+info+"<br/>";
				}
			}
			if(StringUtils.isNotBlank(detail.getFeature5())){
				String info="";
				for (String arr : arrStr) {
					if(detail.getFeature5().toLowerCase().contains(arr)){
						info+=arr+" ";
					}
				}
				if(StringUtils.isNotBlank(info)){
					returnInfo+="卖点5含有关键字 "+info+"<br/>";
				}
			}
		}
		return returnInfo;
	}
	
	
	//
	@RequestMapping(value = {"exceptionSizeList"})
	public String exceptionSizeList(String country, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Object[]> sizeList=productService.findExcepitonSize(country);
        model.addAttribute("sizeList",sizeList);
        model.addAttribute("country",country);
		return "modules/amazoninfo/amazonExceptionSizeList";
	}
	
	
	@RequestMapping(value = {"exportExceptionSize"})
	public String exportExceptionSize(String country,HttpServletRequest request,
			HttpServletResponse response, Model model) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 600);
	    HSSFCell cell = null;		
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
		
        List<String> title =Lists.newArrayList("国家","产品名","ASIN","亚马逊长","亚马逊宽","亚马逊高","ERP长","ERP宽","ERP高");
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
		}
		int rownum=1;
		List<Object[]> sizeList=productService.findExcepitonSize(country);
		for (Object[] obj:sizeList) {
			row = sheet.createRow(rownum++);
			int j=0;
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[0]==null?"":obj[0].toString());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[1]==null?"":obj[1].toString());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[2]==null?"":obj[2].toString());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[4]==null?"":obj[4].toString());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[5]==null?"":obj[5].toString());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[6]==null?"":obj[6].toString());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[7]==null?"":obj[7].toString());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[8]==null?"":obj[8].toString());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[9]==null?"":obj[9].toString());
		}
		for (int i = 0; i < title.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
		}
		  try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf1 = new SimpleDateFormat("MMddhhmm");
				String fileName = "size" + sdf1.format(new Date()) + ".xls";
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
	
	
	@RequestMapping(value = {"duplicateRemoval"})
	public String duplicateRemoval(){
		return "modules/amazoninfo/duplicateRemoval";
	}	
	
	
	@RequestMapping(value = {"rankSalesAnalyse"})
	public String rankSalesAnalyse(String country,String catalog, Model model){
		if(StringUtils.isNotBlank(country)&&StringUtils.isNotBlank(catalog)){
			Map<Integer,Integer> rankMap=amazonPostsDetailService.findQuantityByRank(country,catalog);
			model.addAttribute("rankMap", rankMap);
		}
		model.addAttribute("catalog", catalog);
		model.addAttribute("country", country);
		return "modules/amazoninfo/postsRankSalesAnalyseList";
	}
	
}
