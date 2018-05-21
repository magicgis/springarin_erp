package com.springrain.erp.modules.amazoninfo.web;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonReviewer;
import com.springrain.erp.modules.amazoninfo.entity.AmazonReviewerContent;
import com.springrain.erp.modules.amazoninfo.entity.ReviewerComment;
import com.springrain.erp.modules.amazoninfo.entity.ReviewerEmail;
import com.springrain.erp.modules.amazoninfo.entity.ReviewerSendEmail;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.AmazonReviewerService;
import com.springrain.erp.modules.amazoninfo.service.ReviewerCommentService;
import com.springrain.erp.modules.amazoninfo.service.ReviewerEmailService;
import com.springrain.erp.modules.amazoninfo.service.ReviewerSendEmailService;
import com.springrain.erp.modules.custom.entity.CustomEmailTemplate;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.custom.entity.Signature;
import com.springrain.erp.modules.custom.service.CustomEmailTemplateService;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.custom.service.SignatureService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 评测Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/reviewer")
public class ReviewerController extends BaseController {

	@Autowired
	private AmazonReviewerService amazonReviewerService;
	
	@Autowired
	private ReviewerSendEmailService reviewerSendEmailService;
	
	@Autowired
	private SignatureService signatureService;
	
	@Autowired
	private CustomEmailTemplateService customEmailTemplateService;
	
	@Autowired
	private ReviewerCommentService reviewerCommentService;
	
	@Autowired
	private ReviewerEmailService reviewerEmailService;
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private AmazonProductService amazonProductService;
	
