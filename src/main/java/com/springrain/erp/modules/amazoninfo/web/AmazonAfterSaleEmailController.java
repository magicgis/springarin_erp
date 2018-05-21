package com.springrain.erp.modules.amazoninfo.web;

import java.util.Date;
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
import org.springframework.web.multipart.MultipartFile;
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
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonComment;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonCustomFilter;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerFilterService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.custom.entity.CustomEmailTemplate;
import com.springrain.erp.modules.custom.service.CustomEmailTemplateService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 亚马逊售后邮件Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/afterSale")
public class AmazonAfterSaleEmailController extends BaseController {
	
	@Autowired
	private AmazonCustomerFilterService customerFilterService;
	
	@Autowired
	private CustomEmailTemplateService customEmailTemplateService;
	
	@Autowired
	private MailManager mailManager;
	
	@Autowired
	private SystemService systemService;

	@Autowired
	private AmazonProductService amazonProductService;
	
	@ModelAttribute
	public AmazonCustomFilter get(@RequestParam(required=false) Integer id) {
		if (id != null){
			return customerFilterService.get(id);
		}else{
			return new AmazonCustomFilter();
		}
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(AmazonCustomFilter amazonCustomFilter, String aboutMe, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AmazonCustomFilter> page = new Page<AmazonCustomFilter>(request, response);
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("id desc");
		} else {
			page.setOrderBy(orderBy);
		}
		page.setPageSize(5000);
		if (amazonCustomFilter.getEndDate() == null) {
			Date today = new Date();
			amazonCustomFilter.setEndDate(today);
			amazonCustomFilter.setStartDate(DateUtils.addMonths(today, -24));
		}
		amazonCustomFilter.setAuditState("1");//只查询审批通过的
		page = customerFilterService.find(page, amazonCustomFilter, aboutMe);
		model.addAttribute("page", page);
		model.addAttribute("aboutMe", aboutMe);
		model.addAttribute("offices", UserUtils.getOfficeList());
		return "modules/amazoninfo/amazonAfterSaleTaskList";
	}
	
