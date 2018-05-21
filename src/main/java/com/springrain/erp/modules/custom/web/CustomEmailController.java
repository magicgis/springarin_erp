package com.springrain.erp.modules.custom.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonRefund;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonRefundService;
import com.springrain.erp.modules.custom.entity.Comment;
import com.springrain.erp.modules.custom.entity.CustomEmail;
import com.springrain.erp.modules.custom.entity.CustomEmailTemplate;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.custom.entity.Signature;
import com.springrain.erp.modules.custom.entity.UnsubscribeEmail;
import com.springrain.erp.modules.custom.scheduler.CustomEmailMonitor;
import com.springrain.erp.modules.custom.service.CommentService;
import com.springrain.erp.modules.custom.service.CustomEmailService;
import com.springrain.erp.modules.custom.service.CustomEmailTemplateService;
import com.springrain.erp.modules.custom.service.CustomProductProblemService;
import com.springrain.erp.modules.custom.service.CustomSuggestionService;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.custom.service.SendEmailService;
import com.springrain.erp.modules.custom.service.SignatureService;
import com.springrain.erp.modules.custom.service.UnsubscribeEmailService;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.server.pojo.Message;

/**
 * 邮件Controller
 * @author tim
 * @version 2014-04-30
 */
@Controller
@RequestMapping(value = "${adminPath}/custom/emailManager")
public class CustomEmailController extends BaseController {

	@Autowired
	private CustomEmailService customEmailService;
	
	@Autowired
	private SendEmailService sendEmailService;
	
	@Autowired
	private SignatureService signatureService;
	
	@Autowired
	private AmazonOrderService orderService;
	
	@Autowired
	private PsiProductService psiProductService;
	
	
	@Autowired
	private CustomEmailTemplateService customEmailTemplateService;
	
	@Autowired
	private AmazonRefundService  amazonRefundService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private EventService eventService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private AmazonCustomerService amazonCustomerService;
	@Autowired
	private CustomProductProblemService	 customProductProblemService;
	@Autowired
	private CustomSuggestionService customSuggestionService;
	@Autowired
	private PsiProductTypeGroupDictService psiTypeGroupService;
	
	@Autowired
	private MailManager mailManager;
	
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	
	private final static Pattern ORDER_PATTERN = Pattern.compile("\\d{3}-\\d{7}-\\d{7}");
	
	private final static Pattern MSG_ID_PATTERN = Pattern.compile("\\[commMgrTok:.+\\]");

