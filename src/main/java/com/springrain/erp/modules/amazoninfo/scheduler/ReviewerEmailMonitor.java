package com.springrain.erp.modules.amazoninfo.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.mail.Message;
import javax.mail.search.SearchTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.AmazonReviewer;
import com.springrain.erp.modules.amazoninfo.entity.ReviewerEmail;
import com.springrain.erp.modules.amazoninfo.service.AmazonReviewerService;
import com.springrain.erp.modules.amazoninfo.service.ReviewerEmailService;
import com.springrain.erp.modules.custom.entity.AutoReply;
import com.springrain.erp.modules.custom.entity.CustomEmail;
import com.springrain.erp.modules.custom.service.AutoReplyService;
import com.springrain.erp.modules.sys.entity.User;

public class ReviewerEmailMonitor {
	
	@Autowired
	private ReviewerEmailManager reviewerEmailManager;
	
	@Autowired
	private ReviewerEmailService reviewerEmailService;
	
	@Autowired
	private AmazonReviewerService amazonReviewerService;

	@Autowired
	private AutoReplyService autoReplyService;

	@Autowired(required = false)
	private SearchTerm filter;

	private static Cache<String, List<User>> masterUserCache = CacheBuilder.newBuilder().expireAfterWrite(24L, TimeUnit.HOURS).build();

	private static boolean isStart = true;
	private static long expired1;
	private static long errorTime1;
	private static final Logger LOGGER = LoggerFactory.getLogger(ReviewerEmailMonitor.class);

	private static String flag = "";

	private static Cache<String, Integer> cache = CacheBuilder.newBuilder().expireAfterWrite(2L, TimeUnit.HOURS).build();
	private static Cache<String, Set<String>> cache1 = CacheBuilder
			.newBuilder().expireAfterWrite(1L, TimeUnit.DAYS).build();

