/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.security.Digests;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsDetail;
import com.springrain.erp.modules.amazoninfo.entity.AmazonTreadReview;
import com.springrain.erp.modules.amazoninfo.entity.AmazonTreadReviewAccount;
import com.springrain.erp.modules.amazoninfo.htmlunit.LoginUtil;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsDetailService;
import com.springrain.erp.modules.amazoninfo.service.AmazonTreadReviewService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;


@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/amazonTreadReview")
public class AmazonTreadReviewController extends BaseController {

	@Autowired
	private AmazonTreadReviewService amazonTreadReviewService;
	@Autowired
	private AmazonPostsDetailService amazonPostsDetailService;
	
	
	private final static Logger logger = LoggerFactory.getLogger(AmazonTreadReviewController.class);
	
	@RequestMapping(value = {"list", ""})
	public String list(AmazonTreadReview amazonTreadReview, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(amazonTreadReview.getCreateDate()==null){
			amazonTreadReview.setCreateDate(DateUtils.addMonths(today,-1));
			amazonTreadReview.setEndDate(today);
		}
		Page<AmazonTreadReview> page = new Page<AmazonTreadReview>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("id desc");
		}else{
			page.setOrderBy(orderBy+",id desc");
		}	
        page = amazonTreadReviewService.find(page, amazonTreadReview); 
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
		return "modules/amazoninfo/amazonTreadReviewList";
	}
	
	@RequestMapping(value = {"accountList"})
	public String list(AmazonTreadReviewAccount amazonTreadReviewAccount, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isBlank(amazonTreadReviewAccount.getDelFlag())){
			amazonTreadReviewAccount.setDelFlag("0");
		}
		Page<AmazonTreadReviewAccount> page = new Page<AmazonTreadReviewAccount>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("id desc");
		}else{
			page.setOrderBy(orderBy+",id desc");
		}	
        page = amazonTreadReviewService.find(page, amazonTreadReviewAccount); 
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
		return "modules/amazoninfo/amazonTreadReviewAccountList";
	}
	
	
	@RequestMapping(value = {"updateDelFlag"})
	public String updateDelFlag(Integer id) {
		amazonTreadReviewService.updateDelFlag(id);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonTreadReview/accountList/?repage";
	}
	
	
	@RequestMapping(value = {"accountAdd"})
	public String accountAdd(AmazonTreadReviewAccount amazonTreadReviewAccount, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/amazoninfo/amazonTreadReviewAccountAdd";
	}
	
	
	@RequestMapping(value = "saveAccount")
	public String saveAccount(@RequestParam("excel")MultipartFile excelFile,AmazonTreadReviewAccount amazonTreadReviewAccount, Model model, RedirectAttributes redirectAttributes) {
		Workbook workBook;
		List<AmazonTreadReviewAccount> accountList=Lists.newArrayList();
		String country=amazonTreadReviewAccount.getCountry();
		User user=UserUtils.getUser();
		Date date=new Date();
		List<String> oldAccountList=amazonTreadReviewService.findAccount(country);
		Set<String> nameSet=Sets.newHashSet();
		try {
			workBook = WorkbookFactory.create(excelFile.getInputStream());
			Sheet sheet = workBook.getSheetAt(0);
			sheet.setForceFormulaRecalculation(true);
			// 循环行Row
			for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
				AmazonTreadReviewAccount account=new AmazonTreadReviewAccount();
				Row row = sheet.getRow(rowNum);
				if (row == null) {
					continue;
				}
				Cell cell0 = row.getCell(0);
				Cell cell1 = row.getCell(1);
				account.setCountry(country);
				
				String password=String.valueOf(cell1.getStringCellValue()).trim();
				password=Encodes.encodeBase64(password.getBytes());
				account.setPassword(password);
				String loginName=String.valueOf(cell0.getStringCellValue()).trim();
				account.setLoginName(loginName);
				if(oldAccountList!=null&&oldAccountList.size()>0&&oldAccountList.contains(loginName)){
					nameSet.add(loginName);
				}
				account.setCreateUser(user);
				account.setCreateDate(date);
				account.setDelFlag("0");
				accountList.add(account);
			}
			if(nameSet!=null&&nameSet.size()>0){
				amazonTreadReviewService.updateDelFlagByName(nameSet, country);
			}
			amazonTreadReviewService.saveAccount(accountList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonTreadReview/accountList/?repage";
	}
	
	
	@RequestMapping(value = {"form"})
	public String form(AmazonTreadReview amazonTreadReview, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isNotEmpty(amazonTreadReview.getCountry())){
			List<AmazonPostsDetail> asinAndNameList=amazonPostsDetailService.getProductNameList1(amazonTreadReview.getCountry());
			model.addAttribute("asinAndNameList", asinAndNameList);
		}else{
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					amazonTreadReview.setCountry(dict.getValue());
					List<AmazonPostsDetail> asinAndNameList=amazonPostsDetailService.getProductNameList1(amazonTreadReview.getCountry());
					model.addAttribute("asinAndNameList", asinAndNameList);
					break;
				}
			}
			if(StringUtils.isEmpty(amazonTreadReview.getCountry())){
				amazonTreadReview.setCountry("");
			}
		}
		model.addAttribute("amazonTreadReview", amazonTreadReview);
		return "modules/amazoninfo/amazonTreadReviewAdd";
	}
	
	
	@RequestMapping(value = {"treadReview"})
	public String treadReview(final AmazonTreadReview amazonTreadReview, RedirectAttributes redirectAttributes) {
		String country=amazonTreadReview.getCountry();
		List<String> accList=amazonTreadReviewService.isExist(country, amazonTreadReview.getAsin());
		Map<String,String> accountTempMap=Maps.newHashMap();
		if(accList!=null&&accList.size()>0){
			accountTempMap=amazonTreadReviewService.findAccountByCountry(country,amazonTreadReview.getAccountNum(),accList);
		}else{
			accountTempMap=amazonTreadReviewService.findAccountByCountry(country, amazonTreadReview.getAccountNum());
		}
		final Map<String,String> accountMap=accountTempMap;
		if(accountMap!=null&&accountMap.size()>0){
			amazonTreadReview.setCreateDate(new Date());
			amazonTreadReview.setCreateUser(UserUtils.getUser());
			Set<String> accountSet=accountMap.keySet();
			String accountNames="";
			StringBuffer buf= new StringBuffer();
			for (String accountName: accountSet) {
				buf.append(accountName+",");
			}
			accountNames=buf.toString();
			amazonTreadReview.setAccount(accountNames.substring(0, accountNames.length()-1));
			amazonTreadReviewService.save(amazonTreadReview);
			new Thread(){
				public void run() {
					boolean flag=true;
					for (String name: accountMap.keySet()) {
						 String loginName=name;
						 String password=accountMap.get(name);
						 password=new String(Encodes.decodeBase64(password));
						 submit(amazonTreadReview,loginName,password,flag);
						 flag=false;
					}
					amazonTreadReviewService.save(amazonTreadReview);
				}
			}.start();
		}else{
			addMessage(redirectAttributes, "没用有效账号用于踩差评");
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonTreadReview/?country="+amazonTreadReview.getCountry();
	}
	
	public void submit(final AmazonTreadReview amazonTreadReview,String loginName,String password,boolean flag){
		String country=amazonTreadReview.getCountry();
		String asin=amazonTreadReview.getAsin();
		WebClient client = LoginUtil.frontRegister(country, false,loginName,password);
		String stuffix =country;
		if("jp".equals(country)||"uk".equals(country)){
			stuffix="co."+country;
		}else if("mx".equals(country)){
			stuffix="com."+country;
		}
		if(client!=null){
			try {
				String reviewUrl="https://www.amazon."+stuffix+"/product-reviews/"+asin+"/ref=cm_cr_dp_see_all_btm?ie=UTF8&showViewpoints=1&sortBy=top";
				HtmlPage  reviewPage= getPage(client,reviewUrl,0);
				DomElement reviewDoc=reviewPage.getElementById("cm_cr-review_list");
				List<HtmlElement> reviewList=(List<HtmlElement>)reviewDoc.getByXPath("//div[@class='a-section review']");
				
				if(reviewList!=null&&reviewList.size()>0){
					for (HtmlElement review: reviewList) {
						try{
							List<HtmlElement> reviewDiv=review.getElementsByTagName("div");
							//List<DomElement> reviewEls =(List<DomElement>)reviewDiv.get(0).getElementsByTagName("a").get(0).getByXPath("//i[@class='a-icon a-icon-star a-star-5 review-rating']//span");
							String title=reviewDiv.get(0).getElementsByTagName("a").get(1).asText();
							String reviewStar = reviewDiv.get(0).getElementsByTagName("a").get(0).asText();
							String starNum=findStarByNew(reviewStar);
							if(!"4".equals(starNum)){
								DomElement yesOrNoLink=reviewDiv.get(0).getElementsByTagName("a").get(1);
								String link=yesOrNoLink.getAttribute("href");
								String tempLink="https://www.amazon."+stuffix+link;
								HtmlPage linkPage= getPage(client,tempLink,0);
								List<DomElement> tables = (List<DomElement>)linkPage.getByXPath("//table");
								DomElement table= tables.get(0);
								List<HtmlElement> trs =table.getElementsByTagName("tr");
								DomElement td = (DomElement)trs.get(0).getChildNodes().get(0);
								String vote=td.getChildNodes().get(3).getChildNodes().get(0).asText();
								
								String linkUrl ="";
								if(Float.parseFloat(starNum)>3){
									List<DomElement> els =(List<DomElement>)linkPage.getByXPath("//a[@class='votingButtonReviews yesButton']");
									linkUrl= els.get(0).getAttribute("href");
								}else{
									List<DomElement> els =(List<DomElement>)linkPage.getByXPath("//a[@class='votingButtonReviews noButton']");
									linkUrl= els.get(0).getAttribute("href");
								}
								getPage(client,linkUrl,0);
								//HtmlPage returnPage  = getPage(client,linkUrl,0);
								//String text=returnPage.asText();
								//logger.info(linkUrl+"=="+text);
								if(flag){
									String desc=amazonTreadReview.getDescription();
									if(StringUtils.isNotBlank(desc)){
										amazonTreadReview.setDescription(desc+starNum+" star, <a target='_blank' href="+tempLink+">"+title+"</a>, "+vote+"<br/>");
									}else{
										amazonTreadReview.setDescription(starNum+" star, <a target='_blank' href="+tempLink+">"+title+"</a>, "+vote+"<br/>");
									}
								}
								
							}
						}catch(Exception e){
							logger.info(e.getMessage(),e);
						}
					}
				}
				
				
			}catch (Exception e) {
				logger.info(e.getMessage(),e);
			}
		 }
	 }
		
		private  HtmlPage getPage(WebClient client,String url,int num){
			if(num>10){
				return null;
			}
			try {
				HtmlPage page =  client.getPage(url);
				return page;
			} catch (Exception e) {
				e.printStackTrace();
				num = num +1;
				return getPage(client,url,num);
			}
		}
		
		private  String findStarByNew(String str) {
			str = str.replace(",",".");
			String result = null;
			String regex = "\\d{1}\\.0";
			Pattern pattern = Pattern.compile(regex);
			Matcher match = pattern.matcher(str);
			if (match.find()) {
				result = match.group().replace(".0","");
			}
			return result;
		}
}
