package com.springrain.erp.modules.amazoninfo.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonBuyComment;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonComment;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonCustomFilter;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonCustomer;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonReviewComment;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerFilterService;
import com.springrain.erp.modules.custom.entity.CustomEmailTemplate;
import com.springrain.erp.modules.custom.service.UnsubscribeEmailService;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

public class AfterSaleEmailMonitor {

	private final static Logger logger = LoggerFactory.getLogger(AfterSaleEmailMonitor.class);

	//好评反馈发送的订单记录
	private static List<String> feedbackOrderIds = Lists.newArrayList();
	
	@Autowired
	private AmazonCustomerFilterService customerFilterService;
	
//	@Autowired
//	private SendCustomEmailManager sendCustomEmailManager;
	
	@Autowired
	private SendCustomEmail1Manager sendCustomEmail1Manager;

	@Autowired
	private UnsubscribeEmailService unsubscribeEmailService;

	public void send() {
		try {
			customerFilterService.notice();
		} catch (Exception e) {
			logger.error("售后任务到期通知异常！！", e);
		}
		try {
			Date today = new Date();
			logger.info("开始处理售后邮件");
			//处理任务,添加到待发送中
			//customerFilterService.processingTasks();
			List<AmazonCustomFilter> taskList = customerFilterService.findProcessingTasks();
			for (AmazonCustomFilter amazonCustomFilter : taskList) {
				if ("1".equals(amazonCustomFilter.getState()) && amazonCustomFilter.getEndDate().before(DateUtils.addDays(new Date(), -1))) {
					continue;
				}
				customerFilterService.processingSingleTasks(amazonCustomFilter);
			}
			logger.info("预处理完毕");
			
			//免打扰邮箱
			List<Object> unSentEmails = unsubscribeEmailService.findEmails();
			Set<String> unSents =  Sets.newHashSet();
			for (Object obj : unSentEmails) {
				unSents.add(obj.toString());
			}

			int size = 20000;	//设置一天最多处理邮件数量
			List<AmazonComment> commentList = customerFilterService.getSendEmailList(size);
			logger.info("待发送的邮件数量：" + commentList.size());
			//发送的订单号集合
			List<String> orderIds = customerFilterService.getSendOrderList(null);
			feedbackOrderIds = customerFilterService.getSendOrderList("4");
			//步骤：拿到待发送记录,取模板替换变量,然后发送邮件,修改记录状态
			int i = 0;
			int total = 0;
			int totalFail = 0;
			for (AmazonComment comment : commentList) {
				try {
					MailInfo mailInfo = getMailInfo(comment, unSents, orderIds, today);
					if (mailInfo == null) {
						continue;
					}
					boolean rs = sendCustomEmail1Manager.send(mailInfo);
					//发送成功记录信息
					if (rs) {
						total++;
						comment.setSendFlag("1");	//已发送
						comment.setSentDate(today);
					} else {
						totalFail++;
					}
					Thread.sleep(1000);	//休眠一下,缓解邮件服务器压力
				} catch (Exception e) {
					i++;
					if (i < 5) {	//避免重复日志,只打印5次完整异常信息,后续异常只做提示
						logger.warn("编号为" + comment.getId() + "的邮件发送异常", e);  
					} else {
						logger.warn("编号为" + comment.getId() + "的邮件发送异常," + e.getMessage());
					}
				}
			}
			customerFilterService.saveComments(commentList);	//保存记录
			//标记已完成的任务
			customerFilterService.finishTasks();
			logger.info("处理售后邮件完毕");
		} catch (Exception e) {
			logger.error("处理售后邮件发生异常", e);
		}
		//任务完毕后清空
		feedbackOrderIds.clear();
	}
	