	@RequestMapping(value = {"auditList"})
	public String auditList(AmazonCustomFilter amazonCustomFilter, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AmazonCustomFilter> page = new Page<AmazonCustomFilter>(request, response);
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("id desc");
		} else {
			page.setOrderBy(orderBy);
		}
		if (amazonCustomFilter.getEndDate() == null) {
			Date today = new Date();
			amazonCustomFilter.setEndDate(today);
			amazonCustomFilter.setStartDate(DateUtils.addMonths(today, -3));
		}
		if (amazonCustomFilter.getAuditState() == null) {
			amazonCustomFilter.setAuditState("0");
		}
		page = customerFilterService.find(page, amazonCustomFilter, null);
		model.addAttribute("page", page);
		return "modules/amazoninfo/amazonAfterSaleAuditList";
	}
	
	@RequestMapping(value = {"save"})
	public String save(AmazonCustomFilter customFilter, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		if ("4".equals(customFilter.getTaskType())) {
			customFilter.setGood("1");//留过好评
		}
		if (StringUtils.isNotEmpty(customFilter.getPn1()) && customFilter.getPn1().length() >= 1000) {
			model.addAttribute("message", "保存失败,购买过的商品名超出长度限制");
			model.addAttribute("customFilter", customFilter);
			model.addAttribute("saveFlag", "1");

			//邮件模板
			CustomEmailTemplate template = this.customEmailTemplateService.get(customFilter.getTemplate().getId());
			List<CustomEmailTemplate> templates = this.customEmailTemplateService.findAfterSale(template.getTemplateType(), customFilter.getCountry());
			model.addAttribute("templates", templates);
			//购买过的商品
			List<String> productNames = amazonProductService.findAllProductName();
			String[] productsName = null;
			if (StringUtils.isNotEmpty(customFilter.getPn1())) {
				productsName = customFilter.getPn1().split(",");
			}
			if (productsName != null && productsName.length > 0) {
				productNames.removeAll(Lists.newArrayList(productsName));
				model.addAttribute("productsName", Lists.newArrayList(productsName));
			}
			model.addAttribute("productNames", productNames);
			//未购买过的商品
			List<String> noProductNames = amazonProductService.findAllProductName();
			String[] noProductsName = null;
			if (StringUtils.isNotEmpty(customFilter.getPn11())) {
				noProductsName = customFilter.getPn11().split(",");
			}
			if (noProductsName != null && noProductsName.length > 0) {
				noProductNames.removeAll(Lists.newArrayList(noProductsName));
				model.addAttribute("noProductsName", Lists.newArrayList(noProductsName));
			}
			model.addAttribute("noProductNames", noProductNames);
			return "modules/amazoninfo/amazonCustomerFilterForm";
		} 
		String message = "修改任务成功！";
		boolean flag = false;	//标记是否发邮件通知
		if (customFilter.getId() == null) {
			flag = true;
			customFilter.setCreateBy(UserUtils.getUser());
			customFilter.setCreateDate(new Date());
			customFilter.setDelFlag("0");
			customFilter.setState("0");
			customFilter.setAuditState("0");
			message = "保存任务成功！";
		} 
		/*else if (!"0".equals(customFilter.getAuditState())) {
			//如果修改前已经审批过则再次发邮件通知,反之如果修改前还未审批则不通知
			flag = true;
		}*/
		//审批否决的修改后重新审批
		if ("2".equals(customFilter.getAuditState())){
			customFilter.setAuditState("0");
		}
		//如果已开始任务,先取消等待的任务
		if ("1".equals(customFilter.getState())) {
			customerFilterService.cancel(customFilter);
			customFilter.setState("1");
		}
		//int total = customerFilterService.queryCount(customFilter);
		//customFilter.setTotalCustomer(total);
		customerFilterService.save(customFilter);
		if (flag) {
			List<User> list = systemService.findUserByPermission("amazoninfo:afterSale:approve");
			if (list == null || list.size() == 0) {
				logger.warn("未找到售后邮件审批负责人");
				addMessage(redirectAttributes, message);
				return "redirect:"+Global.getAdminPath()+"/amazoninfo/afterSale/auditList/?repage";
			}
        	String toAddress = "";
        	StringBuffer buf= new StringBuffer();
        	for (User user : list) {
        		buf.append(user.getEmail() + ",");
			}
        	toAddress=buf.toString();
        	if (StringUtils.isNotEmpty(toAddress)) {
        		toAddress = toAddress.substring(0, toAddress.length()-1);
			} else {
				logger.warn("未找到售后邮件审批负责人");
				addMessage(redirectAttributes, message);
				return "redirect:"+Global.getAdminPath()+"/amazoninfo/afterSale/auditList/?repage";
			}
			//String toAddress = "linda@inateck.com";
			String content = "<p><span style='font-size:20px'>Hi:<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+customFilter.getCreateBy().getName()+"提交了一个售后邮件任务,请尽快处理。</span>" +
        			"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/afterSale/auditList'>点击处理</a></p>";
			final MailInfo mailInfo = new MailInfo(toAddress, "售后邮件群发任务通知", new Date());
			mailInfo.setContent(content);
			new Thread(){
			    public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
		//异步查询客户总数
		final Integer id = customFilter.getId();
		new Thread(){
		    public void run(){
		    	AmazonCustomFilter filter = customerFilterService.get(id);
				int total = customerFilterService.queryCount(filter);
				filter.setTotalCustomer(total);
				customerFilterService.save(filter);
			}
		}.start();
		message += "稍后刷新页面查看客户总数";
		addMessage(redirectAttributes, message);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/afterSale/auditList/?repage";
	}
	
	@RequestMapping(value = {"preview"})
	public String preview(AmazonCustomFilter customFilter, HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("customFilter", customFilter);
		if (StringUtils.isNotEmpty(customFilter.getPn1()) && customFilter.getPn1().length() >= 1000) {
			model.addAttribute("message", "预览客户数失败,购买过的商品名超出长度限制");
		} else {
			int total = customerFilterService.queryCount(customFilter);
			model.addAttribute("message", "满足当前条件的用户数为：" + total);
			model.addAttribute("saveFlag", "1");
		}
		//邮件模板
		CustomEmailTemplate template = this.customEmailTemplateService.get(customFilter.getTemplate().getId());
		List<CustomEmailTemplate> templates = this.customEmailTemplateService.findAfterSale(template.getTemplateType(), customFilter.getCountry());
		model.addAttribute("templates", templates);
		//购买过的商品
		List<String> productNames = amazonProductService.findAllProductName();
		String[] productsName = null;
		if (StringUtils.isNotEmpty(customFilter.getPn1())) {
			productsName = customFilter.getPn1().split(",");
		}
		if (productsName != null && productsName.length > 0) {
			productNames.removeAll(Lists.newArrayList(productsName));
			model.addAttribute("productsName", Lists.newArrayList(productsName));
		}
		model.addAttribute("productNames", productNames);
		//未购买过的商品
		List<String> noProductNames = amazonProductService.findAllProductName();
		String[] noProductsName = null;
		if (StringUtils.isNotEmpty(customFilter.getPn11())) {
			noProductsName = customFilter.getPn11().split(",");
		}
		if (noProductsName != null && noProductsName.length > 0) {
			noProductNames.removeAll(Lists.newArrayList(noProductsName));
			model.addAttribute("noProductsName", Lists.newArrayList(noProductsName));
		}
		model.addAttribute("noProductNames", noProductNames);
		return "modules/amazoninfo/amazonCustomerFilterForm";
	}

	@ResponseBody
	@RequestMapping(value = {"ajaxPreview"})
	public String ajaxPreview(AmazonCustomFilter customFilter, @RequestParam(required=false)MultipartFile attachmentFile, HttpServletRequest request, HttpServletResponse response, Model model) {
		int total = customerFilterService.queryCount(customFilter);
		return "满足当前条件的用户数为：" + total;
	}
	
	@RequestMapping(value = {"form"})
	public String form(AmazonCustomFilter customFilter, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(customFilter.getEndDate()==null){
			Date end = DateUtils.getDateStart(new Date());
			customFilter.setEndDate(end);
			customFilter.setStartDate(DateUtils.addMonths(end, -3));
		}
		//邮件模板
		if (customFilter.getTemplate() != null) {
			List<CustomEmailTemplate> templates = this.customEmailTemplateService.findAfterSale(customFilter.getTemplate().getTemplateType(), customFilter.getCountry());
			model.addAttribute("templates", templates);
		}
		model.addAttribute("customFilter", customFilter);
		//购买过的商品
		List<String> productNames = amazonProductService.findAllProductName();
		String[] productsName = null;
		if (StringUtils.isNotEmpty(customFilter.getPn1())) {
			productsName = customFilter.getPn1().split(",");
		}
		if (productsName != null && productsName.length > 0) {
			productNames.removeAll(Lists.newArrayList(productsName));
			model.addAttribute("productsName", Lists.newArrayList(productsName));
		}
		model.addAttribute("productNames", productNames);
		//未购买过的商品
		List<String> noProductNames = amazonProductService.findAllProductName();
		String[] noProductsName = null;
		if (StringUtils.isNotEmpty(customFilter.getPn11())) {
			noProductsName = customFilter.getPn11().split(",");
		}
		if (noProductsName != null && noProductsName.length > 0) {
			noProductNames.removeAll(Lists.newArrayList(noProductsName));
			model.addAttribute("noProductsName", Lists.newArrayList(noProductsName));
		}
		model.addAttribute("noProductNames", noProductNames);
		return "modules/amazoninfo/amazonCustomerFilterForm";
	}
	
	@RequestMapping(value = {"view"})
	public String view(AmazonCustomFilter customFilter, String flag, HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("customFilter", customFilter);
		model.addAttribute("flag", flag);
		//邮件模板
		if (customFilter.getTemplate() != null) {
			List<CustomEmailTemplate> templates = this.customEmailTemplateService.findAfterSale(customFilter.getTemplate().getTemplateType(), customFilter.getCountry());
			model.addAttribute("templates", templates);
		}
		return "modules/amazoninfo/amazonCustomerFilterView";
	}
	
	@RequestMapping(value = {"delete"})
	public String delete(AmazonCustomFilter customFilter, HttpServletRequest request, HttpServletResponse response, Model model) {
		customFilter = customerFilterService.get(customFilter.getId());
		customFilter.setDelFlag("1");
		customerFilterService.save(customFilter);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/afterSale/?repage";
	}
	
	@RequestMapping(value = {"cancel"})
	public String cancel(AmazonCustomFilter customFilter, HttpServletRequest request, HttpServletResponse response, Model model) {
		customerFilterService.cancel(customFilter);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/afterSale/?repage";
	}
	
	/**
	 * 暂时停止任务
	 */
	@RequestMapping(value = {"stop"})
	public String stop(AmazonCustomFilter customFilter, HttpServletRequest request, HttpServletResponse response, Model model) {
		customFilter.setState("4");	//改为暂停状态
		customerFilterService.save(customFilter);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/afterSale/?repage";
	}
	
	/**
	 * 恢复停止的任务
	 */
	@RequestMapping(value = {"restart"})
	public String restart(AmazonCustomFilter customFilter, HttpServletRequest request, HttpServletResponse response, Model model) {
		customFilter.setState("1");	//恢复到进行中状态
		customerFilterService.save(customFilter);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/afterSale/?repage";
	}
	
	@RequestMapping(value = {"sendList"})
	public String sendList(AmazonComment amazonComment, String isReply, String isReview, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AmazonComment> page = new Page<AmazonComment>(request, response);
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("id desc");
		} else {
			page.setOrderBy(orderBy);
		}
		if(amazonComment.getSentDate()==null){
			Date end = DateUtils.getDateStart(new Date());
			amazonComment.setSentDate(end);
			amazonComment.setCreateDate(DateUtils.addMonths(end, -1));
		}
		//评论产品对应的asin列表，匹配评论表中的asin,得出产品评论数据
		List<String> asinList = Lists.newArrayList();
		if(amazonComment.getTask() != null && StringUtils.isNotEmpty(amazonComment.getTask().getPn2())){
			String productName = amazonComment.getTask().getPn2();
			asinList = customerFilterService.getProductAsinList(amazonComment.getTask().getCountry(),productName);
		}
		page = customerFilterService.findSendList(page, amazonComment, isReply, isReview, asinList);
		model.addAttribute("page", page);
		model.addAttribute("amazonComment", amazonComment);
		model.addAttribute("isReply", isReply);
		model.addAttribute("isReview", isReview);
		model.addAttribute("asinProduct", customerFilterService.getAsinProductMap());
		return "modules/amazoninfo/amazonAfterSaleEmailList";
	}
	
	@RequestMapping(value = {"viewSendInfo"})
	public String viewSendInfo(Integer id, HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("amazonComment", customerFilterService.getComment(id));
		return "modules/amazoninfo/amazonCommentView";
	}
	
	@RequestMapping(value = "approval")
	public String approval(AmazonCustomFilter amazonCustomFilter, String stateStr, HttpServletRequest request, 
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		amazonCustomFilter.setAuditState(stateStr);
		customerFilterService.save(amazonCustomFilter);
		if (amazonCustomFilter.getCreateBy() != null) {
			final MailInfo mailInfo = new MailInfo(amazonCustomFilter.getCreateBy().getEmail(), "售后邮件任务审批提醒("+DateUtils.getDate("yyyy/MM/dd")+")", new Date());
			String result = "1".equals(stateStr)?"审批通过":"被否决";
			String contents = "<p><span style='font-size:20px'>Hi "+amazonCustomFilter.getCreateBy().getName()+",<br/>&nbsp;&nbsp;&nbsp;&nbsp;你提交的["+amazonCustomFilter.getReason()+"]售后邮件任务已经"+result+"，请知悉。" +
					"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/afterSale/auditList?auditState="+stateStr+"'>点击查看</a></span>";
			mailInfo.setContent(contents);
			new Thread(){
			    public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
		addMessage(redirectAttributes, "审批操作成功！");
        return "redirect:"+Global.getAdminPath()+"/amazoninfo/afterSale/auditList/?repage";
	}
	
	@RequestMapping(value = "batchApproval")
	public String batchApproval(@RequestParam("eid[]")String[] eid, String state, HttpServletRequest request, 
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Set<String> emailSet = Sets.newHashSet();
		for (String str : eid) {
			Integer id = Integer.parseInt(str);
			AmazonCustomFilter amazonCustomFilter = customerFilterService.get(id);
			amazonCustomFilter.setAuditState(state);
			emailSet.add(amazonCustomFilter.getCreateBy().getEmail());
			customerFilterService.save(amazonCustomFilter);
		}
		
		if (emailSet.size() > 0) {
			String toAddress = "";
			StringBuffer buf= new StringBuffer();
			for (String string : emailSet) {
				buf.append(string + ",");
			}
			toAddress = buf.toString();
        	toAddress = toAddress.substring(0, toAddress.length()-1);
        	final MailInfo mailInfo = new MailInfo(toAddress, "售后邮件任务审批提醒("+DateUtils.getDate("yyyy/MM/dd")+")", new Date());
        	String result = "1".equals(state)?"审批通过":"被否决";
			String contents = "<p><span style='font-size:20px'>Hi,<br/>&nbsp;&nbsp;&nbsp;&nbsp;你提交的售后邮件任务已经"+result+"，请知悉。" +
					"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/afterSale/auditList?auditState="+state+"'>点击查看</a></span>";
			mailInfo.setContent(contents);
			new Thread(){
			    public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
		addMessage(redirectAttributes, "审批操作成功！");
        return "redirect:"+Global.getAdminPath()+"/amazoninfo/afterSale/auditList/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value={"sendTestEmail"})
	public String sendTestEmail(AmazonCustomFilter customFilter){
		if (customFilter.getTemplate() == null || customFilter.getTemplate().getId() == null) {
			return "请选择模板";
		}
		CustomEmailTemplate template = this.customEmailTemplateService.get(customFilter.getTemplate().getId());
		User user = UserUtils.getUser();
		String subject = template.getTemplateSubject();
		if (StringUtils.isNotEmpty(subject) && subject.contains("${orderId}")) {
			subject = subject.replace("${orderId}", "115-1052722-5092264(测试数据)");
		}
		String asin = "B017SBQDRC";
		String productName = "WP1004(测试数据)";
		String reviewAsin = "RC378ICGA1SKK";
		try {
			List<String> list = customerFilterService.getAsinByCountry(customFilter.getCountry());
			if (list != null && list.size() > 0) {
				asin = list.get(0);
				productName = list.get(1);
				reviewAsin = list.get(2);
			}
		} catch (Exception e) {
			logger.warn(customFilter.getCountry()+"获取asin异常", e);
		}
		String content = template.getTemplateContent();
		//富文本编辑的时候A链接会转义大括号
		if (content.contains("%7b") || content.contains("%7d")) {
			content = content.replaceAll("%7b", "{");
			content = content.replaceAll("%7d", "}");
		}
		if (content.contains("${productName}")) {
			content = content.replace("${productName}", productName);
		}
		while (content.contains("${asin}")) {
			content = content.replace("${asin}", asin);
		}
		if (content.contains("${customerName}")) {
			content = content.replace("${customerName}", "James");
		}
		//内容支持订单号
		if (content.contains("${orderId}")) {
			content = content.replace("${orderId}", "115-1052722-5092264");
		}
		//内容支持评论链接
		if (content.contains("${reviewAsin}")) {
			content = content.replace("${reviewAsin}", reviewAsin);
		}
		MailInfo mailInfo = new MailInfo(user.getEmail(), subject, new Date());
		mailInfo.setContent(content);
		if (StringUtils.isNotEmpty(template.getFileName()) && template.getFileName().contains("/")) {
			mailInfo.setFileName(template.getFileName().split("/")[1]);
			mailInfo.setFilePath(template.getFilePath());
		}
		boolean rs = mailManager.send(mailInfo);
		if (rs) {
			return "测试邮件已发送,请注意查收!";
		} else {
			return "测试邮件发送失败!";
		}
	}
	
	/**
	 * 获取对应类型的邮件模板
	 * @param templateType
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"getTemplate"})
	public String getTemplate(String templateType, String country){
		if (StringUtils.isEmpty(templateType) || StringUtils.isEmpty(country)) {
			return null;
		}
		List<CustomEmailTemplate> list = customEmailTemplateService.findAfterSale(templateType, country);
		List<Map<String, String>> rs = Lists.newArrayList();
		for (CustomEmailTemplate customEmailTemplate : list) {
			Map<String,String> map = Maps.newHashMap();
			map.put("key", customEmailTemplate.getId().toString());
			map.put("value", customEmailTemplate.getTemplateName());
			rs.add(map);
		}
		return JSON.toJSONString(rs);
	}
	
	/**
	 * 延期截止时间
	 * @param templateType
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping(value={"updateEndDate"})
	public String updateEndDate(Integer id, String endDateStr){
		try {
			AmazonCustomFilter amazonCustomFilter = customerFilterService.get(id);
			Date newDate = new Date(endDateStr);
			if (newDate.before(amazonCustomFilter.getEndDate())) {
				return "修改失败,新的截止时间不能小于原截止时间";
			}
			amazonCustomFilter.setEndDate(newDate);
			customerFilterService.save(amazonCustomFilter);
		} catch (Exception e) {
			logger.error("修改截止时间失败", e);
			return "修改截止时间失败";
		}
		return "1";
	}
	
	//批量移交
	@RequestMapping(value = "batchTransmitOther")
	public String batchTransmitOther(@RequestParam("eid[]")Integer[] eid, String userId , Model model, RedirectAttributes redirectAttributes) {
		User user = UserUtils.getUserById(userId);
		for (Integer id : eid) {
			AmazonCustomFilter amazonCustomFilter = customerFilterService.get(id);
			amazonCustomFilter.setCreateBy(user);
			customerFilterService.save(amazonCustomFilter);
		}
		addMessage(redirectAttributes, "Operation is successful！");
		return "redirect:" + Global.getAdminPath() + "/amazoninfo/afterSale/list";
	}
	
}
