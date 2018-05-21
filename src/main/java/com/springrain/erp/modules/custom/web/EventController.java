package com.springrain.erp.modules.custom.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.UUID;

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
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.email.MailManagerInfo;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.MessageUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.ZipUtil;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsDetail;
import com.springrain.erp.modules.amazoninfo.entity.AmazonReviewer;
import com.springrain.erp.modules.amazoninfo.entity.ReviewerEmail;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderExtract;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderItem;
import com.springrain.erp.modules.amazoninfo.scheduler.SendEmailByOrderMonitor;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerFilterService;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsDetailService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.AmazonReviewerService;
import com.springrain.erp.modules.amazoninfo.service.ReviewerEmailService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.custom.entity.Comment;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.custom.scheduler.CustomEmailManager;
import com.springrain.erp.modules.custom.service.CommentService;
import com.springrain.erp.modules.custom.service.CustomProductProblemService;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.custom.service.SendEmailService;
import com.springrain.erp.modules.custom.service.UnsubscribeEmailService;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.OfficeService;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.server.webservice.ICommentRequest;

/**
 * 事件Controller
 * @author tim
 * @version 2014-05-21
 */
@Controller
@RequestMapping(value = "${adminPath}/custom/event")
public class EventController extends BaseController {
	@Autowired
	private EventService eventService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private AmazonProductService amazonProductService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private AmazonOrderService orderService;
	
	@Autowired
	private PsiProductService psiProductService;

	@Autowired
	private ReviewerEmailService reviewerEmailService;

	@Autowired
	private AmazonReviewerService amazonReviewerService;
	
	@Autowired
	private CustomEmailManager customEmailManager;
	
	@Autowired
	private SendEmailService sendEmailService;
	
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	
	@Autowired
	private CustomEmailManager sendCustomEmailManager;
	
	@Autowired
	private AmazonPostsDetailService amazonPostsDetailService;
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	@Autowired
	private AmazonOrderService amazonOrderService;
	@Autowired
	private  AmazonCustomerFilterService amazonCustomerFilterService;
	@Autowired
	private CustomProductProblemService	 customProductProblemService;
	@Autowired
	private PsiProductTypeGroupDictService psiTypeGroupService;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@ModelAttribute
	public Event get(@RequestParam(required=false)Integer id) {
		if (id!=null && id>0){
			return eventService.get(id);
		}else{
			return new Event();
		}
	}
	
	@RequiresPermissions("custom:event:view")
	@RequestMapping(value = {"list", ""})
	public String list(Event event, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(event.getCreateDate()==null){
			event.setCreateDate(DateUtils.addMonths(today,-1));
			event.setEndDate(today);
		}
		User user = UserUtils.getUser();
		if (!user.isAdmin()&& event.getMasterBy()==null){
				event.setMasterBy(user);
		}; 
		Page<Event> page = new Page<Event>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("id desc");
		}else{
			page.setOrderBy(orderBy+",id desc");
		}	
        page = eventService.find(page, event); 
        page.setOrderBy(orderBy);
        List<User> all =systemService.findAllUsers();
        List<String> products = Lists.newArrayList();
        List<String> countComment = Lists.newArrayList();
        for (Event eve : page.getList()) {
        	long num = commentService.findNewSysComment(eve);
        	long num1 = commentService.findStarChangeComment(eve);
        	long num2 = commentService.findAddChangeComment(eve);
        	if(num>0){
        		if(num2>0){
        			countComment.add(MessageUtils.format("custom_event_sysNote",new Object[]{num,num1})+"[1 review]");
        		}else{
        			countComment.add(MessageUtils.format("custom_event_sysNote",new Object[]{num,num1}));
        		}
        	}else{
                if(num2>0){
                	countComment.add("[1 review]");
        		}else{
        			countComment.add(null);
        		}
        	}
        	String name = "unknown";
        	if(StringUtils.isNotEmpty(eve.getRemarks())&&StringUtils.isNotEmpty(eve.getCountry())){
        		String temp = amazonProductService.findProductName(eve.getRemarks(), eve.getCountry());
            	if(StringUtils.isNotEmpty(temp)){
            		String[] temp1 = temp.split(" ");
            		if(temp1.length>1){
            			name = temp1[1];
            			if(temp1.length>2){
            				name =name+" "+temp1[2];
            			}
            		}else{
            			name = temp;
            		}
            	}
        	}
        	products.add(name);
		}

		List<Office> offices = Lists.newArrayList();
		String state = event.getState();
		if(StringUtils.isEmpty(state) || (state.equals("1")|| state.equals("0"))){
			offices.addAll(officeService.findAll());
		}
        boolean canProcess = true;
		if (!user.isAdmin()){
			if(!UserUtils.hasPermission("event:distribution:all")){//所有事件分配
				canProcess = user.getId().equals(event.getMasterBy().getId()) ;
			}
		}
		model.addAttribute("canProcess", canProcess);
		model.addAttribute("offices", offices);
		