	@ModelAttribute
	public AmazonReviewer get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return amazonReviewerService.getReviewer(Integer.parseInt(id));
		}else{
			return new AmazonReviewer();
		}
	}
	
	//站内站外汇总
	@RequestMapping(value = "totalList")
	public String totalList(AmazonReviewer reviewer, String aboutMe, String isVineVoice, HttpServletRequest request, HttpServletResponse response, Model model) {
		if (StringUtils.isBlank(reviewer.getCountry())) {
			reviewer.setCountry("de");
		}
		Page<AmazonReviewer> page = new Page<AmazonReviewer>(request, response);
		String orderBy = page.getOrderBy();
		if(StringUtils.isEmpty(orderBy)){
			page.setOrderBy("updateDate desc");
		}
		
		page = amazonReviewerService.findReviewList(page, reviewer, aboutMe,isVineVoice);
		model.addAttribute("page", page);
		model.addAttribute("reviewer", reviewer);
		model.addAttribute("aboutMe", aboutMe);
		model.addAttribute("isVineVoice", isVineVoice);
		return "modules/amazoninfo/reviewer/reviewerTotalList";
		
	}

	@RequestMapping(value = {"list", ""})
	public String list(AmazonReviewer reviewer, String aboutMe,String isVineVoice, HttpServletRequest request, HttpServletResponse response, Model model) {
		if (StringUtils.isBlank(reviewer.getCountry())) {
			reviewer.setCountry("de");
		}
		if (StringUtils.isBlank(reviewer.getReviewerType())) {
			reviewer.setReviewerType("0");
		}
		model.addAttribute("reviewer", reviewer);
		model.addAttribute("aboutMe", aboutMe);
		model.addAttribute("isVineVoice", isVineVoice);
		//站外评测人员
		if ("1".equals(reviewer.getReviewerType())) {
			Page<AmazonReviewer> page = new Page<AmazonReviewer>(request, response);
			String orderBy = page.getOrderBy();
			if(StringUtils.isEmpty(orderBy)){
				page.setOrderBy("updateDate desc");
			}
			page = amazonReviewerService.findReviewList(page, reviewer, aboutMe,isVineVoice);
			model.addAttribute("page", page);
			return "modules/amazoninfo/reviewer/reviewerOutList";
		}
		List<Object[]> ops = amazonReviewerService.find(reviewer, aboutMe,isVineVoice);
		model.addAttribute("ops", ops);
		
		// 计算与评测者的联系次数 map<评测人id,联系次数>
		Map<Integer, Integer> contactNum = amazonReviewerService.findContactNum(reviewer);
		model.addAttribute("contactNum", contactNum);
		
		// 列出inateck品牌评测产品的名称 
		Map<String, String> productNameMap = amazonProductService.findProductNameMap();
		Map<Integer, List<String>> asinMap = amazonReviewerService.findAsins(reviewer);
		//评测人id inateck品牌产品名称
		Map<Integer, String> tips = Maps.newHashMap();
		for (Map.Entry<Integer,List<String>> entry : asinMap.entrySet()) { 
		    Integer id = entry.getKey();
			List<String> asinList = entry.getValue();
			String productTip = "";
			StringBuffer buf= new StringBuffer();
			for (int i = 0; i < asinList.size(); i++) {
				String productName = productNameMap.get(asinList.get(i));
				if (StringUtils.isNotBlank(productName)) {
					buf.append(productName + "<br/>");
				}
			}
			productTip = buf.toString();
			if (StringUtils.isNotBlank(productTip)) {
				tips.put(id, productTip);
			}
		}
		model.addAttribute("tips", tips);
		
		String key = reviewer.getCountry();
		if ("jp,uk".contains(key)){
			key = "co."+key;
		}else if("mx".equals(key)){
			key="com."+key;
		}
		model.addAttribute("key", key);
		return "modules/amazoninfo/reviewer/reviewerList";
	}

	/**
	 * 评测产品列表
	 */
	@RequestMapping(value = "reviewerProductList")
	public String reviewerProductList(AmazonReviewerContent reviewerContent, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AmazonReviewerContent> page = new Page<AmazonReviewerContent>(request, response);
		if (StringUtils.isNotBlank(reviewerContent.getProductType())) {
			try {
				reviewerContent.setProductType(URLDecoder.decode(reviewerContent.getProductType(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {}
		}
		String orderBy = page.getOrderBy();
		if(StringUtils.isEmpty(orderBy)){
			page.setOrderBy("reviewDate desc");
		}else if(!orderBy.contains("reviewDate")){
			page.setOrderBy(orderBy+",reviewDate desc");
		}
        page = amazonReviewerService.findReviewProductList(page, reviewerContent);
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
        model.addAttribute("reviewerContent", reviewerContent);
        if(reviewerContent.getBrandType() != null && "inateck".equals(reviewerContent.getBrandType().toLowerCase())){
    		Map<String, String> productName = amazonProductService.findProductNameMap();
            model.addAttribute("productName", productName);
        }
		return "modules/amazoninfo/reviewer/reviewerProductList";
	}

	@RequestMapping(value = "form")
	public String form(AmazonReviewer reviewer, Model model) {
		if (reviewer.getId() == null) {
			reviewer.setReviewerType("1");
		}
		model.addAttribute("reviewer", reviewer);
		return "modules/amazoninfo/reviewer/reviewerForm";
	}

	@RequestMapping(value = "save")
	public String save(AmazonReviewer reviewer, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, reviewer)){
			return form(reviewer, model);
		}
		// 保存信息
		reviewer.setUpdateDate(new Date());
		amazonReviewerService.save(reviewer);
		addMessage(redirectAttributes, "保存评测人'" + reviewer.getName() + "'成功");
		redirectAttributes.addAttribute("reviewerType", reviewer.getReviewerType());
		redirectAttributes.addAttribute("country", reviewer.getCountry());
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/reviewer/totalList?";
	}
	
	@ResponseBody
	@RequestMapping(value = {"saveStar"})
	public String saveStar(AmazonReviewer reviewer, HttpServletRequest request, HttpServletResponse response, Model model) {
		amazonReviewerService.save(reviewer);
		return "1";
	}
	
	@ResponseBody
	@RequestMapping(value = "addStar")
	public String addStar(Integer id, String star) {
		if(StringUtils.isNotEmpty(star) && id != null){
			AmazonReviewer reviewer = amazonReviewerService.getReviewer(id);
			reviewer.setStar(star);
			amazonReviewerService.save(reviewer);
		}
		return "1";
	}
	
	@RequestMapping(value = "sendEmail")
	public String sendEmail(String email, Model model, RedirectAttributes redirectAttributes) {
		ReviewerSendEmail sendEmail2  =  reviewerSendEmailService.findBlankEmail(email);
		ReviewerSendEmail newSendEmail = null;
		User user = UserUtils.getUser();
		if(sendEmail2==null){
			newSendEmail = new ReviewerSendEmail();
			newSendEmail.setType("0");
			newSendEmail.setCreateBy(user);
			Signature signature = signatureService.get(UserUtils.getUser().getId());
			if(signature!=null&&signature.getSignatureContent().length()>0){
				newSendEmail.setSendContent("<br/><br/><br/><br/>--------------------<br/>"+signature.getSignatureContent());
			}	
			newSendEmail.setSendEmail(email);
			reviewerSendEmailService.save(newSendEmail);
		}else{
			newSendEmail = sendEmail2;
		}
		
		//生成邮件模板
		List<CustomEmailTemplate> templates = this.customEmailTemplateService.find(user);		
		model.addAttribute("templates", templates);
		
		model.addAttribute("sendEmail", newSendEmail);
		return "modules/amazoninfo/reviewer/reviewerSendEmail";
	}
	
	@RequestMapping(value = "batchSendEmail")
	public String batchSendEmail(@RequestParam("emails[]")String[] emails, Model model, RedirectAttributes redirectAttributes) {
		if (emails != null && emails.length == 1) {
			return sendEmail(emails[0], model, redirectAttributes);
		}else {
			User user = UserUtils.getUser();
			ReviewerSendEmail newSendEmail = new ReviewerSendEmail();
			newSendEmail.setType("0");
			newSendEmail.setCreateBy(user);
			Signature signature = signatureService.get(UserUtils.getUser().getId());
			if(signature!=null&&signature.getSignatureContent().length()>0){
				newSendEmail.setSendContent("<br/><br/><br/><br/>--------------------<br/>"+signature.getSignatureContent());
			}
			String emailStr = "";
			StringBuffer buf= new StringBuffer();
			if(emails!=null){
				for (String email : emails) {
					buf.append(email + ",");
				}
			}
			emailStr = buf.toString();
			if (StringUtils.isNotEmpty(emailStr)) {
				emailStr = emailStr.substring(0, emailStr.length() - 1);
			}
			newSendEmail.setSendEmail(emailStr);
			
			//生成邮件模板
			List<CustomEmailTemplate> templates = this.customEmailTemplateService.find(user);		
			model.addAttribute("templates", templates);
			
			model.addAttribute("sendEmail", newSendEmail);
			return "modules/amazoninfo/reviewer/reviewerSendEmail";
			
		}
	}

	@RequestMapping(value = "records")
	public String records(AmazonReviewer reviewer, Model model) {
		model.addAttribute("reviewer", reviewer);
		return "modules/amazoninfo/reviewer/reviewerRecords";
	}
	
	@RequestMapping(value = "addComment")
	public String addComment() {
		return "modules/amazoninfo/reviewer/addComment";
	}
	
	@ResponseBody
	@RequestMapping(value = "saveComment")
	public String saveComment(String comment, AmazonReviewer reviewer) {
		//记录最后联系人
		reviewer.setContactBy(UserUtils.getUser());
		amazonReviewerService.save(reviewer);
		ReviewerComment comm = new ReviewerComment();
		comm.setComment(HtmlUtils.htmlUnescape(comment));
		comm.setType("0");
		comm.setAmazonReviewer(reviewer);
		reviewerCommentService.save(comm);
		return "1";
	}
	
	//分平台属性导出
	@RequestMapping(value = "exportReviewEvent")
	public String exportReviewEvent(Event event, String reviewType, HttpServletRequest request,HttpServletResponse response, Model model) {
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

		HSSFCell cell = null;
		if(event.getCreateDate()==null || event.getEndDate()==null){
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			event.setCreateDate(DateUtils.addMonths(today,-1));
			event.setEndDate(today);
		}
		if (StringUtils.isEmpty(event.getType())) {
			event.setType("8");
		}
		List<Event> eventList = eventService.findReviewOrderEvent(event, reviewType);
		List<String> products = Lists.newArrayList();
		List<Integer> reviewerEmails = Lists.newArrayList();
        for (Event eve : eventList) {
        	String name = "unknown";
        	if(StringUtils.isNotEmpty(eve.getRemarks())&&StringUtils.isNotEmpty(eve.getCountry())){
        		String temp = amazonProductService.findProductName(eve.getRemarks(), eve.getCountry());
            	if(StringUtils.isNotEmpty(temp)){
            		if(temp.contains("other")){
            			name = "other";
            		}else{
            			name = temp;
            		}
            	}
        	}
        	products.add(name);
        	//事件关联的邮箱所属的reviewer是否有待处理的邮件
        	String email = eve.getCustomEmail();
        	AmazonReviewer reviewer = amazonReviewerService.findReviewer(email, null);
        	List<String> emailList = Lists.newArrayList();
			if (reviewer != null) {
				emailList.add(reviewer.getReviewEmail());
				if (StringUtils.isNotBlank(reviewer.getEmail1())) {
					emailList.add(reviewer.getEmail1());
				}
				if (StringUtils.isNotBlank(reviewer.getEmail2())) {
					emailList.add(reviewer.getEmail2());
				}
				List<ReviewerEmail> list = reviewerEmailService.findByReviewerEmail(emailList);
				if (list.size() > 0) {
					reviewerEmails.add(list.size()); //有待处理邮件
				} else {
					reviewerEmails.add(0);
				}
			} else {
				reviewerEmails.add(0);
			}
		}
		List<String> title = Lists.newArrayList("评测产品", "国家", "客户名", "邮箱", "负责人", "完成情况", "评测开始时间", "评测完成时间");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int rowIndex = 1;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < eventList.size(); i++) {
			Event reviewEvent = eventList.get(i);
			int j = 0;
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			String productName = "与产品无关";
			if (StringUtils.isNotEmpty(reviewEvent.getRemarks())) {
				productName = products.get(i);
			}
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(productName);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(reviewEvent.getCountry());
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(reviewEvent.getCustomName());
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(reviewEvent.getCustomEmail());
			if (reviewEvent.getMasterBy() != null) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(reviewEvent.getMasterBy().getName());
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(reviewEvent.getStateStr());
			if (reviewEvent.getCreateDate() != null) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(format.format(reviewEvent.getCreateDate()));
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			if("2".equals(reviewEvent.getState())){
				if (reviewEvent.getEndDate() != null) {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(format.format(reviewEvent.getEndDate()));
				} else {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
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

			String fileName = "产品评测报告" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("产品评测报告导出异常", e);
		}
		return null;
	}

	/**
	 * 评测事件列表
	 */
	@RequestMapping(value = "reviewEventList")
	public String reviewEventList(Event event, String reviewType, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(event.getCreateDate()==null || event.getEndDate()==null){
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			event.setCreateDate(DateUtils.addMonths(today,-1));
			event.setEndDate(today);
		}
		if (StringUtils.isEmpty(event.getType())) {
			event.setType("8");
		}
		List<Event> eventList = eventService.findReviewOrderEvent(event, reviewType);
		List<String> products = Lists.newArrayList();
		List<Integer> reviewerEmails = Lists.newArrayList();
        for (Event eve : eventList) {
        	String name = "unknown";
        	if(StringUtils.isNotEmpty(eve.getRemarks())&&StringUtils.isNotEmpty(eve.getCountry())){
        		String temp = amazonProductService.findProductName(eve.getRemarks(), eve.getCountry());
            	if(StringUtils.isNotEmpty(temp)){
            		if(temp.contains("other")){
            			name = "other";
            		}else{
            			name = temp;
            		}
            	}
        	}
        	products.add(name);
        	//事件关联的邮箱所属的reviewer是否有待处理的邮件
        	String email = eve.getCustomEmail();
        	AmazonReviewer reviewer = amazonReviewerService.findReviewer(email, null);
        	List<String> emailList = Lists.newArrayList();
			if (reviewer != null) {
				emailList.add(reviewer.getReviewEmail());
				if (StringUtils.isNotBlank(reviewer.getEmail1())) {
					emailList.add(reviewer.getEmail1());
				}
				if (StringUtils.isNotBlank(reviewer.getEmail2())) {
					emailList.add(reviewer.getEmail2());
				}
				List<ReviewerEmail> list = reviewerEmailService.findByReviewerEmail(emailList);
				if (list.size() > 0) {
					reviewerEmails.add(list.size()); //有待处理邮件
				} else {
					reviewerEmails.add(0);
				}
			} else {
				reviewerEmails.add(0);
			}
		}
		model.addAttribute("eventList", eventList);
        model.addAttribute("products", products);
        model.addAttribute("reviewerEmails", reviewerEmails);
        model.addAttribute("type", reviewType);
		return "modules/amazoninfo/reviewer/reviewEventList";
	}

	/**
	 * 评测事件相关的邮件
	 */
	@RequestMapping(value = "viewEmail")
	public String viewEmail(String email, HttpServletRequest request, HttpServletResponse response, Model model) {
		AmazonReviewer reviewer = amazonReviewerService.findReviewer(email, null);
    	List<String> emailList = Lists.newArrayList();
    	List<ReviewerEmail> list = Lists.newArrayList();
		if (reviewer != null) {
			emailList.add(reviewer.getReviewEmail());
			if (StringUtils.isNotBlank(reviewer.getEmail1())) {
				emailList.add(reviewer.getEmail1());
			}
			if (StringUtils.isNotBlank(reviewer.getEmail2())) {
				emailList.add(reviewer.getEmail2());
			}
			list = reviewerEmailService.findByReviewerEmail(emailList);
		}
		if (list.size() == 1) {
			return "redirect:"+Global.getAdminPath()+"/amazoninfo/reviewerEmail/form?id=" + list.get(0).getId();
		}
		model.addAttribute("list", list);
		return "modules/amazoninfo/reviewer/reviewEventEmailList";
	}

	/**
	 * 评测记录
	 */
	@RequestMapping(value = "reviewEventHis")
	public String reviewEventHis(String email, HttpServletRequest request, HttpServletResponse response, Model model) {
		AmazonReviewer reviewer = amazonReviewerService.findReviewer(email, null);
		List<String> emailList = Lists.newArrayList();
		if (reviewer != null) {
			emailList.add(reviewer.getReviewEmail());
			if (StringUtils.isNotBlank(reviewer.getEmail1())) {
				emailList.add(reviewer.getEmail1());
			}
			if (StringUtils.isNotBlank(reviewer.getEmail2())) {
				emailList.add(reviewer.getEmail2());
			}
			//amazon平台网站后缀处理
			String key = reviewer.getCountry();
			if ("jp,uk".contains(key)){
				key = "co."+key;
			}else if("mx".equals(key)){
				key="com."+key;
			}
			model.addAttribute("key", key);
		} else {
			emailList.add(email);
		}
		List<Event> eventList = eventService.findReviewOrderEventHis(emailList);
		List<String> products = Lists.newArrayList();
        for (Event eve : eventList) {
        	String name = "unknown";
        	if(StringUtils.isNotEmpty(eve.getRemarks())&&StringUtils.isNotEmpty(eve.getCountry())){
        		String temp = amazonProductService.findProductName(eve.getRemarks(), eve.getCountry());
            	if(StringUtils.isNotEmpty(temp)){
            		if(temp.contains("other")){
            			name = "other";
            		}else{
            			name = temp;
            		}
            	}
        	}
        	products.add(name);
		}
		model.addAttribute("eventList", eventList);
		model.addAttribute("reviewer", reviewer);
        model.addAttribute("products", products);
		return "modules/amazoninfo/reviewer/reviewEventHis";
	}
	
	
	/***
	 *初始化一次vine voice 数据 
	 */
	@RequestMapping(value = "initVineVoice")
	public String initVineVoice(String email, HttpServletRequest request, HttpServletResponse response, Model model) {
		this.amazonReviewerService.initVine();
		return "初始化vineVoice成功";
	}
	
}