	/**
	 * 普通售后邮件
	 * @param comment
	 * @param unSents
	 * @param orderIds
	 * @param today
	 * @return
	 */
	private MailInfo getMailInfo(AmazonComment comment, Set<String> unSents, List<String> orderIds, Date today){
		AmazonCustomer customer = comment.getCustomer();
		String taskType = comment.getTask().getTaskType();
		if (customer == null || (StringUtils.isEmpty(customer.getAmzEmail()) && StringUtils.isEmpty(customer.getEmail()))) {
			comment.setSendFlag("2");	//无效的记录
			comment.setSentDate(new Date());
			comment.setSendSubject("客户不存在或客户邮箱为空");
			return null;
		}
		String sendEmail = customer.getAmzEmail();
		if (StringUtils.isEmpty(sendEmail)) {
			sendEmail = customer.getEmail();
		}
		if (unSents.contains(sendEmail)) {
			comment.setSendFlag("2");	//无效的记录
			comment.setSentDate(new Date());
			comment.setSendSubject("客户邮箱取消订阅");
			comment.setSendEmail(sendEmail);
			return null;
		}
		String returnFlag = comment.getTask().getReturnFlag();
		if ("0".equals(returnFlag) && (customer.getReturnQuantity()>0 || customer.getRefundMoney()>0)) {
			comment.setSendFlag("2");	//无效的记录
			comment.setSentDate(new Date());
			comment.setSendSubject("客户退过货或退过款");
			comment.setSendEmail(sendEmail);
			return null;
		}
		String productName = comment.getTask().getPn1();
		if (StringUtils.isEmpty(productName)) {
			productName = comment.getTask().getPn2();
		}
		if (StringUtils.isEmpty(productName)) {
			productName = comment.getTask().getPn3();
		}
		String reviewAsin = "";
		AmazonBuyComment buyComment = null;
		if (!"4".equals(taskType)) {
			if (StringUtils.isNotEmpty(productName)) {
				for (AmazonBuyComment c : customer.getBuyComments()) {
					//支持改版后的产品,选择的产品名称为全称,以逗号分隔,类型1为正常订单
					if (("1".equals(c.getType()) || "2".equals(c.getType())) && (c.getProductName().contains(productName) || productName.contains(c.getProductName()))) {
						if (comment.getTask().getStartDate().before(c.getTypeDate()) && comment.getTask().getEndDate().after(c.getTypeDate())) {
							buyComment = c;
							for (AmazonReviewComment r : customer.getReviewComments()) {
								if (r.getAsin().equals(c.getAsin())) {
									reviewAsin = r.getReviewAsin();
								}
							}
							break;
						}
					}
				}
			}
		} else {
			if (StringUtils.isEmpty(productName)) {
				productName = "";
			}
			for (AmazonBuyComment c : customer.getBuyComments()) {
				//支持改版后的产品,选择的产品名称为全称,以逗号分隔,类型1为正常订单
				if (("1".equals(c.getType()) || "2".equals(c.getType())) && (c.getProductName().contains(productName) || productName.contains(c.getProductName()))) {
					if (comment.getTask().getStartDate().before(c.getTypeDate()) && comment.getTask().getEndDate().after(c.getTypeDate())) {
						for (AmazonReviewComment r : customer.getReviewComments()) {
							if (r.getAsin().equals(c.getAsin()) && r.getReviewDate().after(c.getTypeDate())
									&& ("4".equals(r.getStar()) || "5".equals(r.getStar()))) {
								buyComment = c;
								reviewAsin = r.getReviewAsin();
								break;
							}
						}
					}
				}
				if (buyComment != null) {
					break;
				}
			}
		}
		if (buyComment == null) {
			comment.setSendFlag("2");	//无效的记录
			comment.setSentDate(new Date());
			comment.setSendSubject("未匹配到正确的购买信息");
			return null;
		}
		try {
			productName = buyComment.getProductName().split(" ")[1];
		} catch (Exception e) {
			productName = buyComment.getProductName();
		}
		
		//过滤延迟发送时间
		if (buyComment.getCreateDate().after(DateUtils.addDays(new Date(), 0 - comment.getTask().getSendDelay()))) {
			return null;
		}
		CustomEmailTemplate template = comment.getTemplate();
		String content = template.getTemplateContent();
		//富文本编辑的时候A链接会转义大括号
		if (content.contains("%7b") || content.contains("%7d")) {
			content = content.replaceAll("%7b", "{");
			content = content.replaceAll("%7d", "}");
		}
		if (content.contains("${productName}")) {
			//替换模板中的产品名称变量
			if (StringUtils.isNotEmpty(productName)) {
				content = content.replace("${productName}", productName);
			} else {
				content = content.replace("${productName}", "Inateck Product");
			}
		}
		String orderId = "";
		String asin = "";
		if (customer.getBuyComments().size() > 0) {
			asin = buyComment.getAsin();
			orderId = buyComment.getOrderId();
		}
		//支持多个链接asin
		while (content.contains("${asin}")) {
			content = content.replace("${asin}", asin);
		}
		//内容支持评论链接
		if (content.contains("${reviewAsin}")) {
			content = content.replace("${reviewAsin}", reviewAsin);
		}
		//内容支持订单号
		if (content.contains("${orderId}")) {
			content = content.replace("${orderId}", orderId);
		}
		if ("4".equals(taskType)) {
			if (orderIds.contains(orderId)) {
				//限制每个订单只发送一次邮件,重复时直接取消
				comment.setSendFlag("2");
				comment.setSentDate(new Date());
				comment.setOrderId(orderId);
				comment.setSendSubject("订单重复发送");
				return null;
			} else {
				orderIds.add(orderId);
			}
		} else {
			if (feedbackOrderIds.contains(orderId)) {
				//限制每个订单好评只发送一次邮件,重复时直接取消
				comment.setSendFlag("2");
				comment.setSentDate(new Date());
				comment.setOrderId(orderId);
				comment.setSendSubject("订单重复发送");
				return null;
			} else {
				feedbackOrderIds.add(orderId);
			}
		}
		
		//内容支持客户名称
		if (content.contains("${customerName}")) {
			if (customer.getName() != null) {
				content = content.replace("${customerName}", customer.getName());
			} else {
				content = content.replace("${customerName}", "Customer");
			}
		}
		String subject = template.getTemplateSubject();
		if (StringUtils.isNotEmpty(subject) && subject.contains("${orderId}")) {
			subject = subject.replace("${orderId}", orderId);
		}
		MailInfo mailInfo = new MailInfo(sendEmail, subject, today);
		mailInfo.setContent(content);
		//支持附件
		if (StringUtils.isNotEmpty(template.getFileName()) && template.getFileName().contains("/")) {
			mailInfo.setFileName(template.getFileName().split("/")[1]);
			mailInfo.setFilePath(template.getFilePath());
		}
		comment.setSendSubject(subject);
		comment.setContent(content);
		comment.setSendEmail(sendEmail);
		comment.setOrderId(orderId);
		return mailInfo;
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		AfterSaleEmailMonitor saleEmailMonitor = new AfterSaleEmailMonitor();
		AmazonCustomerFilterService  customerFilterService= applicationContext.getBean(AmazonCustomerFilterService.class);
		SendCustomEmail1Manager  sendCustomEmail1Manager= applicationContext.getBean(SendCustomEmail1Manager.class);
		saleEmailMonitor.setCustomerFilterService(customerFilterService);
		saleEmailMonitor.setSendCustomEmail1Manager(sendCustomEmail1Manager);
		saleEmailMonitor.send();
		applicationContext.close();
	}

	public AmazonCustomerFilterService getCustomerFilterService() {
		return customerFilterService;
	}

	public void setCustomerFilterService(
			AmazonCustomerFilterService customerFilterService) {
		this.customerFilterService = customerFilterService;
	}

	public SendCustomEmail1Manager getSendCustomEmail1Manager() {
		return sendCustomEmail1Manager;
	}

	public void setSendCustomEmail1Manager(
			SendCustomEmail1Manager sendCustomEmail1Manager) {
		this.sendCustomEmail1Manager = sendCustomEmail1Manager;
	}
	
}