        model.addAttribute("all", all);
        model.addAttribute("products", products);
        model.addAttribute("page", page);
        model.addAttribute("cuser", user);
        model.addAttribute("countComment", countComment);
    	model.addAttribute("groupType", psiTypeGroupService.getAllList());
		return "modules/custom/eventList";
	}
	
	
	@RequestMapping(value = {"problemList"})
	public String problemList(Event event, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date today =sdf.parse(sdf.format(new Date()));
		if (event.getAnswerDate() == null) {
			event.setAnswerDate(DateUtils.addMonths(today, -1));
		}
		Page<Event> page = new Page<Event>(request, response);
		page.setOrderBy("answerDate desc");
        page = eventService.findProblems(page, event); 
        model.addAttribute("productMap", this.psiProductService.findProductTypeMap());
		model.addAttribute("mangerMap", this.psiProductService.findManagerProductTypeMap());//查询产品经理
        model.addAttribute("page", page);
		return "modules/custom/eventProblemList";
	}
	
	
	@RequestMapping(value = "expProblem")
	public String expProblem(Event event,HttpServletRequest request,HttpServletResponse response, Model model) {
		Page<Event> page = new Page<Event>(request, response);
		page.setPageSize(60000);
		page.setOrderBy("answerDate desc");
        List<Event>  events = this.eventService.findProblems(page,event).getList(); 
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
		  List<String> title=Lists.newArrayList("Product Name","Product Line","Problem Type", "Problem Detail",  "Product Manager","Order Nos");
		  for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
		 int rowIndex=1;
         if(events!=null){
	    	for (int i=0;i<events.size();i++) {
	    		Event  eve=events.get(i);
    			int j=0;
	    		row=sheet.createRow(rowIndex++);
	    		row.setHeight((short) 400);
	    		String type =productMap.get(eve.getProductName());
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(eve.getProductName());
                row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(type);
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(eve.getProblemType());
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(eve.getReason());
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(mangerMap.get(type));
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(eve.getInvoiceNumber());
	    	}
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
				String fileName = "eventProblems" + sdf.format(new Date()) + ".xls";
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
	public String count(Event event, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(event.getCreateDate()==null){
			event.setCreateDate(DateUtils.addMonths(today,-1));
			event.setEndDate(today);
		}
		model.addAttribute("date", eventService.count(event));
		return "modules/custom/eventCountList";
	}

	@RequiresPermissions("custom:event:view")
	@RequestMapping(value = "form")
	public String form(Event event, Model model) {
		User user = UserUtils.getUser();
		String state = event.getState();
		if(event.getMasterBy()==null){
			throw new RuntimeException(MessageUtils.format("custom_event_exception"));
		}
		if(state!=null && state.equals("0")&&user.getId().equals(event.getMasterBy().getId())){
			//event.setState("1");
			event.setAnswerDate(new Date());
			eventService.save(event);
		}
		List<Office> offices = Lists.newArrayList();
		state = event.getState();
		if(state!=null && (state.equals("1")|| state.equals("0"))){
			offices.addAll(officeService.findAll());
		}
		boolean canProcess = true;
		boolean canEdit = true;
		if (!user.isAdmin()){
			if(!UserUtils.hasPermission("event:distribution:all")){//所有事件分配
				canProcess = user.getId().equals(event.getMasterBy().getId()) ;
			}
			canEdit = user.getId().equals(event.getMasterBy().getId()) || user.getId().equals(event.getCreateBy().getId()) ;
		}
		String productName = amazonProductService.findProductName(event.getRemarks(), event.getCountry());
		if(StringUtils.isNotEmpty(productName)){
			//放入产品属性
			model.addAttribute("productEliminate", psiProductEliminateService.findProductEliminateByProductName(productName, null, event.getCountry()));
			//召回事件
    		if ("10".equals(event.getType())) {
    			PsiProduct psiProduct = psiProductService.findProductByName(productName);
    			if (psiProduct != null && psiProduct.getWeight() != null) {
    				float wg = psiProduct.getWeight().floatValue()*event.getProductQuantity()/1000;
    				model.addAttribute("productWg", String.format("%.2f", wg));	//产品总重量
				}
    			String orderId = event.getInvoiceNumber();
    			if (StringUtils.isNotEmpty(orderId)) {
    				AmazonOrder order = orderService.findByLazy(orderId);
    				if (order != null) {
    					AmazonAddress address = order.getShippingAddress();
    					String customer_name=address.getName();
    					String shippAd = address.getAddressLine1() == null ? "" : address.getAddressLine1();
    					shippAd += (address.getAddressLine2() == null ? "" : " " + address.getAddressLine2());
    					shippAd += (address.getAddressLine3() == null ? "" : " " + address.getAddressLine3());
    					String city = address.getCity()==null?"":address.getCity();
    					String stateOrRegion = address.getStateOrRegion()==null?"":address.getStateOrRegion();
    					String postcode = address.getPostalCode();
    					String country = address.getCountryCode();
    					model.addAttribute("customerAddress", customer_name + "," + shippAd +  "," + city +
    							"," + stateOrRegion +  "," +  country +  "," + postcode);
    				}
    			}
    		}
    		String[] temp1 = productName.split(" ");
    		if(temp1.length>1){
    			productName = temp1[1];
    			if(temp1.length>2){
    				productName =productName+" "+temp1[2];
    			}
    		}
    	}else{
    		if(StringUtils.isEmpty(event.getRemarks()))
    			productName = MessageUtils.format("custom_event_note");
    		else
    			productName = "unknown";
    	}
		
		model.addAttribute("canProcess", canProcess);
		model.addAttribute("canEdit", canEdit);
		model.addAttribute("offices", offices);
		model.addAttribute("event", event);
		model.addAttribute("productName", productName);
		model.addAttribute("cuser", user);
		if ("1".equals(event.getType())||"2".equals(event.getType())) {
			if(StringUtils.isNotBlank(event.getCustomId())){
				String returnInfo=amazonCustomerFilterService.getReasonByCustomerId(event.getCustomId());
				if(StringUtils.isNotBlank(returnInfo)){
					model.addAttribute("returnInfo", returnInfo);
				}
			}
		   
		}
		return "modules/custom/eventForm";
	}
	
	@ResponseBody
	@RequestMapping(value = "titleByName")
	public  Map<String,String> titleByName(String orderId,String country, Model model) {
		AmazonOrder order = orderService.findByLazy(orderId);
		Map<String,String> titleByName=Maps.newHashMap();
		if(order!=null){
			for (AmazonOrderItem item: order.getItems()) {
				 AmazonPostsDetail detail=amazonPostsDetailService.getDetailByAsinAndCountry(country,item.getAsin());
				 if(detail!=null){
					 titleByName.put(item.getName(),detail.getTitle());
				 }
			}
		}
		return titleByName;
	}	
	
	@RequestMapping(value = "emailNotice")
	public String emailNotice(Event event, RedirectAttributes redirectAttributes) {
		if ("10".equals(event.getType()) && !"1".equals(event.getEmailNotice())) {
			String toEmail = event.getCustomEmail();
			if (StringUtils.isNotEmpty(toEmail)) {
				String productName = amazonProductService.findProductName(event.getRemarks(), event.getCountry());
				if(StringUtils.isNotEmpty(productName)){
					String content = "Dear customer,<br/><br/>"+
						"Greetings from Inateck Customer Service!<br/><br/>"+
						"In order to recall product "+productName+", we have sent you a prepaid label in another email " +
						"just now. Would you please print it and send the product back to us? Please mention " +
						"ID \"SPR-"+event.getId()+"\" in a piece of paper and put it into the parcel so that " +
						"we can well track the event when the parcel arrives.<br/><br/>"+
						"Thank you very much for your kind gesture.<br/><br/>"+
						"Best regards,<br/>"+
						"Inateck Customer Service";
					SendEmail sendEmail = new SendEmail("Product Recall",content,toEmail,"0");
					MailInfo mailInfo = sendEmail.getMailInfo();
					User user = UserUtils.getUser();
					sendEmail.setCreateBy(user);

					AmazonAccountConfig config=amazonAccountConfigService.getByName(event.getAccountName());
					MailManagerInfo  info=customEmailManager.setCustomEmailManager(config.getEmailType(),config.getCustomerEmail(),config.getCustomerEmailPassword());
					customEmailManager.setManagerInfo(info);
					
					if(customEmailManager.send(mailInfo)){
						customEmailManager.clearConnection();
						event.setEmailNotice("1");	//标记已发送邮件通知
						event.setState("1");
						if (event.getAnswerDate() == null) {
							event.setAnswerDate(new Date());
						}
						eventService.save(event);
						sendEmail.setSentDate(new Date());
						sendEmail.setSendFlag("1");
						sendEmailService.save(sendEmail);
						
						Comment comm = new Comment();
						String common = user.getName()+" send an email to the customer. <a href='../sendEmail/view?id="+sendEmail.getId()+"'>view</a>";
						comm.setComment(common);
						comm.setType("1");
						comm.setEvent(event);
						commentService.save(comm);
						addMessage(redirectAttributes, "'"+mailInfo.getSubject()+"'Email sent successfully");
					}else{
						addMessage(redirectAttributes, "'"+mailInfo.getSubject()+"'Mail failed, probably because the network is not clear, please try again later!");
					}
				} else {
					addMessage(redirectAttributes, "Mail failed, Not found the product model, Please check the information!");
				}
			} else {
				addMessage(redirectAttributes, "Mail failed,customer email is empty!");
			}
		}
		return "redirect:"+Global.getAdminPath()+"/custom/event/?repage";
	}
	
	@RequiresPermissions("custom:event:edit")
	@RequestMapping(value = "delete")
	public String delete(Integer id, RedirectAttributes redirectAttributes) {
		eventService.delete(id);
		addMessage(redirectAttributes, MessageUtils.format("custom_event_deleteNote"));
		return "redirect:"+Global.getAdminPath()+"/custom/event/?repage";
	}
	
	@RequiresPermissions("custom:event:view")
	@RequestMapping(value = "/comment/view")
	public String addComment() {
		return "modules/custom/commentAdd";
	}
	
	@RequiresPermissions("custom:event:view")
	@RequestMapping(value = "/editView")
	public String editView(@RequestParam(required=false)String support,@RequestParam(required=false)String orderId,@RequestParam(required=false)String emailId,@RequestParam(required=false)String isEmailTax,Event event,Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			if(event.getId()!= null && !user.getId().equals(event.getMasterBy().getId()) && !user.getId().equals(event.getCreateBy().getId())){
				throw new RuntimeException(MessageUtils.format("custom_event_noPermissions"));
			}
		}
		
		if("1".equals(support)||"3".equals(support)||"8".equals(support)||"4".equals(support)){
			if(event.getId()!=null){
				model.addAttribute("type",support);
				model.addAttribute("aboutEvent",event.getId());
				Event eventSup = new Event();
				eventSup.setCreateBy(user);
				if("1".equals(support)){
					eventSup.setSubject(event.getTypeStr()+"Event SPR-"+event.getId()+"Send replacement goods");
					eventSup.setType("5");
					//eventSup.setMasterBy(supUser);
				}else if("8".equals(support)){	//评测事件
					eventSup.setSubject(event.getTypeStr()+"Event SPR-"+event.getId()+"Related events");
					eventSup.setType("8");
					//eventSup.setMasterBy(supUser);
				}else{
					eventSup.setSubject(event.getTypeStr()+"Event SPR-"+event.getId()+"Related events");
				}
				eventSup.setCountry(event.getCountry());
				eventSup.setCreateDate(new Date());
				eventSup.setCustomEmail(event.getCustomEmail());
				eventSup.setCustomId(event.getCustomId());
				eventSup.setCustomName(event.getCustomName());
				eventSup.setInvoiceNumber(event.getInvoiceNumber());
				eventSup.setReason(event.getReason());
				eventSup.setRemarks(event.getRemarks());
				eventSup.setReviewDate(event.getReviewDate());
				eventSup.setReviewLink(event.getReviewLink());
				eventSup.setAccountName(event.getAccountName());
				model.addAttribute("event", eventSup);
			}else if(StringUtils.isNotEmpty(emailId)){
				model.addAttribute("type",support);
				Event eventSup = new Event();
				eventSup.setCreateBy(user);
				if ("8".equals(support)) {
					model.addAttribute("support",support);
					eventSup.setSubject("Review Order Event For "+orderId==null?"":orderId);
					eventSup.setType(support);
					ReviewerEmail reviewerEmail = reviewerEmailService.get(Integer.parseInt(emailId));
					AmazonReviewer reviewer = reviewerEmail.getFormReviewer();
					eventSup.setCustomEmail(reviewerEmail.getRevertEmail());
					if (reviewer != null) {
						eventSup.setCustomName(reviewer.getName());
						if ("0".equals(reviewer.getReviewerType())) {
							eventSup.setCustomId(reviewer.getReviewerId());
						}
					}
				}else if("4".equals(support)){
					eventSup.setSubject("Tax Refund For "+(orderId==null?"":orderId));
					eventSup.setType("4");
					eventSup.setReason("Tax Refund");
				} else {
					eventSup.setSubject("Support Event For "+orderId==null?"":orderId);
					eventSup.setType("5");
				}
				//eventSup.setMasterBy(supUser);
				//查订单
				AmazonOrder order = orderService.findByLazy(orderId);
				if(order!=null){
					String country = order.getSalesChannel();
					country = country.replace("Amazon.co.","").replace("Amazon.","");
					eventSup.setCountry(country);
					eventSup.setCreateDate(new Date());
					eventSup.setCustomEmail(order.getBuyerEmail());
					eventSup.setCustomId(orderService.getCustomIdByOrderId(order.getAmazonOrderId()));
					eventSup.setCustomName(order.getBuyerName());
					eventSup.setInvoiceNumber(orderId);
					eventSup.setRemarks(order.getItems().get(0).getAsin());
					eventSup.setAccountName(order.getAccountName());
					
				}
				model.addAttribute("event", eventSup);
				model.addAttribute("emailId", emailId);
			}
		} else if("10".equals(support) && StringUtils.isNotEmpty(emailId)){ //召回事件
			model.addAttribute("type",support);
			Event eventSup = new Event();
			eventSup.setCreateBy(user);
			model.addAttribute("support",support);
			eventSup.setSubject("Product Recall Event For "+(orderId==null?"":orderId));
			eventSup.setType(support);
			eventSup.setProductQuantity(1);//默认设置1个
			
			//查订单
			AmazonOrder order = orderService.findByLazy(orderId);
			if(order!=null){
				String country = order.getSalesChannel();
				country = country.replace("Amazon.co.","").replace("Amazon.","");
				eventSup.setCountry(country);
				eventSup.setCreateDate(new Date());
				eventSup.setCustomEmail(order.getBuyerEmail());
				eventSup.setCustomId(orderService.getCustomIdByOrderId(order.getAmazonOrderId()));
				eventSup.setCustomName(order.getBuyerName());
				eventSup.setInvoiceNumber(orderId);
				eventSup.setRemarks(order.getItems().get(0).getAsin());
				eventSup.setAccountName(order.getAccountName());
			}
			if (StringUtils.isEmpty(eventSup.getCountry()) || "com".equals(eventSup.getCountry())) {
				model.addAttribute("flagToChina", "1");
			} else {
				model.addAttribute("flagToChina", "0");
			}
			model.addAttribute("event", eventSup);
			model.addAttribute("emailId", emailId);
		} else if("9".equals(support) && StringUtils.isNotEmpty(emailId)){ //product improvement事件
			model.addAttribute("type", support);
			Event eventSup = new Event();
			eventSup.setCreateBy(user);
			model.addAttribute("support",support);
			eventSup.setSubject("Product Improvement Event For "+(orderId==null?"":orderId));
			eventSup.setType(support);
			eventSup.setProductQuantity(1);//默认设置1个
			
			//查订单
			if (StringUtils.isNotEmpty(orderId)) {
				AmazonOrder order = orderService.findByLazy(orderId);
				if (order != null) {
					String country = order.getSalesChannel();
					country = country.replace("Amazon.co.","").replace("Amazon.","");
					eventSup.setCountry(country);
					eventSup.setCreateDate(new Date());
					eventSup.setCustomEmail(order.getBuyerEmail());
					eventSup.setCustomId(orderService.getCustomIdByOrderId(order.getAmazonOrderId()));
					eventSup.setCustomName(order.getBuyerName());
					eventSup.setInvoiceNumber(orderId);
					eventSup.setRemarks(order.getItems().get(0).getAsin());
					eventSup.setAccountName(order.getAccountName());
				}
			}
			model.addAttribute("event", eventSup);
			model.addAttribute("emailId", emailId);
		} else{
			model.addAttribute("event", event);
		}
		List<User> all = Lists.newArrayList();
        for (Office office :  officeService.findAll()) {
        	all.addAll(office.getUserList());
		}
        
    	Map<String,String> productMap =this.psiProductService.findProductTypeMap();
        model.addAttribute("productMapJson", JSON.toJSON(productMap));
		model.addAttribute("mangerMapJson", JSON.toJSON(this.psiProductService.findManagerProductTypeMap()));//查询产品经理
		model.addAttribute("problemMapJson", JSON.toJSON(customProductProblemService.findProblemType()));
        
        model.addAttribute("all", all);
		model.addAttribute("cuser", user);
		//model.addAttribute("supUser", supUser);
		//model.addAttribute("taxUser", taxUser);
		model.addAttribute("isEmailTax", isEmailTax);
		
		Map<String,String> accountMap= amazonAccountConfigService.findCountryByAccount();
		model.addAttribute("accountMap", accountMap);
		
		return "modules/custom/eventEditView";
	}
	
	
	@ResponseBody
	@RequiresPermissions("custom:event:view")
	@RequestMapping(value = "/comment/save")
	public String saveComment(String comment,Event event) {
		if("0".equals(event.getState())){
			event.setState("1");
			eventService.save(event);
		}
		Comment comm = new Comment();
		comm.setComment(HtmlUtils.htmlUnescape(comment));
		comm.setType("0");
		comm.setEvent(event);
		commentService.save(comm);
		if (!"1".equals(event.getCreateBy().getId()) 
				&& !UserUtils.getUser().getId().equals(event.getCreateBy().getId())) {	//管理员和操作人自己创建的事件不发通知
			final MailInfo mailInfo = new MailInfo(event.getCreateBy().getEmail(), "事件变更提醒("+DateUtils.getDate("yyyy/MM/dd")+")", new Date());
			String contents = "<p><span style='font-size:20px'>Hi "+event.getCreateBy().getName()+",<br/>&nbsp;&nbsp;&nbsp;&nbsp;" +
					"你创建的编号为SPR-"+event.getId()+"的事件新增了一条处理记录,记录内容如下,请知悉。</span>" +
        			"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/event/form?id="+event.getId()+"'>点击查看事件详情</a></p><br/>";
			contents += comm.getComment();
			mailInfo.setCcToAddress(event.getMasterBy().getEmail());
			mailInfo.setContent(contents);
			new Thread(){
			    public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
		return "1";
	}
	
	@RequiresPermissions("custom:event:view")
	@RequestMapping(value = "state")
	public String state(@RequestParam(required=false)String method,Event event, Model model, RedirectAttributes redirectAttributes) {
		try {
			event.setResult(URLDecoder.decode(event.getResult(),"utf-8"));
			if(method!=null){
				method = URLDecoder.decode(method,"utf-8");
			}	
		} catch (UnsupportedEncodingException e) {}
		event.setEndDate(new Date());
		eventService.save(event);
		if("1,2".contains(event.getType())&&method!=null){
			Comment comm = new Comment();
			comm.setComment(HtmlUtils.htmlUnescape(method));
			comm.setType("0");
			comm.setEvent(event);
			commentService.save(comm);
		}
		if ("2".equals(event.getState()) || "4".equals(event.getState())) {	//2:完成  4：关闭
			if (!"1".equals(event.getCreateBy().getId()) 
					&& !UserUtils.getUser().getId().equals(event.getCreateBy().getId())) {	//管理员和操作人自己创建的事件不发通知
				String result = "2".equals(event.getState())?"已完成":"已关闭";
				final MailInfo mailInfo = new MailInfo(event.getCreateBy().getEmail(), "事件变更提醒("+DateUtils.getDate("yyyy/MM/dd")+")", new Date());
				String contents = "<p><span style='font-size:20px'>Hi "+event.getCreateBy().getName()+",<br/>&nbsp;&nbsp;&nbsp;&nbsp;你创建的编号为SPR-"+event.getId()+"的事件"+result+"，请知悉。</span>" +
	        			"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/event/form?id="+event.getId()+"'>点击查看</a></p>";
				mailInfo.setCcToAddress(event.getMasterBy().getEmail());
				mailInfo.setContent(contents);
				new Thread(){
				    public void run(){
						mailManager.send(mailInfo);
					}
				}.start();
			}
		}
		addMessage(redirectAttributes,MessageUtils.format("custom_event_updateState", new Object[]{event.getId()}));
		return "redirect:"+Global.getAdminPath()+"/custom/event/?repage";
	}
	
	@RequiresPermissions("custom:event:view")
	@RequestMapping(value = "reopen")
	public String reopen(Event event, Model model, RedirectAttributes redirectAttributes) {
		event.setEndDate(null);
		event.setResult("");
		event.setState("1");
		eventService.save(event);
		return "redirect:"+Global.getAdminPath()+"/custom/event/form?id="+event.getId();
	}
	
	@RequiresPermissions("custom:event:view")
	@RequestMapping(value = "transmit")
	public String transmitOther(String tReason,String userId ,Event event, Model model, RedirectAttributes redirectAttributes) {
		User user = UserUtils.getUserById(userId);
		event.setAnswerDate(null);
		event.setState("0");
		if(StringUtils.isBlank(tReason)){
			tReason = "blank";
		}
		try {
			event.setTransmit(event.getMasterBy().getName()+"("+new Date().toLocaleString()+")"+"reason："+URLDecoder.decode(tReason,"utf-8"));
		} catch (UnsupportedEncodingException e) {}
		event.setMasterBy(user);
		eventService.save(event);
		noteClaimer(event,false);
		addMessage(redirectAttributes, MessageUtils.format("custom_event_transmitNote",new Object[]{user.getName()}));
		return "redirect:"+Global.getAdminPath()+"/custom/event/?repage";
	}
	
	@RequiresPermissions("custom:event:view")
	@RequestMapping(value = "batchTransmitOther")
	public String batchTransmitOther(@RequestParam("eid[]")Integer[] eid,String tReason,String userId , Model model, RedirectAttributes redirectAttributes) {
		if (eid != null && eid.length == 1) {
			return transmitOther(tReason, userId, eventService.get(eid[0]), model, redirectAttributes);
		}else {
			User user = UserUtils.getUserById(userId);
			for (Integer id : eid) {
				Event event = eventService.get(id);
				event.setAnswerDate(null);
				event.setState("0");
				if(StringUtils.isBlank(tReason)){
					tReason = "blank";
				}
				try {
					event.setTransmit(event.getMasterBy().getName()+"("+new Date().toLocaleString()+")"
						+"reason："+URLDecoder.decode(tReason,"utf-8"));
				} catch (UnsupportedEncodingException e) {}
				event.setMasterBy(user);
				eventService.save(event);
			}
			batchNoteClaimer(user, eid.length);
			addMessage(redirectAttributes, MessageUtils.format("custom_event_transmitNote",new Object[]{user.getName()}));
			return "redirect:"+Global.getAdminPath()+"/custom/event/?repage";
		}
	}
	
	
	@ResponseBody
	@RequestMapping(value = "sendTaxEmail")
	public String isExistEvent(Event event,String nameId,String titleId, RedirectAttributes redirectAttributes) {
		AmazonOrder amazonOrder = orderService.findByEg(event.getInvoiceNumber());
		File file = SendEmailByOrderMonitor.genTaxRefundPdf(amazonOrder,amazonOrder.getCountryChar(),nameId,titleId);
		if(file!=null){
			Map<String, String> params = Maps.newHashMap();
			String toEmail =event.getCustomEmail();
			if(StringUtils.isBlank(toEmail)){
				toEmail=amazonOrder.getBuyerEmail();
			}
			params.put("token",UnsubscribeEmailService.getUnsubscribeEmaliHref(toEmail));
			String template = SendEmailByOrderMonitor.getTemplate("tax","_" + amazonOrder.getAccountName(), params);
			String subject = "Tax Refund";
			if("de".equals(amazonOrder.getCountryChar())){
				subject = "Steuererstattung";
			}else if("fr".equals(amazonOrder.getCountryChar())){
				subject = "remboursement d'impôt";
			}else if("it".equals(amazonOrder.getCountryChar())){
				subject = "rimborso fiscale";
			}else if("es".equals(amazonOrder.getCountryChar())){
				subject = "devolución de impuestos";
			}
			
			final MailInfo mailInfo = new MailInfo(toEmail, subject + " "+ amazonOrder.getAmazonOrderId(), new Date());
			mailInfo.setContent(HtmlUtils.htmlUnescape(template));
			mailInfo.setFileName(file.getName());
			mailInfo.setFilePath(file.getAbsolutePath());
			mailInfo.setBccToAddress(UserUtils.getUser().getEmail());
			final String accountName= event.getAccountName();
			new Thread(){
				public void run(){   

					AmazonAccountConfig config=amazonAccountConfigService.getByName(accountName);
					MailManagerInfo  info=sendCustomEmailManager.setCustomEmailManager(config.getEmailType(),config.getCustomerEmail(),config.getCustomerEmailPassword());
					sendCustomEmailManager.setManagerInfo(info);
					sendCustomEmailManager.send(mailInfo);
					sendCustomEmailManager.clearConnection();
				}
			}.start();
		}
		return "1";
	}
	
	@ResponseBody
	@RequestMapping(value = "isExistEvent")
	public String isExistEvent(String orderId, Model model) {
		Event temp=eventService.isExistEventByOrder(orderId);
		if(temp!=null){
			return "0";
		}
		return "1";
	}
	
	@ResponseBody
	@RequestMapping(value = "isBeyond")
	public String isBeyond(String productName, Model model) {
		productName = HtmlUtils.htmlUnescape(productName);
		PsiProduct product = psiProductService.findProductByProductName(productName);
		if ("1".equals(product.getIsNew())) { // 新品判断是否超过15个事件
			return eventService.isBeyond(productName);
		}
		return "1";
	}
	
	@RequiresPermissions("custom:event:view")
	@RequestMapping(value = "saveEvent")
	public String save(@RequestParam(required=false)String aboutEvent,@RequestParam(required=false)String emailId,@RequestParam(required=false)String isEmailTax,@RequestParam(value="comms",required=false)List<String>comms,@RequestParam(value="cids",required=false)String[]cids,@RequestParam("attchmentFile")MultipartFile[] attchmentFiles,Event event,String nameId,String titleId, String skuId,Model model, RedirectAttributes redirectAttributes) {
		for (MultipartFile attchmentFile : attchmentFiles) {
			if(attchmentFile.getSize()!=0){
				if (event!=null&&"4".equals(event.getType())&&event.getAttchmentPath()!=null&&!event.getAttchmentPath().contains("/TP")){
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/event";
					String uuid = UUID.randomUUID().toString();
					File baseDir = new File(baseDirStr+"/"+uuid); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String name = attchmentFile.getOriginalFilename();
					name = HtmlUtils.htmlUnescape(name);
					File dest = new File(baseDir,"TP"+name);
					try {
						FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
						event.setAttchmentPath(uuid+"/TP"+name);
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
					}
				}else{
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/event";
					String uuid = UUID.randomUUID().toString();
					File baseDir = new File(baseDirStr+"/"+uuid); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String name = attchmentFile.getOriginalFilename();
					name = HtmlUtils.htmlUnescape(name);
					File dest = new File(baseDir,name);
					try {
						FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
						event.setAttchmentPath(uuid+"/"+name);
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
					}
				}
			}
		}
		if(event.getCreateBy()==null){
			event.setCreateBy(UserUtils.getUser());
		}
		if(comms!=null&&comms.size()>0){
			List<Comment> cList = event.getComments();
			Map<String, Comment>  temp = Maps.newHashMap();
			for (Comment comment : cList) {
				if("0".equals(comment.getType())&& comment.getCreateBy().getId().equals(UserUtils.getUser().getId())){
					temp.put(comment.getId(),comment);
				}
			}
			for (int i = 0; i < cids.length; i++) {
				String com = comms.get(i);
				Comment comment = temp.get(cids[i]);
				if(cids.length==1&&comms.size()>1){
					String tempr = comms.toString();
					comment.setComment(tempr.substring(1,tempr.length()-1));
				}else{
					comment.setComment(com);
				}
				comment.setUpdateDate(new Date());
			}
		}
		boolean sendNote = event.getId()==null;
		if ("10".equals(event.getType()) && StringUtils.isEmpty(event.getEmailNotice())) {
			event.setEmailNotice("0");	//召回事件标记是否已邮件通知客户
		}
		if ("4".equals(event.getType()) && StringUtils.isEmpty(event.getEmailNotice())) {
			event.setEmailNotice("0");
		}
		AmazonOrder amazonOrder =null;
		if("4".equals(event.getType())){
			try{
				amazonOrder = orderService.findByEg(event.getInvoiceNumber());
				if(amazonOrder!=null&&StringUtils.isNotBlank(event.getTaxId())){
					orderService.setRateSn(event.getTaxId(),event.getInvoiceNumber());
				}
			}catch(Exception e){}
		}
		if("4".equals(event.getType())&&sendNote&&"0".equals(isEmailTax)){
			event.setState("1");
			//AmazonOrder amazonOrder = orderService.findByEg(event.getInvoiceNumber());
			logger.info("======refundTax发送"+amazonOrder.getAmazonOrderId());
			File file = SendEmailByOrderMonitor.genTaxRefundPdf(amazonOrder,amazonOrder.getCountryChar(),nameId,titleId);
			if(file!=null){
				Map<String, String> params = Maps.newHashMap();
				String toEmail =event.getCustomEmail();
				if(StringUtils.isBlank(toEmail)){
					toEmail=amazonOrder.getBuyerEmail();
				}
				params.put("token",UnsubscribeEmailService.getUnsubscribeEmaliHref(toEmail));
				String template = SendEmailByOrderMonitor.getTemplate("tax","_" + amazonOrder.getAccountName(), params);
				String subject = "Tax Refund";
				if("de".equals(amazonOrder.getCountryChar())){
					subject = "Steuererstattung";
				}else if("fr".equals(amazonOrder.getCountryChar())){
					subject = "remboursement d'impôt";
				}else if("it".equals(amazonOrder.getCountryChar())){
					subject = "rimborso fiscale";
				}else if("es".equals(amazonOrder.getCountryChar())){
					subject = "devolución de impuestos";
				}
				
				final MailInfo mailInfo = new MailInfo(toEmail, subject + " "+ amazonOrder.getAmazonOrderId(), new Date());
				mailInfo.setContent(HtmlUtils.htmlUnescape(template));
				mailInfo.setFileName(file.getName());
				mailInfo.setFilePath(file.getAbsolutePath());
				
				mailInfo.setBccToAddress(UserUtils.getUser().getEmail());
				final String accountName=event.getAccountName();
				new Thread(){
					public void run(){   
						AmazonAccountConfig config=amazonAccountConfigService.getByName(accountName);
						MailManagerInfo  info=sendCustomEmailManager.setCustomEmailManager(config.getEmailType(),config.getCustomerEmail(),config.getCustomerEmailPassword());
						sendCustomEmailManager.setManagerInfo(info);
						sendCustomEmailManager.send(mailInfo);
						sendCustomEmailManager.clearConnection();
					}
				}.start();
				
			}
		}
		if ("11".equals(event.getType()) && StringUtils.isNotEmpty(event.getInvoiceNumber()) 
				&& StringUtils.isEmpty(event.getCustomEmail())) {
			//查订单
			AmazonOrder order = orderService.findByLazy(event.getInvoiceNumber());
			if(order!=null && "Shipped".equals(order.getOrderStatus())){
				event.setCustomEmail(order.getBuyerEmail());
				event.setCustomId(orderService.getCustomIdByOrderId(order.getAmazonOrderId()));
				event.setCustomName(order.getBuyerName());
				if (!"1".equals(event.getRefundType())) {
					event.setTotalPrice(order.getOrderTotal());
				}
			}
		}
		if("8".equals(event.getType())&&event.getId()==null){//review order新增
			List<Event> eventList=Lists.newArrayList();
			if(StringUtils.isNotBlank(nameId)){//asin
				String[] asinArr=nameId.split(",");
				String[] quantityArr=titleId.split(",");
				String[] skuArr=skuId.split(",");
				int index=0;
				for (String arr: asinArr) {
					if(StringUtils.isNotBlank(arr)){
						Event tempEvent=new Event();
						tempEvent.setCreateDate(event.getCreateDate());
						tempEvent.setCreateBy(event.getCreateBy());
						tempEvent.setAnswerDate(event.getAnswerDate());
						tempEvent.setUpdateBy(event.getUpdateBy());
						tempEvent.setUpdateDate(event.getUpdateDate());
						tempEvent.setState(event.getState());
						tempEvent.setMasterBy(event.getMasterBy());
						tempEvent.setRemarks(arr);
						tempEvent.setDelFlag("0");
						tempEvent.setTransmit(event.getTransmit());
						tempEvent.setSubject(event.getSubject());
						tempEvent.setType(event.getType());
						tempEvent.setDescription(event.getDescription());
						tempEvent.setPriority(event.getPriority());
						tempEvent.setCustomId(event.getCustomId());
						tempEvent.setInvoiceNumber(event.getInvoiceNumber());
						tempEvent.setReviewLink(event.getReviewLink());
						tempEvent.setReviewDate(event.getReviewDate());
						tempEvent.setAttchmentPath(event.getAttchmentPath());
						tempEvent.setResult(event.getResult());
						tempEvent.setCustomName(event.getCustomName());
						tempEvent.setCustomEmail(event.getCustomEmail());
						tempEvent.setCountry(event.getCountry());
						tempEvent.setReason(event.getReason());
						tempEvent.setAccountName(event.getAccountName());
						//String sku=skuArr[index].substring(skuArr[index].indexOf("[")+1, skuArr[index].indexOf("]"));
						//AmazonProduct2 product = amazonProduct2Service.getProduct(event.getCountry(),sku);
						Float price=amazonProduct2Service.getSalePrice(event.getCountry(),arr);
						if(price!=null){
							tempEvent.setReviewPrice(price);
						}
						
						tempEvent.setReviewQuantity(Integer.parseInt(quantityArr[index]));
						eventList.add(tempEvent);
					}
					index++;
				}
			}
			if(eventList!=null&&eventList.size()>0){
				eventService.save(eventList);
			}
		}else{
			eventService.save(event);
		}
		
		if("9".equals(event.getType())){
			//如果为产品问题的，发邮件通知创建人，责任人
			String content="";
			String email =UserUtils.getUser().getEmail()+","+UserUtils.getUserById(event.getMasterBy().getId()).getEmail();
			if(sendNote){
				content="Product Improvement事件、已创建<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/event/form?id="+event.getId()+"'>["+event.getSubject()+"]</a></br>"+event.getDescription();
			}else{
				content="Product Improvement事件、重新编辑<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/event/form?id="+event.getId()+"'>["+event.getSubject()+"]</a></br>"+event.getDescription();
			}
			try {
				this.sendEmail("Hi,All<br/> "+content, email);
			} catch (Exception e) {
			}
		}
		
		
		if(!StringUtils.isEmpty(aboutEvent)){
			try {
				int id = Integer.parseInt(aboutEvent);
				Event aboutE = eventService.get(id);
				if(event!=null){
					String common =UserUtils.getUser().getName()+"  created "+event.getTypeStr()+" Event for the cause of "+aboutE.getTypeStr()+" Event. <a href='form?id="+event.getId()+"'>view</a>";
					Comment comm = new Comment();
					comm.setComment(common);
					comm.setType("1");
					comm.setCreateBy(UserUtils.getUserById("1"));
					comm.setEvent(aboutE);
					commentService.save(comm);
				}
				addMessage(redirectAttributes, MessageUtils.format("custom_event_note1",new Object[]{event.getTypeStr(),event.getId()}));
				return "redirect:"+Global.getAdminPath()+"/custom/event/form?id="+id;
			} catch (NumberFormatException e) {}
		}else if(StringUtils.isNotEmpty(emailId)){
			try {
				if(event!=null){
					String common = "";
					//根据主键生成方式判断是客服邮箱还是评测邮箱
					if(emailId.matches("[0-9]+")){
						common =UserUtils.getUser().getName()+"  created "+event.getTypeStr()+" Event for the cause of Review Email. <a href='../../amazoninfo/reviewerEmail/form?id="+emailId+"'>view</a>";
						if (event.getInvoiceNumber() != null) {
							String orderId = event.getInvoiceNumber().split(",")[0];
							AmazonOrder order = orderService.findByLazy(orderId);
							// 如果为评测邮件的事件并且订单地址不为空，则更新订单地址为评测者地址
							AmazonAddress address = order==null?null:order.getShippingAddress();
							if (address != null) {
								ReviewerEmail reviewerEmail = reviewerEmailService.get(Integer.parseInt(emailId));
								AmazonReviewer reviewer = reviewerEmail.getFormReviewer();
								if (reviewer != null) {
									StringBuffer sBuffer = new StringBuffer();
									sBuffer.append(address.getName()==null?"":address.getName() + ",")
										.append(address.getAddressLine1()==null?"":address.getAddressLine1() + ",")
										.append(address.getAddressLine2()==null?"":address.getAddressLine2() + ",")
										.append(address.getAddressLine3()==null?"":address.getAddressLine3() + ",")
										.append(address.getCounty()==null?"":address.getCounty() + ",")
										.append(address.getCity()==null?"":address.getCity() + ",")
										.append(address.getCountryCode()==null?"":address.getCountryCode() + ",")
										.append(address.getPostalCode()==null?"":address.getPostalCode());
									reviewer.setAddress(sBuffer.toString());
									amazonReviewerService.save(reviewer);
								}
							}
						}
					} else {
						common =UserUtils.getUser().getName()+"  created "+event.getTypeStr()+" Event for the cause of Customer Email. <a href='../emailManager/form?id="+emailId+"'>view</a>";
					}
					Comment comm = new Comment();
					comm.setComment(common);
					comm.setType("1");
					comm.setCreateBy(UserUtils.getUserById("1"));
					comm.setEvent(event);
					commentService.save(comm);
				}
				addMessage(redirectAttributes, MessageUtils.format("custom_event_note1",new Object[]{event.getTypeStr(),event.getId()}));
			} catch (NumberFormatException e) {}
		}else{
			addMessage(redirectAttributes, MessageUtils.format("custom_event_note2", new Object[]{event.getId()}));
		}
		event.setMasterBy(UserUtils.getUserById(event.getMasterBy().getId()));
		if(sendNote){
			noteClaimer(event,true);
		}
		if("8".equals(event.getType())&&event.getId()==null){
			return "redirect:"+Global.getAdminPath()+"/custom/event/list";
		}else{
			return "redirect:"+Global.getAdminPath()+"/custom/event/form?id="+event.getId();
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
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/event/";  
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
	
	
	@RequiresPermissions("custom:event:view")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String export(Event event, HttpServletRequest request, HttpServletResponse response) {
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		Page<Event> page = new Page<Event>(request, response);
		page.setPageNo(1);
		page.setPageSize(10000);
		String orderBy = page.getOrderBy();
		if("-1".equals(event.getType())){
			if("".equals(orderBy)){
				page.setOrderBy("masterBy desc,type asc,priority asc,id desc");
			}else{
				page.setOrderBy(orderBy+"id desc");
			}	
		}else{
			if("".equals(orderBy)){
				page.setOrderBy("id desc");
			}else{
				page.setOrderBy(orderBy+",id desc");
			}	
		}
        page = eventService.find(page, event); 
        List<Event> data = page.getList();
        Map<String,String> nameMap=amazonProductService.getProductNameAsin();
        for (Event event2 : data) {
			event2.setType(event2.getTypeStr());
        	event2.setPriority(event2.getPriorityStr());
        	event2.setState(event2.getStateStr());
        	event2.setAnswerDate(event2.getCreateDate());
        	List<Comment> comments = event2.getComments();
        	StringBuffer stringBuffer = new StringBuffer("");
        	if(comments!=null && comments.size()>0){
        		for (int i = comments.size()-1; i >=0; i--) {
					if(!"1".equals(comments.get(i).getCreateBy().getId())){
						stringBuffer.append(comments.get(i).getComment()+"|");
					}
				}
        	}
        	String commentStr = stringBuffer.toString();
        	if(commentStr.length()>0){
        		commentStr = commentStr.substring(0,commentStr.length()-1);
        	}
        	event2.setAttchmentPath(commentStr);
        	String name = "unknown";
        	if(StringUtils.isNotEmpty(event2.getRemarks())&&StringUtils.isNotEmpty(event2.getCountry())){
        		String temp = nameMap.get(event2.getRemarks()+","+event2.getCountry());
            	if(StringUtils.isNotEmpty(temp)){
            			name = temp;
            	}
        	}
        	event2.setDescription(name);
		}
		try {
            String fileName = "事件数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
    		new ExportExcel("事件数据("+format.format(event.getCreateDate())+"-"+format.format(event.getEndDate())+")", Event.class).setDataList(data).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/custom/event/?repage";
    }
	
	
	@RequiresPermissions("custom:event:view")
    @RequestMapping(value = "countExport", method=RequestMethod.POST)
    public String countExport(Event event, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
            String fileName = "区间内事件统计数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
            Date today = new Date();
    		today.setHours(0);
    		today.setSeconds(0);
    		today.setMinutes(0);
    		if(event.getCreateDate()==null){
    			event.setCreateDate(DateUtils.addMonths(today,-1));
    			event.setEndDate(today);
    		}
    		Map<String,Map<String, String>> data = eventService.count(event);
    		
            ExportExcel excel = new ExportExcel("区间内事件统计数据("+format.format(event.getCreateDate())+"-"+format.format(event.getEndDate())+")",new String[]{"负责人","有效事件","解决事件","关闭事件","解决率","平均解决速度"});
            int nofors = 0 ;
            int twos = 0 ;
            int fores = 0;
            double prcentNum = 0 ;
            double days = 0;
            int length = data.values().size();
            for (Map<String, String> rowData : data.values()) {
            	Row row = excel.addRow();
				int col = 0 ;
				excel.addCell(row, col++, rowData.get("user"));
				String noFor = rowData.get("noFor") ==null?"0":rowData.get("noFor");
				String two = rowData.get("two") ==null?"0":rowData.get("two");
				String forE = rowData.get("for") ==null?"0":rowData.get("for");
				String avg = "";
				if(rowData.get("avg") ==null){
					avg ="无";
				}else{
					double temp  = Double.parseDouble(rowData.get("avg"));
					temp  = Double.parseDouble(rowData.get("avg"))/(double)86400;
					BigDecimal   bd   =   new   BigDecimal(temp);
					temp   = bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					days = days+temp;
					if(temp<1){
						avg = "1天以内";
					}else{
						avg = temp+"天";
					}
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
				nofors = nofors + Integer.parseInt(noFor);
				twos = twos + Integer.parseInt(two);
				fores = fores + Integer.parseInt(forE);
				excel.addCell(row, col++, noFor);
				excel.addCell(row, col++, two);
				excel.addCell(row, col++, forE);
				
				excel.addCell(row, col++, prcent);
				excel.addCell(row, col++, avg);
				/*String result = rowData.get("result");
				if(result!=null){
					 int count = result.split("<br/>").length;
					 result = result.replaceAll("</a>", "").replaceAll("<a [^>]*>","").replaceAll("<br/>", "\\\r\\\n");
					 row = excel.addRow();
					 row.setHeight((short)(750*count));
					 Cell cell = row.createCell(0);
					 cell.setCellValue(result);
					 CellStyle cellStyle =  excel.getWb().createCellStyle();
					 cellStyle.setWrapText(true);
					 cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
					 cell.setCellStyle(cellStyle);
					 excel.getSheet().addMergedRegion(new CellRangeAddress(excel.getRownum()-1, (short) excel.getRownum()-1, 0, (short)5));   
				}*/
			}
            Row row = excel.addRow();
            excel.addCell(row, 0, "合计");
            excel.addCell(row, 1, nofors);
			excel.addCell(row, 2, twos);
			excel.addCell(row, 3, fores);
			if(length>0){
				BigDecimal   bd  = new   BigDecimal(((double)twos/(double)nofors)*(double)100);
				prcentNum   = bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				bd   =   new   BigDecimal(days/length);
				days   = bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			excel.addCell(row, 4, prcentNum+"%");
			excel.addCell(row, 5, days+"天");
            excel.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, MessageUtils.format("custom_event_exception1", new Object[]{e.getMessage()}));
		}
		return "redirect:"+Global.getAdminPath()+"/custom/event/count?repage";
    }
	
	@Autowired
	private ICommentRequest commentRequest ;
	
	@RequiresPermissions("custom:event:view")
    @RequestMapping(value = "ratingExport", method=RequestMethod.POST)
    public String ratingExport(Event event, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "客户评论统计数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
            Date today = new Date();
    		today.setHours(0);
    		today.setSeconds(0);
    		today.setMinutes(0);
    		if(event.getCreateDate()==null){
    			event.setCreateDate(DateUtils.addMonths(today,-1));
    			event.setEndDate(today);
    		}
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    		ExportExcel excel = new ExportExcel("客户评论统计数据("+format.format(event.getCreateDate())+"-"+format.format(event.getEndDate())+")",new String[]{"市场","1分","2分","3分","4分","5分"});
    		String date = commentRequest.getCountComments("qweqwr*$@#12314", format1.format(event.getCreateDate()), format1.format(event.getEndDate()));
    		int good = 0 ;
	        int bad = 0 ;
    		if(StringUtils.isNotEmpty(date)){
    			List<HashMap> list = JSON.parseArray(date, HashMap.class);
    			Map<String,Row> rows = Maps.newHashMap();
    			for (Dict dict : DictUtils.getDictList("platform")) {
    				Row row = excel.addRow();
    				String key = dict.getValue();
    				if("com".equals(key)){
    					key = "com.inateck";
    				}else if("jp,uk".contains(key)){
    					key = "co."+key;
    				}
    				excel.addCell(row, 0,dict.getValue());
    				for (int i = 1; i <= 4; i++) {
    					excel.addCell(row,i,0);
					}
    				rows.put(key,row);
				}
    			Row row = excel.addRow();
    			excel.addCell(row, 0,"合计");
    			rows.put("count",row);
    			//合计行
    	        for (int i = 0; i < list.size(); i++) {
    	        	 HashMap map = list.get(i);
    	        	 int count  = 0;
    	        	 for (Object obj :map.entrySet()) {
    	        		 Entry entry = (Entry)obj;
    	        		 String key1 = entry.getKey().toString();
    	        		 excel.addCell(rows.get(key1),(i+1),entry.getValue());
    	        		 count += Integer.parseInt(entry.getValue().toString());
					 }
	        		 excel.addCell(rows.get("count"),(i+1),count);
    	        	 if(i<=2){
    	        		 bad +=count;
    	        	 }else{
    	        		 good += count;
    	        	 }
				}
    		}     
            Row row = excel.addRow();
            row.setHeight((short)1000);
            excel.addCell(row, 0,"1-3分合计");
            excel.addCell(row, 1, bad);
            row = excel.addRow();
            row.setHeight((short)1000);
            excel.addCell(row, 0,"4-5分合计");
            excel.addCell(row, 1, good);
            excel.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "导出客户评论统计数据失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/custom/event/count?repage";
    }
	
	@Autowired
	private MailManager mailManager;
	
	public  void noteClaimer(final Event event,final boolean isCreate){
		if(isCreate && event.getCreateBy().getId().equals(event.getMasterBy().getId())){
			return;
		}
		new Thread(){
			@Override
			public void run() {
				try{
					User master = event.getMasterBy();
					MailInfo mailInfo = new MailInfo(master.getEmail(), "ERP-->SPR-"+event.getId()+"["+event.getTypeStr()+" Event]Changed Note Email", new Date());
					String url = "custom/event/form?id="+event.getId();
					String content ="Creator:"+event.getCreateBy().getName()+";Event:SPR-"+event.getId()+"["+event.getTypeStr()+" Event]"+(isCreate?"Assigned":"Forwarding["+event.getTransmit()+"]")+"to you;please settle in time!<br/>----><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/"+url+"' target='_blank'>Handle</a>"+"<br/><br/>---------------Auto sent email, do not reply！";
					mailInfo.setContent(HtmlUtils.htmlUnescape(content));
					boolean rs = mailManager.send(mailInfo);
					int i = 0 ;
					while(!rs && i<3){
						Thread.sleep(5000);
						rs = mailManager.send(mailInfo);
						i++;
					}
					if(!rs){
						logger.error( event.getMasterBy().getEmail()+"的事件提醒:发送失败");
					}
					
				} catch (Exception e) {
					logger.error( event.getMasterBy().getEmail()+":"+e.getMessage());
				}
			}
		}.start();		
	}
	
	/**
	 * 
	 * @param master 事件转发对象
	 * @param count	事件转发数量
	 */
	public  void batchNoteClaimer(final User master,final int count){
		if(UserUtils.getUser().getId().equals(master.getId())){
			return;
		}
		new Thread(){
			@Override
			public void run() {
				try{
					MailInfo mailInfo = new MailInfo(master.getEmail(), "ERP-->SPR Event Changed Note Email", new Date());
					String url = "custom/event";
					String content ="Forwarder :"+UserUtils.getUser().getName()+";Event:Forwarding "+count+"　Events to you;please settle in time!<br/>----><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/"+url+"' target='_blank'>Handle</a>"+"<br/><br/>---------------Auto sent email, do not reply！";
					mailInfo.setContent(HtmlUtils.htmlUnescape(content));
					boolean rs = mailManager.send(mailInfo);
					int i = 0 ;
					while(!rs && i<3){
						Thread.sleep(5000);
						rs = mailManager.send(mailInfo);
						i++;
					}
					if(!rs){
						logger.error(master.getEmail()+"的事件提醒:发送失败");
					}
					
				} catch (Exception e) {
					logger.error( master.getEmail()+":"+e.getMessage());
				}
			}
		}.start();		
	} 
	
	
	private static Map<String,String> threadTaxPdf = Maps.newHashMap();

    @RequestMapping(value = "exportTaxPdfs")
    public String exportPdfs(final String month,String country,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes){
    	//String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/eventTax/"+month;
    	String baseDirStr= ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/eventTax";
    	if(StringUtils.isBlank(country)){
    		country="";
    	}
    	File zipFile = new File (baseDirStr+"/"+country+month+".zip");
		final String key =  (country+month);
		final String tempCty = country;
		if(threadTaxPdf.get(key)==null){
			if(!zipFile.exists()|| zipFile.lastModified()+3*3600000<new Date().getTime()){
	    		new Thread(){
	    			public void run() {
	    				threadTaxPdf.put(key, "1");
	    				Date monthDate = null;
						try {
							monthDate = DateUtils.parseDate(month+"-1",new String[]{"yyyy-MM-dd"});
						} catch (ParseException e1) {
							logger.error(month+"-1", e1);
						}
	    				Date start = DateUtils.getFirstDayOfMonth(monthDate);
	    				Date end = DateUtils.getLastDayOfMonth(monthDate);
	    				List<Object[]> eventList=eventService.findAllTaxOrder(start,end,tempCty);
	    				if(eventList!=null&&eventList.size()>0){
	    					File pdfFile=null;
		    				for (Object[] obj: eventList) {
								String country=obj[0].toString();
								String orderId=obj[1].toString();
								Float  refundTotal=Float.parseFloat(obj[2].toString());
								AmazonOrder amazonOrder=amazonOrderService.findByEg(orderId);
								String imgPath="";
								String rateSn=(obj[4]==null?"":obj[4].toString());
								String[] arr=obj[3].toString().split(",");
								for (String  path : arr) {
									if(path.split("/")[1].startsWith("TP")&&!path.split("/")[1].endsWith(".pdf")&&!path.split("/")[1].endsWith(".PDF")){
										imgPath=path;
										break;
									}
								}
								Float avgRate=1f;
								if("uk".equals(country)){
									avgRate=amazonProduct2Service.findAvgMonthRate("GBP/EUR",amazonOrder.getPurchaseDate());
								}
								AmazonOrderExtract  orderExtract=amazonOrderService.findRateSnByOrderId(amazonOrder.getAmazonOrderId());
								AmazonAccountConfig config = amazonAccountConfigService.getByName(orderExtract.getAccountName());
								String invoiceType = config.getInvoiceType();
								String flag= invoiceType.substring(0, invoiceType.lastIndexOf("_"));
								String suffix= invoiceType.substring(invoiceType.lastIndexOf("_")+1);
								
								if(orderExtract==null||StringUtils.isBlank(orderExtract.getInvoiceNo())){
									if((orderExtract.getInvoiceFlag().startsWith("0")&&!"fr".equals(country))||
										((orderExtract.getInvoiceFlag().startsWith("00")||orderExtract.getInvoiceFlag().startsWith("10"))&&"fr".equals(country))
									 ){//未发送账单
										String invoiceNo=amazonOrderService.createFlowNo(flag,8,suffix);
										amazonOrder.setInvoiceNo(invoiceNo);
										amazonOrderService.updateInvoiceNoById(orderExtract.getInvoiceNo(),orderExtract.getAmazonOrderId()) ;
									}else{
										amazonOrder.setInvoiceNo(orderExtract.getId()+"");
									}
								}else{
									amazonOrder.setInvoiceNo(orderExtract.getInvoiceNo());
								}
								pdfFile=SendEmailByOrderMonitor.genEuTaxRefundPdf2(rateSn,month,imgPath,avgRate,refundTotal,amazonProductService,country, amazonOrder,"3",tempCty);
							}
		    		    	
		    		    	try {
		    					ZipUtil.zip("","",pdfFile.getParentFile().getAbsolutePath());
		    				} catch (Exception e) {
		    						e.printStackTrace();
		    				}
		    		    	
	    				}
	    				
	    				threadTaxPdf.remove(key);
	    			};
	    		}.start();
	    		addMessage(redirectAttributes, "Is background rendering, please make the request again after 5 minutes ...");
	    		return "redirect:"+Global.getAdminPath()+"/custom/event/";
			}else{
				try {
    				response.addHeader("Content-Disposition", "attachment;filename="
    						+zipFile.getName());
    				OutputStream out = response.getOutputStream();
    				out.write(FileUtils.readFileToByteArray(zipFile));
    				out.flush();
    				out.close();
        		} catch (Exception e) {
    				e.printStackTrace();
    			}
        		return null;
			}
		}
		addMessage(redirectAttributes, "Is background rendering,Just a moment please ...");
		return "redirect:"+Global.getAdminPath()+"/custom/event/";
    }	
	
	@RequestMapping(value = "approval")
	public String approval(Event event, RedirectAttributes redirectAttributes) {
		event.setState("1");
		if (event.getAnswerDate() == null) {
			event.setAnswerDate(new Date());
		}
		eventService.save(event);
		return "redirect:"+Global.getAdminPath()+"/custom/event/?repage";
	}
	
	private  void sendEmail(String content,String toAddress) throws Exception{
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();
			final MailInfo mailInfo = new MailInfo(toAddress,(content.length()>100?content.substring(0, 100)+"...":content)+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(content);
			new Thread(){
				public void run(){
					mailManager.send(mailInfo);
				} 
			}.start();
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "saveIsEvil")
	public String saveIsEvil(Event event) {
		eventService.save(event);
		return "1";
	}
	

	@RequestMapping(value = "updatePrice")
	@ResponseBody
	public String updatePrice(Integer id,Float reviewPrice) {
		eventService.updatePrice(id,reviewPrice);
		return "1";
	}
	
	@RequestMapping(value = "test")
	public void test(){
		this.eventService.getMaster("de","2","");
	}
	
	
	@RequiresPermissions("custom:event:view")
    @RequestMapping(value = "timeOutExport", method=RequestMethod.POST)
    public String timeOutExport(Event event, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fileName = "超过24小时事件统计数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
           
            List<Event> data=eventService.findOutTimeEvent(event);
    		
            ExportExcel excel = new ExportExcel("超过24小时事件统计数据("+format.format(event.getCreateDate())+"-"+format.format(event.getEndDate())+")",new String[]{"事件","类型","主题","负责人","创建时间","第一次处理时间","相隔小时","国家"});
            
            for (Event  e : data) {
            	Row row = excel.addRow();
				int col = 0 ;
				excel.addCell(row, col++, "SPR-"+e.getId());
				excel.addCell(row, col++, e.getTypeStr());
				excel.addCell(row, col++, e.getSubject());
				excel.addCell(row, col++, e.getCustomName());
				excel.addCell(row, col++, format.format(e.getCreateDate()));
				excel.addCell(row, col++, format.format(e.getAnswerDate()));
				excel.addCell(row, col++, e.getPriority());
				excel.addCell(row, col++, e.getAccountName());
			}
            excel.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, MessageUtils.format("custom_event_exception1", new Object[]{e.getMessage()}));
		}
		return "redirect:"+Global.getAdminPath()+"/custom/event/count?repage";
    }
}