	public void monitor() {
		long st = System.currentTimeMillis();
		Thread thread = new Thread(){
			public void run() {
				int hour = new Date().getHours();
				String date = DateUtils.getDate();
				if (isStart) {
					if (hour > 11)
						flag = date;
					try {
						expired1 = 60 * Integer.parseInt(Global
								.getConfig("custom.email.expire"));
					} catch (NumberFormatException localNumberFormatException) {
					}
					isStart = false;
				}
				if ((!flag.equals(date)) && (hour > 10)) {
					expired1 = 1470L;
					flag = date;
				}
				if(expired1>25){
					LOGGER.info("开始下载评测邮件start get Reviewer Email!!!!!!" + expired1);
				}
				errorTime1 = expired1 + 5L;
				try {
					receiveNeededMail(expired1, reviewerEmailManager);
					errorTime1 = 0L;
				} catch (Exception e) {
					LOGGER.error(reviewerEmailManager.getManagerInfo().getUserName()
							+ ":" + e.getMessage(), e);
				}
			};
		};
		thread.start();
		long proess = 0;
		while(thread.isAlive()){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}
			proess = Math
					.round((float) (System.currentTimeMillis() - st) / 60000.0F + 0.5F);
			if((expired1>1000L&&proess>30L)|| (expired1<1000L&&proess>10L)){
				LOGGER.warn("线程超时了,准备打断中...");
				try {
					thread.stop();
				} catch (Exception e) {}
				masterUserCache.cleanUp();
				cache.cleanUp();
				cache1.cleanUp();
				reviewerEmailManager.clearConnection();
				break;
			}
		}
		expired1 = errorTime1 + 5L + proess;
	}

	private void receiveNeededMail(long expired, final MailManager mailManager)
			throws Exception {
		List<Message> messages = mailManager.receiveMail(Integer
				.parseInt(expired + ""));
		List<String> delRepeat = Lists.newArrayList();
		String server = mailManager.getManagerInfo().getUserName();
		for (final Message message : messages) {
			Date date = null;
			try {
				date = mailManager.getSentDate(message);
			} catch (Exception e) {
				LOGGER.error("解析时间出错！");
			}
			String messageId = mailManager.getMessageId(message) + "!"
					+ (date == null ? "null" : Long.valueOf(date.getTime()));
			boolean isProcessed = true;
			if (!messageId.startsWith("null")) {
				isProcessed = this.reviewerEmailService.isProcessedEmail(messageId);
			}
			if (delRepeat.contains(messageId))
				continue;
			if ((isProcessed)
					|| ((this.filter != null) && (!this.filter.match(message)))) {
				continue;
			}
			String fromEmail = "";
			try {
				fromEmail = MailManager.getFrom(message);
			} catch (Exception e) {
				LOGGER.error(fromEmail + ":转化出错了！", e);
			}
			String subject = mailManager.getSubject(message);
			// 根据邮箱找到最近一次的联系人作为负责人
			AmazonReviewer reviewer = amazonReviewerService.findReviewer(fromEmail, null);
			List<String> emailList = Lists.newArrayList();
			if (reviewer != null) {
				emailList.add(reviewer.getReviewEmail());
				if (StringUtils.isNotBlank(reviewer.getEmail1())) {
					emailList.add(reviewer.getEmail1());
				}
				if (StringUtils.isNotBlank(reviewer.getEmail2())) {
					emailList.add(reviewer.getEmail2());
				}
			} else {
				emailList.add(fromEmail);
			}
			//匹配评测者的三个邮箱
			User master = reviewerEmailService.findMastByFromEmail(emailList);
			ReviewerEmail reviewerEmail = new ReviewerEmail(messageId, subject,
					date == null ? new Date() : date, "0", master, fromEmail);
			reviewerEmail.setRevertServerEmail(server);
			reviewerEmail.setFormReviewer(reviewer);	//来自哪个测评人
			try {
				CustomEmail customEmail = new CustomEmail();
				mailManager.getMail(message, customEmail);
				reviewerEmail.setUrgent(mailManager.getPriority(message));
				reviewerEmail.setReceiveContent(stringFilter(customEmail.getReceiveContent()));
				reviewerEmail.setAttchmentPath(customEmail.getAttchmentPath());
				reviewerEmail.setInlineAttchmentPath(customEmail.getInlineAttchmentPath());
			} catch (Exception e) {
				LOGGER.error(subject + ":转化出错了！", e);
			}
			reviewerEmail.setCreateBy(master);
			//邮件自动转发
			if (master != null) {
				AutoReply autoReply = new AutoReply();
				autoReply.setType("3");
				autoReply.setCreateBy(master);
				autoReply = autoReplyService.findByUser(autoReply);
				//转发目标人已经离职
				if ((autoReply != null) && (autoReply.getForwardTo() != null) && "1".equals(autoReply.getForwardTo().getDelFlag())) {
					autoReply.setUsedForward("0");	//停止
					autoReplyService.save(autoReply);
				}
				if ((autoReply != null) && ("1".equals(autoReply.getUsedForward()))
						&& (autoReply.getForwardTo() != null) && "0".equals(autoReply.getForwardTo().getDelFlag())) {
					reviewerEmail.setMasterBy(autoReply.getForwardTo());
					reviewerEmail.setTransmit(master.getName() + "  auto forwardTo "
							+ reviewerEmail.getMasterBy().getName());
				}
			}
			try{
				reviewerEmail.setSubject(Encodes.filterOffUtf8Mb4(reviewerEmail.getSubject()));
				this.reviewerEmailService.save(reviewerEmail);
			}catch(Exception e){
				LOGGER.error("评测邮件 " + reviewerEmail.getSubject()+"存储错误"+e.getMessage());
			}
			delRepeat.add(messageId);
		}
	}

	public static String stringFilter(String str) throws PatternSyntaxException {
		if(StringUtils.isBlank(str)){
			return "";
		}
		String regEx = "[^\u0000-\uFFFF0-9\\s@#!\\?%\\+\\.!/\":]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
}