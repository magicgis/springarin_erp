package com.springrain.erp.modules.amazoninfo.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.endpoint.Client;
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

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.Image;
import com.springrain.erp.modules.amazoninfo.entity.ImageFeed;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.ImageFeedService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.magento.MagentoClientService;

/**
 * 亚马逊帖子上架Controller
 * @author tim
 * @version 2014-08-06
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/imageFeed")
public class ImageFeedController extends BaseController {

	@Autowired
	private ImageFeedService imageFeedService;
	
	@Autowired
	private AmazonProductService amazonProductService;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	
	@Autowired
	private PsiProductGroupUserService groupUserService;
	
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	private static Logger logger = LoggerFactory.getLogger(ImageFeedController.class);
	
	@ModelAttribute
	public ImageFeed get(@RequestParam(required=false) Integer id) {
		if (id!=null&&id>0){
			return imageFeedService.get(id);
		}else{
			return new ImageFeed();
		}
	}
	
	@RequiresPermissions("amazoninfo:imageFeed:view")
	@RequestMapping(value = {"list", ""})
	public String list(ImageFeed imageFeed, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(imageFeed.getRequestDate()==null){
			imageFeed.setRequestDate(DateUtils.addMonths(today,-1));
			imageFeed.setEndDate(today);
		}
		User user = UserUtils.getUser();
		if (!user.isAdmin()&& imageFeed.getCreateBy()==null){
			imageFeed.setCreateBy(user);
		}; 
		if(UserUtils.hasPermission("it:special:permission")){
			imageFeed.setCreateBy(null);
		}
		Page<ImageFeed> page = new Page<ImageFeed>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("id desc");
		}else{
			page.setOrderBy(orderBy+",id desc");
		}	
        page = imageFeedService.find(page, imageFeed); 
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
        model.addAttribute("cuser",user);
		return "modules/amazoninfo/imageFeedList";
	}

	@RequiresPermissions("amazoninfo:imageFeed:view")
	@RequestMapping(value = "form")
	public String form(ImageFeed imageFeed, Model model) {
		if(StringUtils.isNotEmpty(imageFeed.getCountry())){
			List<String> countryList = Lists.newArrayList(imageFeed.getCountry());
			model.addAttribute("sku", amazonProductService.findSkuForImage(countryList));
			//model.addAttribute("sku",amazonProductService.findSku(imageFeed.getCountry()));
		}else{
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					imageFeed.setCountry(dict.getValue());
					List<String> countryList = Lists.newArrayList(imageFeed.getCountry());
					model.addAttribute("sku", amazonProductService.findSkuForImage(countryList));
					//model.addAttribute("sku",amazonProductService.findSku(imageFeed.getCountry()));
					break;
				}
			}
			if(imageFeed.getCountry()==null){
				imageFeed.setCountry("");
			}
		}
		model.addAttribute("imageFeed", imageFeed);
		model.addAttribute("accountList",amazonAccountConfigService.findCountryByAccountByServer());
		return "modules/amazoninfo/imageFeedForm";
	}
	
	@RequiresPermissions("amazoninfo:imageFeed:view")
	@ResponseBody
	@RequestMapping(value = "getSku")
	public String getSku(String country) {
		if (StringUtils.isEmpty(country)) {
			return null;
		}
		List<String> countryList = Arrays.asList(country.split(","));
		return JSON.toJSONString(amazonProductService.findSkuForImage(countryList));
	}
	
	@RequiresPermissions("amazoninfo:imageFeed:view")
	@RequestMapping(value = "view")
	public String view(ImageFeed imageFeed, Model model) {
		model.addAttribute("imageFeed", imageFeed);
		return "modules/amazoninfo/imageFeedView";
	}
	
	@RequiresPermissions("amazoninfo:imageFeed:view")
	@RequestMapping(value = "save")
	public String save(ImageFeed imageFeed, Model model, RedirectAttributes redirectAttributes) {
		Map<String,AmazonAccountConfig> accountMap = amazonAccountConfigService.findConfigByAccountName();
		if(imageFeed.getId()!=null){
			String rs = submit(imageFeed, accountMap.get(imageFeed.getAccountName()));
			addMessage(redirectAttributes, rs);
			return "redirect:"+Global.getAdminPath()+"/amazoninfo/imageFeed/?repage";
		}
		String productName = imageFeed.getSku();
		if(StringUtils.isEmpty(productName)){
			addMessage(redirectAttributes, "请选择产品");
			return "redirect:"+Global.getAdminPath()+"/amazoninfo/imageFeed/?repage";
		}
		Map<String, String> skus = Maps.newHashMap();
		String[] arr=imageFeed.getCountry().split(",");
		Set<String> accountName=Sets.newHashSet();
		for (String temp : arr) {
			accountName.add(temp.substring(temp.indexOf("_")+1));
		}
		List<Map<String, String>> maps = amazonProductService.findSkuForEditImage(accountName, null);
		for (Map<String, String> map : maps) {
			if (map.get("value").contains(productName+"[")) {
				String key = map.get("key").split("@")[0];
				String sku = map.get("key").split("@")[1];
				if ("us".equals(key)) {
					key = "com";
				}
				String countrySku = skus.get(key);
				if (StringUtils.isEmpty(countrySku)) {
					skus.put(key, sku);
				} else {
					skus.put(key, countrySku + "|" + sku);
				}
			}
		}
		logger.info("修改图片sku：" + skus.toString());
		String rs = "正在发到服务器，请等待结果。。。";
		if (skus.size() == 0) {
			rs = "操作失败,未匹配到相应的SKU...";
		}
		if(imageFeed.getId()==null){
			for (Map.Entry<String,String> entry: skus.entrySet()) { 
				AmazonAccountConfig config=accountMap.get(entry.getKey());
			    String country = config.getCountry();
				String sku = entry.getValue();
				
				ImageFeed feed = new ImageFeed();
				feed.setCountry(country);
				feed.setSku(sku + "," + productName);
				feed.setAccountName(entry.getKey());
				List<Image> images = Lists.newArrayList();
				for (Iterator<Image> iterator = imageFeed.getImages().iterator(); iterator.hasNext();) {
					Image image = iterator.next();
					if(StringUtils.isNotEmpty(image.getLocation())||"1".equals(image.getIsDelete())){
						Image image2 = new Image();
						image2.setIsDelete(image.getIsDelete());
						String location = image.getLocation();
						if (location.startsWith("|")) {
							location = location.substring(1);
						}
						image2.setLocation(location);
						image2.setType(image.getType());
						image2.setImageFeed(feed);
						images.add(image2);
					}else{
						iterator.remove();
					}
				}
				feed.setImages(images);
				rs = submit(feed, config);
			}
		}
		
		addMessage(redirectAttributes, rs);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/imageFeed/?repage";
	}
	
	private  String submit(final ImageFeed imageFeed,final AmazonAccountConfig config){
		String rs = "正在发到服务器，请等待结果。。。";
		try {
			if(imageFeed.getId()==null){
				imageFeed.setCreateBy(UserUtils.getUser());
				imageFeed.setRequestDate(new Date());
			}
			imageFeed.setState("1");
			imageFeedService.save(imageFeed);
			//同步修改至官网
			submitToInateck(imageFeed);
			new Thread(){
				public void run() {
					//远程webservice提交到亚马逊
					try {
						String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp() + ":8080");
						Client client = BaseService.getCxfClient(interfaceUrl);
						Object[] str = new Object[]{Global.getConfig("ws.key"), imageFeed.getId()};
						client.invoke("submitImageFeed", str);
					} catch (Exception e) {
						imageFeed.setState("4");
						imageFeedService.save(imageFeed);
						logger.error(config.getAccountName()+"修改图片出错了", e);
					}
				};
			}.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rs = "修改图片失败了!";
		}
		return rs;
	}

	private void submitToInateck(final ImageFeed imageFeed) {
		new Thread(){
			public void run() {
				String sku = imageFeed.getSku();
				if (StringUtils.isEmpty(sku)) {
					return;
				}
				sku = sku.split(",")[0].split("\\|")[0];
				String country = imageFeed.getCountry();
				//国家_sku对应的asin
				String asin = amazonProduct2Service.findAsin(country, sku);
				if(StringUtils.isNotEmpty(asin)){
					MagentoClientService.catalogProductAttributeMediaUpdate(country, asin, imageFeed.getImages());
				}
			}
		}.start();
	}
	
	public static String getResultStr(File result,ImageFeed feed){
		String rs = "";
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(result);
			Element root = doc.getRootElement();
			List<Element> msgs = root.elements("Message");
			String sku = feed.getSku();
			if (sku.contains(",")) {
				try {
					sku = sku.split(",")[1];
				} catch (Exception e) {}
			}
			for (int i = 0; i < msgs.size(); i++) {
				Element el = (Element)msgs.get(i).selectSingleNode("//ProcessingSummary");
				rs = sku +":<br/>"+HtmlUtils.htmlEscape(el.asXML())+"<br/>";
			}
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
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/imagefeeds/";  
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
	
	@RequiresPermissions("amazoninfo:imageFeed:view")
	@ResponseBody
	@RequestMapping(value = "getLink")
	public String getLink(String sku,String country) {
		if (StringUtils.isEmpty(sku) || StringUtils.isEmpty(country)) {
			return "";
		}
		Set<String> accountName=Sets.newHashSet();
		if (country.contains(",")) {
			accountName.add(country.split(",")[0]);
		}
		String asin = amazonProduct2Service.findAsin(country, sku);
		if (StringUtils.isEmpty(asin)) {	//根据产品名去查找
			List<Map<String, String>> maps = amazonProductService.findSkuForEditImage(accountName, null);
			for (Map<String, String> map : maps) {
				if (map.get("value").contains(sku+"[")) {
					sku = map.get("key").split("@")[1];
					asin = amazonProduct2Service.findAsin(country, sku);
					break;
				}
			}
		}
		if(asin!=null){
			String suff = country;
			if("uk,jp".contains(country)){
				 suff = "co."+suff;
			}else if("mx".equals(country)){
				suff = "com."+suff;
			}
			return "http://www.amazon."+suff+"/dp/"+asin;
		}
		return "";
	}
	
	@RequiresPermissions("amazoninfo:imageFeed:view")
	@ResponseBody
	@RequestMapping(value = "getProductImage")
	public String getProductImage(String sku) {
		if (StringUtils.isEmpty(sku)) {
			return "";
		}
		String rs = amazonProduct2Service.findProductImage(sku);
		if (StringUtils.isEmpty(rs)) {	//根据产品名去查找
			List<Map<String, String>> maps = amazonProductService.findSkuForEditImage(Sets.newHashSet("Inateck_DE","Inateck_US"), null);
			for (Map<String, String> map : maps) {
				if (map.get("value").contains(sku+"[")) {
					sku = map.get("key").split("@")[1];
					rs = amazonProduct2Service.findProductImage(sku);
					break;
				}
			}
		}
		return rs;
	}
	
}