	@ModelAttribute
	public CustomEmail get(@RequestParam(required=false) String id) {
		if (!StringUtils.isBlank(id)){
			CustomEmail rs = customEmailService.get(id);
			return rs;
		}else{
			return new CustomEmail();
		}
	}
	
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = {"list", ""})
	public String list(CustomEmail customEmail, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser(); 
		if (!user.isAdmin()){
			customEmail.setCreateBy(user);
		}
		if(customEmail.getState()==null){
			customEmail.setState("5");
		}
		if (customEmail.getCustomSendDate() == null) {	//默认查询近一个月数据
			Date date = new Date();
			date = DateUtils.getDateStart(date);
			customEmail.setCustomSendDate(DateUtils.addMonths(date, -1));
			customEmail.setEndDate(date);
		}
		Page<CustomEmail> page = new Page<CustomEmail>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			if("2".equals(customEmail.getState())){
				page.setOrderBy("endDate desc");
			}else{
				page.setOrderBy("revertEmail,customSendDate desc");
			}
		}else{
			page.setOrderBy(orderBy+",revertEmail,customSendDate desc");
		}
		//long st = System.currentTimeMillis();
        page = customEmailService.find(page,customEmail); 
        //logger.info("-----------------------time :"+(System.currentTimeMillis()-st)/1000);
        model.addAttribute("otherMaster",CustomEmailMonitor.getOtherMaster(customEmailService));
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
        model.addAttribute("cuser", user);
        if (!("0".equals(customEmail.getState()) || "1".equals(customEmail.getState()) || "5".equals(customEmail.getState()))) {
        	model.addAttribute("dateLimit", "1");
        }
    	model.addAttribute("groupType", psiTypeGroupService.getAllList());
    	model.addAttribute("emailMap", amazonAccountConfigService.findAccountByEmail());
		return "modules/custom/customEmailList";
	}
	
	@RequestMapping(value = {"problemList"})
	public String problemList(CustomEmail customEmail, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date today =sdf.parse(sdf.format(new Date()));
		if (customEmail.getAnswerDate() == null) {
			customEmail.setAnswerDate(DateUtils.addMonths(today, -1));
		}
		Page<CustomEmail> page = new Page<CustomEmail>(request, response);
		page.setPageSize(20);
		String orderBy = page.getOrderBy();
		page.setOrderBy("customSendDate desc");
        page = customEmailService.findProblems(page,customEmail); 
        page.setOrderBy(orderBy);
        
		model.addAttribute("productMap", this.psiProductService.findProductTypeMap());
		model.addAttribute("mangerMap", this.psiProductService.findManagerProductTypeMap());//查询产品经理
        model.addAttribute("page", page);
		return "modules/custom/customEmailProblemList";
	}
	
	
	@RequestMapping(value = "expProblem")
	public String expProblem(CustomEmail customEmail,HttpServletRequest request,HttpServletResponse response, Model model) {
		Page<CustomEmail> page = new Page<CustomEmail>(request, response);
		page.setPageSize(60000);
		page.setOrderBy("answerDate desc");
        List<CustomEmail>  customEmails = customEmailService.findProblems(page,customEmail).getList(); 
        
        
       Map<String,String> productMap = this.psiProductService.findProductTypeMap();
	   Map<String,String> mangerMap  = this.psiProductService.findManagerProductTypeMap();//查询产品经理
		
		
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
			  
			  CellStyle titleStyle = wb.createCellStyle();
			  titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			  HSSFFont font1 = wb.createFont();
			  font1.setFontHeightInPoints((short) 15); // 字体高度
			  font1.setFontName(" 黑体 "); // 字体
			  font1.setBoldweight((short) 15);
			  titleStyle.setFont(font1);

			  HSSFCell cell = null;		
			  List<String> title=Lists.newArrayList("Product Line","Product Name","Problem Type", "Problem Detail",  "Product Manager","Order Nos");
			  for (int i = 0; i < title.size(); i++) {
					cell = row.createCell(i);
					cell.setCellValue(title.get(i));
					cell.setCellStyle(style);
					sheet.autoSizeColumn((short) i);
				}
		 int rowIndex=1;
         if(customEmails!=null){
		    	for (int i=0;i<customEmails.size();i++) {
		    		CustomEmail  custom=customEmails.get(i);
	    			int j=0;
		    		row=sheet.createRow(rowIndex++);
		    		row.setHeight((short) 400);
		    		String type =productMap.get(custom.getProductName());
                    row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(type);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(custom.getProductName());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(custom.getProblemType());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(custom.getProblem());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(mangerMap.get(type));
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(custom.getOrderNos());
		    		}
	          
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
		
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		
				String fileName = "emailProblem" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
         }
		return null;
	}
	
	@RequestMapping(value = {"count"})
	public String count(CustomEmail customEmail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(customEmail.getCreateDate()==null){
			customEmail.setCreateDate(DateUtils.addDays(today,-2));
			customEmail.setEndDate(today);
		}
		Map<String, Map<String, String>> date =  customEmailService.count(customEmail);
		//总回复数
		Map<String, Map<String, String>> date1 = sendEmailService.count(customEmail);
		for (Entry<String, Map<String, String>> entry : date.entrySet()) {
			String key = entry.getKey();
			Map<String, String> temp = date1.get(key);
			if(temp!=null){
				if(date.get(key)==null){
					date.put(key, temp);
				}else{
					date.get(key).putAll(temp);
				}
			}
		}
		for (Entry<String, Map<String, String>> entry : date1.entrySet()) {
			String key = entry.getKey();
			Map<String, String> temp = entry.getValue();
			if(null!=temp){
				if(date.get(key)==null){
					date.put(key, temp);
				}else{
					date.get(key).putAll(temp);
				}
			}
		}
		model.addAttribute("date",date);
		return "modules/custom/customEmailCountList";
	}
	

	@RequiresPermissions("custom:email:all")
	@RequestMapping(value = {"all"})
	public String all(CustomEmail customEmail, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			customEmail.setCreateBy(user);
		}
		if (customEmail.getCustomSendDate() == null) {	//默认查询近一个月数据
			Date date = new Date();
			date = DateUtils.getDateStart(date);
			customEmail.setCustomSendDate(DateUtils.addMonths(date, -1));
			customEmail.setEndDate(date);
		}
		Page<CustomEmail> page = new Page<CustomEmail>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("customSendDate desc");
		}else{
			page.setOrderBy(orderBy+",customSendDate desc");
		}	
        page = customEmailService.findAll(page,customEmail); 
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
        model.addAttribute("all",systemService.findAllUsers());
        if (!("0".equals(customEmail.getState()) || "1".equals(customEmail.getState()))) {
        	model.addAttribute("dateLimit", "1");
        }
        model.addAttribute("emailMap", amazonAccountConfigService.findAccountByEmail());
		return "modules/custom/customEmailAllList";
	}
	
	@ResponseBody
	@RequestMapping(value = "isExistEvent")
	public String isExistEvent(CustomEmail emailManager,String attchmentPath, Model model,String amazonOrderId) {
		
		if(StringUtils.isBlank(amazonOrderId)){
			Set<String> ordersStr = getOrders(emailManager.getSubject());
			if (ordersStr != null) {
				ordersStr.addAll(getOrders(emailManager.getReceiveContent()));
			}
			
			if(ordersStr==null||ordersStr.size()==0){
				String email=emailManager.getRevertEmail();
				if(email.contains("marketplace.amazon.")){
					List<String> orderIdList=orderService.findAllOrderId(email);
					if(orderIdList!=null&&orderIdList.size()>0){
						ordersStr.addAll(orderIdList);
					}
				}else{
					String amzEmail=amazonCustomerService.findAmzEmail(email);
					if(StringUtils.isNotBlank(amzEmail)){
						List<String> orderIdList=orderService.findAllOrderId(amzEmail);
						if(orderIdList!=null&&orderIdList.size()>0){
							ordersStr.addAll(orderIdList);
						}
					}
				}
			}
			
			List<AmazonOrder> orders = Lists.newArrayList();
			for (String ordStr : ordersStr) {
				AmazonOrder order = orderService.findByEg(ordStr);
				if(order!=null){
					orders.add(order);
				}
			}
			
			if(orders!=null&&orders.size()>0){
				Event event=eventService.isExistEventByOrder(orders.get(0).getAmazonOrderId());
				if(event!=null){
					//String attchmentPath=emailManager.getAttchmentPath();
					if(StringUtils.isNotBlank(attchmentPath)&&((StringUtils.isBlank(event.getAttchmentPath()))||(
							StringUtils.isNotBlank(event.getAttchmentPath())&&!event.getAttchmentPath().contains("/TP"+attchmentPath))
					)		
					){
						String uuid = UUID.randomUUID().toString();
						String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/event/"+uuid;
						File dir = new File(baseDirStr);
						String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/customEmail/";  
					    String ctxLoadPath = ctxPath + attchmentPath;
					        
						if (!dir.isDirectory()){dir.mkdirs();}
						
						if(attchmentPath.contains(",")){
							String[] arr=attchmentPath.split(",");
							attchmentPath=arr[0].split("/")[1];
						}else{
							attchmentPath=attchmentPath.split("/")[1];
						}
						if(attchmentPath.endsWith(".pdf")){
							FileUtils.copyFile(ctxLoadPath,baseDirStr+"/"+attchmentPath);
							event.setAttchmentPath(uuid+"/"+attchmentPath);
						}else{
							FileUtils.copyFile(ctxLoadPath,baseDirStr+"/TP"+attchmentPath);
							event.setAttchmentPath(uuid+"/TP"+attchmentPath);
						}
						
						eventService.save(event);
						
						String common =UserUtils.getUser().getName()+"  created "+event.getTypeStr()+" Event Attchment for the cause of Customer Email. <a href='../emailManager/form?id="+emailManager.getId()+"'>view</a>";
						Comment comm = new Comment();
						comm.setComment(common);
						comm.setType("1");
						comm.setCreateBy(UserUtils.getUserById("1"));
						comm.setEvent(event);
						commentService.save(comm);
						
						return "0";
					}else if(StringUtils.isNotBlank(event.getAttchmentPath())&&event.getAttchmentPath().contains("/TP"+attchmentPath)){
						return "0";
					}else{
						return "1";
					}
				}else{
					return "1";
				}
			}
			return "1";
		}else{
			Event event=eventService.isExistEventByOrder(amazonOrderId);
			if(event!=null){
				//String attchmentPath=emailManager.getAttchmentPath();
				if(StringUtils.isNotBlank(attchmentPath)&&((StringUtils.isBlank(event.getAttchmentPath()))||(
						StringUtils.isNotBlank(event.getAttchmentPath())&&!event.getAttchmentPath().contains("/TP"+attchmentPath))
				)		
				){
					String uuid = UUID.randomUUID().toString();
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/event/"+uuid;
					File dir = new File(baseDirStr);
					String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/customEmail/";  
				    String ctxLoadPath = ctxPath + attchmentPath;
				        
					if (!dir.isDirectory()){dir.mkdirs();}
					
					if(attchmentPath.contains(",")){
						String[] arr=attchmentPath.split(",");
						attchmentPath=arr[0].split("/")[1];
					}else{
						attchmentPath=attchmentPath.split("/")[1];
					}
					if(attchmentPath.endsWith(".pdf")){
						FileUtils.copyFile(ctxLoadPath,baseDirStr+"/"+attchmentPath);
						event.setAttchmentPath(uuid+"/"+attchmentPath);
					}else{
						FileUtils.copyFile(ctxLoadPath,baseDirStr+"/TP"+attchmentPath);
						event.setAttchmentPath(uuid+"/TP"+attchmentPath);
					}
					
					eventService.save(event);
					
					String common =UserUtils.getUser().getName()+"  created "+event.getTypeStr()+" Event Attchment for the cause of Customer Email. <a href='../emailManager/form?id="+emailManager.getId()+"'>view</a>";
					Comment comm = new Comment();
					comm.setComment(common);
					comm.setType("1");
					comm.setCreateBy(UserUtils.getUserById("1"));
					comm.setEvent(event);
					commentService.save(comm);
					
					return "0";
				}else if(StringUtils.isNotBlank(event.getAttchmentPath())&&event.getAttchmentPath().contains("/TP"+attchmentPath)){
					return "0";
				}else{
					return "1";
				}
			}else{
				return "1";
			}
		}
		
	}
	
	
	@RequestMapping(value = { "taxRefund"})
	public String taxRefund(CustomEmail emailManager,HttpServletRequest request,HttpServletResponse response, Model model,String amazonOrderId) {
		AmazonOrder amazonOrder=null;
		if(StringUtils.isBlank(amazonOrderId)){
			Set<String> ordersStr = getOrders(emailManager.getSubject());
			if (ordersStr != null) {
				ordersStr.addAll(getOrders(emailManager.getReceiveContent()));
			}
			if(ordersStr==null||ordersStr.size()==0){

				String email=emailManager.getRevertEmail();
				if(email.contains("marketplace.amazon.")){
					List<String> orderIdList=orderService.findAllOrderId(email);
					if(orderIdList!=null&&orderIdList.size()>0){
						ordersStr.addAll(orderIdList);
					}
				}else{
					String amzEmail=amazonCustomerService.findAmzEmail(email);
					if(StringUtils.isNotBlank(amzEmail)){
						List<String> orderIdList=orderService.findAllOrderId(amzEmail);
						if(orderIdList!=null&&orderIdList.size()>0){
							ordersStr.addAll(orderIdList);
						}
					}
				}
			
		  }
			
			List<AmazonOrder> orders = Lists.newArrayList();
			for (String ordStr : ordersStr) {
				AmazonOrder order = orderService.findByEg(ordStr);
				if(order!=null){
					orders.add(order);
				}
			}
			if(orders!=null&&orders.size()>0){
				amazonOrder=orderService.get(orders.get(0).getId());
			}
		}else{
           amazonOrder=orderService.findByEg(amazonOrderId);
		}
		AmazonRefund amazonRefund=new AmazonRefund();
		amazonRefund.setCountry(StringUtils.substringAfterLast(amazonOrder.getSalesChannel(), "."));
		amazonRefund.setAmazonOrderId(amazonOrder.getAmazonOrderId());
		List<AmazonRefund> records=amazonRefundService.getRefundRecord(amazonRefund.getAmazonOrderId());
		List<User> all = Lists.newArrayList();
	    List<User> list1 = systemService.findUserByPermission("amazoninfo:refund:"+StringUtils.substringAfterLast(amazonOrder.getSalesChannel(),"."));
		if (list1 != null && list1.size() > 0) {
			all.addAll(list1);
		}
		//全球退款审核人
		List<User> list2 = systemService.findUserByPermission("amazoninfo:refund:all");
		if (list2 != null && list2.size() > 0) {
			all.addAll(list2);
		}
		model.addAttribute("records", records);
		model.addAttribute("amazonOrder", amazonOrder);
		model.addAttribute("all", all);
		
		AmazonAddress address = amazonOrder.getInvoiceAddress();
		if (address == null) {
			address = amazonOrder.getShippingAddress();
		}
		String countryCode = address.getCountryCode();
		if(countryCode!=null){
			countryCode = countryCode.toUpperCase();
		}else{
			countryCode = "";
		}
		float rate = 0f;
		if("uk,de,fr,es,it".contains(amazonRefund.getCountry())){
			CountryCode code  = null;
			try {
				code = CountryCode.valueOf(countryCode);
			} catch (Exception e) {
				String temp = amazonRefund.getCountry();
				if("uk".equals(temp)){
					temp = "gb";
				}
				code = CountryCode.valueOf(temp.toUpperCase());
			}
			rate = code.getVat();
		}
		model.addAttribute("rate", rate);
		return "modules/amazoninfo/order/amazonTaxRefundAdd";
	}
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "form")
	public String form(CustomEmail emailManager, Model model,String checkEmail) {
		String state = emailManager.getState();
		if(emailManager.getMasterBy().getId().equals(UserUtils.getUser().getId())){
			if(state!=null && state.equals("0")){
				emailManager.setState("1");
				emailManager.setAnswerDate(new Date());
				customEmailService.save(emailManager);
			}
		}
		Set<String> ordersStr = getOrders(emailManager.getSubject());
		if (ordersStr != null) {
			ordersStr.addAll(getOrders(emailManager.getReceiveContent()));
		}
		if(ordersStr==null||ordersStr.size()==0){

				String email=emailManager.getRevertEmail();
				if(email.contains("marketplace.amazon.")){
					List<String> orderIdList=orderService.findAllOrderId(email);
					if(orderIdList!=null&&orderIdList.size()>0){
						ordersStr.addAll(orderIdList);
					}
				}else{
					String amzEmail=amazonCustomerService.findAmzEmail(email);
					if(StringUtils.isNotBlank(amzEmail)){
						List<String> orderIdList=orderService.findAllOrderId(amzEmail);
						if(orderIdList!=null&&orderIdList.size()>0){
							ordersStr.addAll(orderIdList);
						}
					}
				}
			
		}
		String sku = "";
		String country ="";
		List<AmazonOrder> orders = Lists.newArrayList();
		Set<String> emailSet=Sets.newHashSet();
		if (ordersStr != null) {
			for (String ordStr : ordersStr) {
				AmazonOrder order = orderService.findByEg(ordStr);
				
				if(order!=null){
					String customId=orderService.getCustomIdByOrderId(ordStr); 
					if(StringUtils.isNotBlank(customId)){
						order.setCustomId(customId);
					}
					orders.add(order);
					if(StringUtils.isNotBlank(order.getBuyerEmail())){
						emailSet.add(order.getBuyerEmail());
					}
					order.setAmazonRefunds(amazonRefundService.getRefundRecord(order.getAmazonOrderId()));
					if(StringUtils.isEmpty(sku)){
						//随意取一个订单一个item里的sku
						sku=order.getItems().get(0).getSellersku();
						country=order.getSalesChannel().substring(order.getSalesChannel().lastIndexOf(".")+1);
					}
				}
			}
		}
		
		if(emailSet!=null&&emailSet.size()>0){
			Map<String,Set<String>> eventIdMap=customEmailService.findAllEventByEmail(emailSet);
			model.addAttribute("eventIdMap", eventIdMap);
		}
		if(ordersStr!=null&&ordersStr.size()>0){
			StringBuffer orderNos = new StringBuffer("");
			for(String orderNo:ordersStr){
				orderNos.append(orderNo+",");
			}
			emailManager.setOrderNos(orderNos.toString().substring(0, orderNos.toString().length()-1));
		}
		String productName="";
		
		if(StringUtils.isNotEmpty(sku)){
			//查询该sku对应的产品名字
			PsiSku psiSku=this.psiProductService.getSkuBySku(sku);
			if(psiSku!=null){
				if(StringUtils.isNotEmpty(psiSku.getColor())){
					productName=psiSku.getProductName()+"_"+psiSku.getColor();
				}else{
					productName=psiSku.getProductName();
				}
			}
		}
		
		//查找历史问题信息
		List<Object[]> hisProblems = this.customEmailService.findHistoryEmail(emailManager.getRevertEmail(),emailManager.getId());
		model.addAttribute("hisProblems", hisProblems);
		
		
		//找到所有的系统模板
		List<CustomEmailTemplate> templates = this.customEmailTemplateService.find();		
		model.addAttribute("templates", templates);
		//查询所有产品名及产品类型
		Map<String,String> productMap =this.psiProductService.findProductTypeMap();
		
		model.addAttribute("country", country);
		model.addAttribute("checkEmail",checkEmail);
		model.addAttribute("productName", productName);
		model.addAttribute("productMap", productMap);
		model.addAttribute("productMapJson", JSON.toJSON(productMap));
		model.addAttribute("mangerMapJson", JSON.toJSON(this.psiProductService.findManagerProductTypeMap()));//查询产品经理
		//产品问题类型
		model.addAttribute("problemMapJson", JSON.toJSON(customProductProblemService.findProblemType()));
		
		model.addAttribute("orders", orders);
		
		
		Map<String, String> cache = CustomEmailMonitor.getMasterUserCache(customEmailService,emailManager.getMasterBy().getId());
		model.addAttribute("customRoles",cache);
		model.addAttribute("otherMaster",CustomEmailMonitor.getOtherMaster(customEmailService));
		model.addAttribute("suggestion", customSuggestionService.getByEmailId(emailManager.getId()));
		List<String> wordList=Lists.newArrayList("fire","burn","exlode","smoke","crash","remove","negative","free","positive",
				 "compensation", "discount", "money","bank","paypal","gratuit","gratuitement","commentaire","négatif","argent","réduction","banque","supprimer",
				 "retirar","negativo","comentario","dinero","banco","reembolso","descuento","eliminar",
				 "negativ","entfernen","aktualisieren","ändern","kommentar","geld","paypal","entschädigung",
				 "eliminare","rimborsare","rimvuovere","cancellare","migliorare","modificare",
				 "現金","ネガティブレビュー","ネガティブレビューを削除","ディスカウント","賠償","バンク","クレーム");
		String content=emailManager.getReceiveContent();
		if(StringUtils.isNotBlank(content)&&emailManager.getRevertEmail().contains("marketplace.amazon")){
			 for (String word: wordList) {
				if(content.toLowerCase().contains(" "+word+" ")||content.toLowerCase().contains(" "+word+",")
						||content.toLowerCase().contains(","+word+" ")||content.toLowerCase().contains(","+word+",")
						||content.toLowerCase().contains(word+" ")||content.toLowerCase().contains(" "+word)
						){
					
					content=content.replaceAll(" "+word+" ","<b><span style='color:red;'>"+" "+word+" "+"</span></b>");
					content=content.replaceAll(" "+word.toUpperCase()+" ","<b><span style='color:red;'>"+" "+word.toUpperCase()+" "+"</span></b>");
					
					content=content.replaceAll(" "+word+",","<b><span style='color:red;'>"+" "+word+","+"</span></b>");
					content=content.replaceAll(" "+word.toUpperCase()+",","<b><span style='color:red;'>"+" "+word.toUpperCase()+","+"</span></b>");
					
					content=content.replaceAll(","+word+" ","<b><span style='color:red;'>"+","+word+" "+"</span></b>");
					content=content.replaceAll(","+word.toUpperCase()+" ","<b><span style='color:red;'>"+","+word.toUpperCase()+" "+"</span></b>");
					
					content=content.replaceAll(","+word+",","<b><span style='color:red;'>"+","+word+","+"</span></b>");
					content=content.replaceAll(","+word.toUpperCase()+",","<b><span style='color:red;'>"+","+word.toUpperCase()+","+"</span></b>");
					
					content=content.replaceAll(word+" ","<b><span style='color:red;'>"+word+" "+"</span></b>");
					content=content.replaceAll(word.toUpperCase()+" ","<b><span style='color:red;'>"+word.toUpperCase()+" "+"</span></b>");
					
					content=content.replaceAll(" "+word,"<b><span style='color:red;'>"+" "+word+"</span></b>");
					content=content.replaceAll(" "+word.toUpperCase(),"<b><span style='color:red;'>"+" "+word.toUpperCase()+"</span></b>");
					
					String upperWord=word.substring(0,1).toUpperCase()+word.substring(1);
					content=content.replaceAll(" "+upperWord+" ","<b><span style='color:red;'>"+" "+upperWord+" "+"</span></b>");
					content=content.replaceAll(" "+upperWord+",","<b><span style='color:red;'>"+" "+upperWord+","+"</span></b>");
					content=content.replaceAll(","+upperWord+" ","<b><span style='color:red;'>"+","+upperWord+" "+"</span></b>");
					content=content.replaceAll(","+upperWord+",","<b><span style='color:red;'>"+","+upperWord+","+"</span></b>");
					content=content.replaceAll(" "+upperWord,"<b><span style='color:red;'>"+" "+upperWord+"</span></b>");
					content=content.replaceAll(upperWord+" ","<b><span style='color:red;'>"+upperWord+" "+"</span></b>");
					
				}
			 }
		}
		emailManager.setTempContent(content);
		model.addAttribute("emailManager", emailManager);
		model.addAttribute("emailMap", amazonAccountConfigService.findAccountByEmail());
		return "modules/custom/customEmailForm";
	}
	
	@RequestMapping(value = "view")
	public String view(String all ,CustomEmail emailManager, Model model) {
		if(all!=null){
			model.addAttribute("flag", true);
		}
		
		//查找历史问题信息
		List<Object[]> hisProblems = this.customEmailService.findHistoryEmail(emailManager.getRevertEmail(),null);
		model.addAttribute("hisProblems", hisProblems);
		model.addAttribute("emailManager", emailManager);
		return "modules/custom/customEmailView";
	}

	@RequestMapping(value = "getContent")
	public String getContent(CustomEmail emailManager, Model model) {
		List<String> wordList=Lists.newArrayList("fire","burn","exlode","smoke","crash","remove","negative","free","positive",
			 "compensation", "discount", "money","bank","paypal","gratuit","gratuitement","commentaire","négatif","argent","réduction","banque","supprimer",
			 "retirar","negativo","comentario","dinero","banco","reembolso","descuento","eliminar",
			 "negativ","entfernen","aktualisieren","ändern","kommentar","geld","paypal","entschädigung",
			 "eliminare","rimborsare","rimvuovere","cancellare","migliorare","modificare",
			 "現金","ネガティブレビュー","ネガティブレビューを削除","ディスカウント","賠償","バンク","クレーム");
		String content=emailManager.getReceiveContent();
		if(StringUtils.isNotBlank(content)&&emailManager.getRevertEmail().contains("marketplace.amazon")){
			 for (String word: wordList) {
				if(content.toLowerCase().contains(" "+word+" ")||content.toLowerCase().contains(" "+word+",")
						||content.toLowerCase().contains(","+word+" ")||content.toLowerCase().contains(","+word+",")
						||content.toLowerCase().contains(word+" ")||content.toLowerCase().contains(" "+word)
						){
					
					content=content.replaceAll(" "+word+" ","<b><span style='color:red;'>"+" "+word+" "+"</span></b>");
					content=content.replaceAll(" "+word.toUpperCase()+" ","<b><span style='color:red;'>"+" "+word.toUpperCase()+" "+"</span></b>");
					
					content=content.replaceAll(" "+word+",","<b><span style='color:red;'>"+" "+word+","+"</span></b>");
					content=content.replaceAll(" "+word.toUpperCase()+",","<b><span style='color:red;'>"+" "+word.toUpperCase()+","+"</span></b>");
					
					content=content.replaceAll(","+word+" ","<b><span style='color:red;'>"+","+word+" "+"</span></b>");
					content=content.replaceAll(","+word.toUpperCase()+" ","<b><span style='color:red;'>"+","+word.toUpperCase()+" "+"</span></b>");
					
					content=content.replaceAll(","+word+",","<b><span style='color:red;'>"+","+word+","+"</span></b>");
					content=content.replaceAll(","+word.toUpperCase()+",","<b><span style='color:red;'>"+","+word.toUpperCase()+","+"</span></b>");
					
					content=content.replaceAll(word+" ","<b><span style='color:red;'>"+word+" "+"</span></b>");
					content=content.replaceAll(word.toUpperCase()+" ","<b><span style='color:red;'>"+word.toUpperCase()+" "+"</span></b>");
					
					content=content.replaceAll(" "+word,"<b><span style='color:red;'>"+" "+word+"</span></b>");
					content=content.replaceAll(" "+word.toUpperCase(),"<b><span style='color:red;'>"+" "+word.toUpperCase()+"</span></b>");
					
					String upperWord=word.substring(0,1).toUpperCase()+word.substring(1);
					content=content.replaceAll(" "+upperWord+" ","<b><span style='color:red;'>"+" "+upperWord+" "+"</span></b>");
					content=content.replaceAll(" "+upperWord+",","<b><span style='color:red;'>"+" "+upperWord+","+"</span></b>");
					content=content.replaceAll(","+upperWord+" ","<b><span style='color:red;'>"+","+upperWord+" "+"</span></b>");
					content=content.replaceAll(","+upperWord+",","<b><span style='color:red;'>"+","+upperWord+","+"</span></b>");
					content=content.replaceAll(" "+upperWord,"<b><span style='color:red;'>"+" "+upperWord+"</span></b>");
					content=content.replaceAll(upperWord+" ","<b><span style='color:red;'>"+upperWord+" "+"</span></b>");
					
				}
			 }
		}
		emailManager.setTempContent(content);
		model.addAttribute("customEmail", emailManager);
		return "modules/custom/customEmailcontent";
	}
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "reply")
	public String reply(CustomEmail emailManager,Model model,String country,String checkEmail) {
		//相关邮件
		//String revertEmail = emailManager.getRevertEmail();
		//List<CustomEmail> relativeEmails = customEmailService.findRelativeEmail(revertEmail,emailManager.getId());
		//model.addAttribute("relativeEmails", relativeEmails);
		SendEmail sendEmail = null;
		List<SendEmail> sends =  emailManager.getSendEmails();
		List<SendEmail> sendeds =  Lists.newArrayList(sends);
		if(sends!=null&&sends.size()>0){
			if("0".equals(sends.get(0).getSendFlag())){
				sendEmail = sendeds.remove(0);
			}
		}
		if(sendEmail == null ){
			Set<String> ordersStr = getOrders(emailManager.getSubject());
			ordersStr.addAll(getOrders(emailManager.getReceiveContent()));
			List<AmazonOrder> orders = Lists.newArrayList();
			sendEmail = new SendEmail();
			for (String ordStr : ordersStr) {
				AmazonOrder order = orderService.findByLazy(ordStr);
				if(order!=null){
					orders.add(order);
					sendEmail.setOrderId(order.getAmazonOrderId());
				}
			}
			sendEmail.setType("1");
			sendEmail.setCustomEmail(emailManager);
			sendEmail.setCreateBy(emailManager.getMasterBy());
			Signature signature = signatureService.get(emailManager.getMasterBy().getId());
			sendEmail.setSendContent("");
			if(sendeds.size()==0){
				sendEmail.setSendSubject("RE:"+emailManager.getSubject());
				if(signature!=null&&signature.getSignatureContent().length()>0){
					sendEmail.setSendContent("<br/><br/><br/><br/>--------------------<br/>"+signature.getSignatureContent());
				}
				sendEmail.setSendContent(sendEmail.getSendContent()
						+"<br/>------------------ Original ------------------<br/>"+emailManager.getReceiveContent());
				String email = emailManager.getRevertEmail();
				String temp = email.toLowerCase().replace("-","").replace("_","");
				if(temp.contains("notreply")||temp.contains("noreply")){
					email="";
				}
				StringBuffer sb = new StringBuffer("");
				Set<String> set = Sets.newHashSet();
				if(orders.size()>0){
					for (AmazonOrder amazonOrder : orders) {
						if(null!= amazonOrder.getBuyerEmail()){
							if(!email.contains(amazonOrder.getBuyerEmail()) && !set.contains(amazonOrder.getBuyerEmail())){
								sb.append(","+amazonOrder.getBuyerEmail());
							}
							set.add(amazonOrder.getBuyerEmail());
						}
					}
				}
				sendEmail.setSendEmail(email+sb.toString());
			}else{
				SendEmail sended = sendeds.get(sendeds.size()-1);
				sendEmail.setSendSubject("RE:"+sended.getSendSubject());
				if(signature!=null&&signature.getSignatureContent().length()>0){
					sendEmail.setSendContent("<br/><br/><br/><br/>--------------------<br/>"+signature.getSignatureContent());
				}
				sendEmail.setSendContent(sendEmail.getSendContent()
						+"<br/>------------------ Original ------------------<br/>"+sended.getSendContent());
				sendEmail.setSendEmail(sended.getSendEmail());
				sendEmail.setCcToEmail(sended.getCcToEmail());
				sendEmail.setBccToEmail(sended.getBccToEmail());
			}
			sendEmail.setServerEmail(emailManager.getRevertServerEmail());
			sendEmailService.save(sendEmail);
		}else{
			Set<String> ordersStr = getOrders(emailManager.getSubject());
			ordersStr.addAll(getOrders(emailManager.getReceiveContent()));
			for (String ordStr : ordersStr) {
				AmazonOrder order = orderService.findByLazy(ordStr);
				if(order!=null){
					sendEmail.setOrderId(order.getAmazonOrderId());
					break;
				}
			}
		}
		
		//生成邮件模板
		User user = UserUtils.getUser();
		List<CustomEmailTemplate> templates = this.customEmailTemplateService.find(user);
		/*
        List<String> orders = getOrders2(sendEmail.getSendSubject()); 
        if(orders.size()==0){
        	orders = getOrders2(sendEmail.getSendContent());
        }
        AmazonOrder amazonOrder = null;
        if(orders.size()!=0){
            amazonOrder = this.orderService.findByLazy(orders.get(0));
        }else{
        	amazonOrder = new  AmazonOrder();
        }
            model.addAttribute("amazonOrder",amazonOrder);
        */
		   
		if(StringUtils.isBlank(sendEmail.getServerEmail())){
			sendEmail.setServerEmail(emailManager.getRevertServerEmail());
		}
		model.addAttribute("templates", templates);
		model.addAttribute("customEmailId", emailManager.getId());
		model.addAttribute("sendEmail", sendEmail);
		model.addAttribute("sendeds", sendeds);
		model.addAttribute("revertEmail", emailManager.getRevertEmail());
		if(StringUtils.isBlank(country)){
			country = emailManager.getRevertEmail().substring(emailManager.getRevertEmail().lastIndexOf(".") + 1);
		}
		if("2".equals(checkEmail)/*&&"uk,com,ca".contains(country)*/){//需审核
			List<User> userList=systemService.hasPerssion("custom:emailCheck:"+country);
			model.addAttribute("userList", userList);
			model.addAttribute("checkEmail",checkEmail);
			model.addAttribute("checkCountry",country);
		}else{
			model.addAttribute("checkEmail","3");
		}
		model.addAttribute("emailCountry",country);
		model.addAttribute("emailMap",amazonAccountConfigService.findAccountByEmail());
		return "modules/custom/customEmailReply";
	}
	
	

	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "transmit")
	public String transmit(String customKey ,CustomEmail emailManager, Model model, RedirectAttributes redirectAttributes) {
		User cuser = UserUtils.getUser();
		if(!emailManager.getMasterBy().getId().equals(cuser.getId())){
			if(!SecurityUtils.getSubject().isPermitted("custom:email:proxy")){
				throw new RuntimeException("Email is masterBy "+emailManager.getMasterBy().getName()+",You do not have permission！");
			}
		}	
		User user = CustomEmailMonitor.getMaster(customKey, customEmailService);
		while(user.getId().equals(emailManager.getMasterBy().getId())){
			user =  CustomEmailMonitor.getMaster(customKey, customEmailService);
		}
		emailManager.setAnswerDate(null);
		emailManager.setState("0");
		DateFormat formatDay = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		if(cuser.getName().equals(emailManager.getMasterBy().getName())){
			emailManager.setTransmit(emailManager.getMasterBy().getName()+"-->"+user.getName()+"["+formatDay.format(new Date())+"]");
		}else{
			emailManager.setTransmit(emailManager.getMasterBy().getName()+"-->"+user.getName()+"[operator:"+cuser.getName()+";"+formatDay.format(new Date())+"]");
		}
		
		emailManager.setMasterBy(user);
		emailManager.setCreateBy(user);
		customEmailService.save(emailManager);
		if ("02b7f0e4f8f4416bb0b55b95812d7cec".equals(cuser.getId())) {
			logger.info("susie Transmit:" + emailManager.getId());
		}
		addMessage(redirectAttributes, "Forward Email Successful,masterBy:'" +user.getName()+"'");
		return "redirect:"+Global.getAdminPath()+"/custom/emailManager/?repage";
	}
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "transmitOther")
	public String transmitOther(String userId ,CustomEmail emailManager, Model model, RedirectAttributes redirectAttributes) {
		User cuser = UserUtils.getUser();
		if(!emailManager.getMasterBy().getId().equals(cuser.getId())){
			if(!SecurityUtils.getSubject().isPermitted("custom:email:proxy")){
				throw new RuntimeException("Email is masterBy "+emailManager.getMasterBy().getName()+",You do not have permission！");
			}
		}
		User user = UserUtils.getUserById(userId);
		emailManager.setAnswerDate(null);
		emailManager.setState("0");
		DateFormat formatDay = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		if(cuser.getName().equals(emailManager.getMasterBy().getName())){
			emailManager.setTransmit(emailManager.getMasterBy().getName()+"-->"+user.getName()+"["+formatDay.format(new Date())+"]");
		}else{
			emailManager.setTransmit(emailManager.getMasterBy().getName()+"-->"+user.getName()+"[operator:"+cuser.getName()+";"+formatDay.format(new Date())+"]");
		}
		
		emailManager.setMasterBy(user);
		emailManager.setCreateBy(user);
		customEmailService.save(emailManager);
		if ("02b7f0e4f8f4416bb0b55b95812d7cec".equals(cuser.getId())) {
			logger.info("susie Transmit:" + emailManager.getId());
		}
		addMessage(redirectAttributes, "Forward Email Successful,masterBy:'" +user.getName()+"'");

		if(emailManager.getRevertEmail().contains("@amazon.")){
			List<String> temp=Lists.newArrayList();;
			temp.add("<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/emailManager/view?id="+emailManager.getId()+"'>"+emailManager.getSubject()+"</a>");
			sendCheckEmail(user.getEmail(),temp);
		}
		
		return "redirect:"+Global.getAdminPath()+"/custom/emailManager/?repage";
	}
	
	
	
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "batchtransmitOther")
	public String batchtransmitOther(@RequestParam("eid[]")String[] eid,String userId , Model model, RedirectAttributes redirectAttributes) {
		User user = UserUtils.getUserById(userId);
		User cuser = UserUtils.getUser();
		Map<String,List<String>> map=Maps.newHashMap();
		for (String id : eid) {
			CustomEmail emailManager = customEmailService.get(id);
			if(!emailManager.getMasterBy().getId().equals(cuser.getId())){
				if(!SecurityUtils.getSubject().isPermitted("custom:email:proxy")){
					continue;
				}
			}
			emailManager.setAnswerDate(null);
			emailManager.setState("0");
			DateFormat formatDay = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
			if(cuser.getName().equals(emailManager.getMasterBy().getName())){
				emailManager.setTransmit(emailManager.getMasterBy().getName()+"-->"+user.getName()+"["+formatDay.format(new Date())+"]");
			}else{
				emailManager.setTransmit(emailManager.getMasterBy().getName()+"-->"+user.getName()+"[operator:"+cuser.getName()+";"+formatDay.format(new Date())+"]");
			}
			
			emailManager.setMasterBy(user);
			emailManager.setCreateBy(user);
			customEmailService.save(emailManager);
			
			if(emailManager.getRevertEmail().contains("@amazon.")){
				List<String> temp=map.get(user.getEmail());
				if(temp==null){
					temp=Lists.newArrayList();
					map.put(user.getEmail(), temp);
				}
				temp.add("<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/emailManager/view?id="+emailManager.getId()+"'>"+emailManager.getSubject()+"</a>");
			}
		}
		if ("02b7f0e4f8f4416bb0b55b95812d7cec".equals(cuser.getId())) {
			logger.info("susie Transmit:" + Arrays.toString(eid));
		}
		addMessage(redirectAttributes, "Forward Email Successful,masterBy:'" +user.getName()+"'");
		if(map!=null&&map.size()>0){
			for ( Map.Entry<String,List<String>> entry: map.entrySet()) {
				sendCheckEmail(entry.getKey(),entry.getValue());
			}
		}
		return "redirect:"+Global.getAdminPath()+"/custom/emailManager/?repage";
	}
	
	
	private  boolean sendCheckEmail(String email,List<String> emailList){
		String toAddress =email;
		StringBuffer content= new StringBuffer("");

		content.append("Hi,The following mail need to be processed<br/><br/>");
		
		for (String link: emailList) {
			content.append(link+"<br/><br/>");
		}
		
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();   
			final MailInfo mailInfo = new MailInfo(toAddress,"Please log in erp to process mail"+DateUtils.getDate("-yyyy/MM/dd"),date);
			mailInfo.setContent(content.toString());
			new Thread(){
				public void run(){   
					 mailManager.send(mailInfo);
				}
			}.start();
		}
		return true;
	}
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "batchNoply")
	public String batchNoply(@RequestParam("eid[]")String[] eid, Model model, RedirectAttributes redirectAttributes) {
		final List<Message> rs = Lists.newArrayList();
		User cuser = UserUtils.getUser();
		for (String ide : eid) {
			final CustomEmail emailManager = customEmailService.get(ide);
			if(emailManager==null){
				continue;
			}
			if(!emailManager.getMasterBy().getId().equals(cuser.getId())){
				if(!SecurityUtils.getSubject().isPermitted("custom:email:proxy")){
					continue;
				}else{
					emailManager.setMasterBy(cuser);
				}
			}
			emailManager.setEndDate(new Date());
			emailManager.setState("4");
			Matcher matcher = MSG_ID_PATTERN.matcher(emailManager.getReceiveContent());
			String msgid = "";
			String country = emailManager.getRevertEmail().substring(emailManager.getRevertEmail().lastIndexOf(".")+1);
			while(matcher.find()){
				msgid = matcher.group();
				break;
			}
			if(StringUtils.isNotBlank(msgid)){
				msgid = msgid.split(":")[1];
				msgid = msgid.substring(0,msgid.length()-1);
				final String mid = msgid;
				final String countryStr = country;
				final String id = emailManager.getId();
				emailManager.setResult("Please wait a moment!");
				rs.add(new Message(id,countryStr,mid,emailManager.getCustomSendDate().getTime()));
			}
			customEmailService.save(emailManager);
		}
	/*	new Thread(){
			public void run() {
				List<CustomEmail> list = customEmailService.findNoreply();
				rs.addAll(Collections2.transform(list, new Function<CustomEmail, Message>() {
					public Message apply(CustomEmail input) {
						Matcher matcher = MSG_ID_PATTERN.matcher(input.getReceiveContent());
						String msgid = "";
						String country = input.getRevertEmail().substring(input.getRevertEmail().lastIndexOf(".")+1);
						while(matcher.find()){
							msgid = matcher.group();
							break;
						}
						if(StringUtils.isNotBlank(msgid)){
							msgid = msgid.split(":")[1];
							msgid = msgid.substring(0,msgid.length()-1);
							Message message =  new Message(input.getId(),country,msgid,input.getCustomSendDate().getTime());
							return message;
						}	
						return null;
					};
				}));
				processNoreply(rs);
			};
		}.start();*/
		addMessage(redirectAttributes, "Email status has been changed to no treatment");
		return "redirect:"+Global.getAdminPath()+"/custom/emailManager/?repage";
	}
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "noreply")
	public String noreply(final CustomEmail emailManager, Model model, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
		User cuser = UserUtils.getUser();
		if(!emailManager.getMasterBy().getId().equals(cuser.getId())){
			if(!SecurityUtils.getSubject().isPermitted("custom:email:proxy")){
				throw new RuntimeException("Email is masterBy "+emailManager.getMasterBy().getName()+",You do not have permission！");
			}else{
				emailManager.setMasterBy(cuser);
			}
		}	
		
		//保存备注
		if(StringUtils.isNotEmpty(emailManager.getRemarks())){
			emailManager.setRemarks(URLDecoder.decode(emailManager.getRemarks(), "UTF-8"));
		}
		emailManager.setEndDate(new Date());
		/*Matcher matcher = MSG_ID_PATTERN.matcher(emailManager.getReceiveContent());
		String msgid = "";
		String country = emailManager.getRevertEmail().substring(emailManager.getRevertEmail().lastIndexOf(".")+1);
		while(matcher.find()){
			msgid = matcher.group();
			break;
		}
		if(StringUtils.isNotBlank(msgid)){
			msgid = msgid.split(":")[1];
			msgid = msgid.substring(0,msgid.length()-1);
			final String mid = msgid;
			final String countryStr = country;
			final String id = emailManager.getId();
			emailManager.setResult("Please wait a moment!");
			new Thread(){
				public void run() {
					List<CustomEmail> list = customEmailService.findNoreply();
					Message msg = new Message(id,countryStr,mid,emailManager.getCustomSendDate().getTime());
					List<Message> rs = Lists.newArrayList();
					rs.add(msg);
					rs.addAll(Collections2.transform(list, new Function<CustomEmail, Message>() {
						public Message apply(CustomEmail input) {
							Matcher matcher = MSG_ID_PATTERN.matcher(input.getReceiveContent());
							String msgid = "";
							String country = input.getRevertEmail().substring(input.getRevertEmail().lastIndexOf(".")+1);
							while(matcher.find()){
								msgid = matcher.group();
								break;
							}
							if(StringUtils.isNotBlank(msgid)){
								msgid = msgid.split(":")[1];
								msgid = msgid.substring(0,msgid.length()-1);
								Message message =  new Message(input.getId(),country,msgid,input.getCustomSendDate().getTime());
								return message;
							}	
							return null;
						};
					}));
					processNoreply(rs);
				};
			}.start();
		}*/
		customEmailService.save(emailManager);
		addMessage(redirectAttributes, "Email status has been changed to no treatment!");
		return "redirect:"+Global.getAdminPath()+"/custom/emailManager/?repage";
	}
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "process")
	public String process(CustomEmail emailManager, Model model, RedirectAttributes redirectAttributes,String checkEmail) {
		emailManager.setEndDate(null);
		emailManager.setResult("");
		emailManager.setState("1");
		customEmailService.save(emailManager);
		return "redirect:"+Global.getAdminPath()+"/custom/emailManager/reply?id="+emailManager.getId()+"&checkEmail="+checkEmail;
	}
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "recall")
	public String recall(CustomEmail emailManager, Model model, RedirectAttributes redirectAttributes,String checkEmail) {
		return "redirect:"+Global.getAdminPath()+"/custom/emailManager/reply?id="+emailManager.getId()+"&checkEmail="+checkEmail;
	}
	
	@RequiresPermissions("custom:email:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		customEmailService.delete(id);
		addMessage(redirectAttributes, "Email has deleted");
		return "redirect:"+Global.getAdminPath()+"/custom/emailManager/?repage";
	}

	
	@RequestMapping("/download")   
    public ModelAndView download(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/customEmail/";  
        String downLoadPath = ctxPath + fileName;
        fileName = fileName.substring(fileName.lastIndexOf("/")+1);
        try {   
            long fileLength = new File(downLoadPath).length();   
            response.setContentType("application/x-msdownload;");   
          /*  response.setHeader("Content-disposition", "attachment; filename="  
                    + URLEncoder.encode(fileName,"utf-8"));   */
            response.setHeader("Content-disposition", "attachment; filename="  
                    + fileName);
            response.setHeader("Content-Length", String.valueOf(fileLength));   
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));   
            bos = new BufferedOutputStream(response.getOutputStream());   
            byte[] buff = new byte[2048];   
            int bytesRead;   
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {   
                bos.write(buff, 0, bytesRead);   
            }   
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
	
	@ResponseBody
	@RequestMapping(value = "update")
	public String updateCache() {
		CustomEmailMonitor.initMasterUsers(customEmailService,true);
		return "1";
	}
	
	@Autowired
	private UnsubscribeEmailService unsubscribeEmailService;
	
	@ResponseBody
	@RequestMapping(value = "unsubscribeEmail")
	public String unsubscribeEmail(UnsubscribeEmail email) {
		if(unsubscribeEmailService.isNotExist(email.getCustomEmail())){
			email.setCreateDate(new Date());
			unsubscribeEmailService.save(email);
		}
		return "1";
	}
	
	@ResponseBody
	@RequestMapping(value = "saveMark")
	public String saveMark(CustomEmail emailManager) {
		customEmailService.save(emailManager);
		return "1";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "update2")
	public String update() {
		List<CustomEmail> emails = customEmailService.findExceptionEmail();
		for (CustomEmail email : emails) {
			String emailStr = email.getRevertEmail();
			String key = emailStr.substring(emailStr.lastIndexOf(".")+1);
			User user = CustomEmailMonitor.getMaster(key, customEmailService);
			if("1".equals(email.getState())){
				email.setAnswerDate(null);
				email.setState("0");
			}
			email.setTransmit(email.getMasterBy().getName()+"Turnover["+new Date().toLocaleString()+"]");
			email.setMasterBy(user);
			email.setCreateBy(user);
			customEmailService.save(email);
		}
		return "1";
	}
	
	/*private void processNoreply(final List<Message> msgs){
		new Thread(){
			public void run() {
				int flag = 0 ;
				while(flag >= 0 && flag <=10){
					try {
						request.sendMessageID("qweqwr*$@#12314",msgs);
						flag = -1;
					} catch (Exception e) {
						flag++;
						try {
							Thread.sleep(60000);
						} catch (InterruptedException e1) {}
					}
				}
			};
		}.start();
	}*/
	
	public static Set<String> getOrders(String input){
		Set<String> rs = Sets.newHashSet();
		if(StringUtils.isNotEmpty(input)){
			Matcher matcher = ORDER_PATTERN.matcher(input);
			while(matcher.find()){
				rs.add(matcher.group());
			}
		}
		return rs;
	}  
	
	
	public static List<String> getOrders2(String input){
		List<String> rs = Lists.newArrayList();
		if(StringUtils.isNotEmpty(input)){
			Matcher matcher = ORDER_PATTERN.matcher(input);
			while(matcher.find()){
				rs.add(matcher.group());
			}
		}
		return rs;
	}  
	
	//保存问题信息
	@ResponseBody
	@RequestMapping(value = {"saveProblem"})
	public String saveProblem(String country,String productName,String  problemType,String problem,String id,String orderNos){
		try{
			this.customEmailService.saveProblem(country, productName, problemType, URLDecoder.decode(problem, "utf-8"), id,orderNos);
		}catch(Exception ex){
			return "保存问题失败："+ex.getMessage();
		}
		return "保存问题成功！！！";
	
	}
	
	@RequestMapping(value = {"export"})
	public String export(CustomEmail customEmail, HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) {
		try {
            String fileName = "customEmailCountData"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
            Map<String, Map<String, String>> data =  customEmailService.count(customEmail);
    		Map<String, Map<String, String>> data1 = sendEmailService.count(customEmail);
    		for (Entry<String, Map<String, String>> entry : data.entrySet()) {
    			String key = entry.getKey();
    			Map<String, String> temp = data1.get(key);
    			if(temp!=null){
    				if(data.get(key)==null){
    					data.put(key, temp);
    				}else{
    					data.get(key).putAll(temp);
    				}
    			}
    		}
    		for (Entry<String, Map<String, String>> entry : data1.entrySet()) {
    			String key = entry.getKey();
    			Map<String, String> temp = data.get(key);
    			if(null!=temp){
    				if(data.get(key)==null){
    					data.put(key, temp);
    				}else{
    					data.get(key).putAll(temp);
    				}
    			}
    		}
    		DateFormat format = new SimpleDateFormat("yyyyMMdd");
            ExportExcel excel = new ExportExcel("CustomEmailCountData("+format.format(customEmail.getCreateDate())+"-"+format.format(customEmail.getEndDate())+")",new String[]{"负责人","有效邮件","回复邮件","邮件回复率(%)","发送邮件","未回复邮件","无效(不处理)邮件","平均响应时间","平均编辑时间","平均每天发送邮件","平均每天回复邮件"});
            long count =1+(customEmail.getEndDate().getTime()-customEmail.getCreateDate().getTime())/(24 * 60 * 60 * 1000);
            for (Map<String, String> rowData : data.values()) {
            	Row row = excel.addRow();
				int col = 0 ;
				excel.addCell(row, col++, rowData.get("user"));
				String noFor = rowData.get("noFor") ==null?"0":rowData.get("noFor");
				String two = rowData.get("two") ==null?"0":rowData.get("two");
				String send = rowData.get("sendEmail") ==null?"0":rowData.get("sendEmail");
				String forE = rowData.get("for") ==null?"0":rowData.get("for");
				String avg = "";
				if(rowData.get("avg") ==null){
					avg ="Miss";
				}else{
					double temp  = Double.parseDouble(rowData.get("avg"))/(double)3600;
					BigDecimal   bd   =   new   BigDecimal(temp);
					temp   = bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					avg = temp+"h";
				}
				String avgResp = "";
				if(rowData.get("resp") ==null){
					avgResp ="Miss";
				}else{
					double temp  = Double.parseDouble(rowData.get("resp"))/(double)3600;
					BigDecimal bd = new BigDecimal(temp);
					temp   = bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					avgResp = temp+"h";
				}
				
				String avgS = "";
				if(rowData.get("sendEmail") ==null){
					avgS ="0";
				}else{
					double temp  = Double.parseDouble(rowData.get("sendEmail"))/(double)count;
					BigDecimal   bd   =   new   BigDecimal(temp);
					temp   = bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					avgS = temp+"";
				}
				
				String avgRe = "";
				if(rowData.get("two") ==null){
					avgRe ="0";
				}else{
					double temp  = Double.parseDouble(rowData.get("two"))/(double)count;
					BigDecimal   bd   =   new   BigDecimal(temp);
					temp   = bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					avgRe = temp+"";
				}
				
				String prcent = "";
				if("0".equals(noFor)){
					prcent = "0%";
				}else{
					double num =  (Double.parseDouble(two)/Double.parseDouble(noFor))*100;
					BigDecimal   bd   =   new   BigDecimal(num);
					num   = bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					prcent = num+"%";
				}
				String noProess = (Integer.parseInt(noFor)-Integer.parseInt(two))+"";
				excel.addCell(row, col++, noFor);
				excel.addCell(row, col++, two);
				excel.addCell(row, col++, prcent);
				if(StringUtils.isNotEmpty(send)&&StringUtils.isNotEmpty(two)){
					try {
						int setInt = Integer.parseInt(send) - Integer.parseInt(two);
						send = setInt+"";
					} catch (Exception e) {}
				}
				excel.addCell(row, col++, send);
				excel.addCell(row, col++, noProess);
				excel.addCell(row, col++, forE);
				excel.addCell(row, col++, avgResp);
				excel.addCell(row, col++, avg);
				excel.addCell(row, col++, avgRe);
				excel.addCell(row, col++, avgS);
			}
            excel.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "Export error！error Info："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/custom/emailManager/count?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateRemark"})
	public String updateRemark(String id,String remark) throws UnsupportedEncodingException {
		if(StringUtils.isNotEmpty(remark)){
			this.customEmailService.saveRemark(URLDecoder.decode(remark, "UTF-8"), id);
		}
		return "true";
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateFollow"})
	public  String updateFollow(String followState,String followDate,String id) throws ParseException{

		if(StringUtils.isNotBlank(followDate)){
			customEmailService.followEmail(followState,new SimpleDateFormat("yyyy-MM-dd").parse(followDate),id);
		}else{
			customEmailService.followEmail(followState,null,id);
		}
		
		return "0";
	}
}
