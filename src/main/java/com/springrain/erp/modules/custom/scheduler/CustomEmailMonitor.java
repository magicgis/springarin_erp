package com.springrain.erp.modules.custom.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.springframework.web.util.HtmlUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.email.MailManagerInfo;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.IdGen;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonComment;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonCustomFilter;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderItem;
import com.springrain.erp.modules.amazoninfo.scheduler.AmazonWSConfig;
import com.springrain.erp.modules.amazoninfo.scheduler.SendCustomEmail1Manager;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerFilterService;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;
import com.springrain.erp.modules.amazoninfo.service.ReviewerEmailService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonRefundService;
import com.springrain.erp.modules.custom.entity.AutoReply;
import com.springrain.erp.modules.custom.entity.CustomEmail;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.custom.service.AutoReplyService;
import com.springrain.erp.modules.custom.service.CustomEmailService;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.custom.web.CustomEmailController;
import com.springrain.erp.modules.custom.web.SendEmailController;
import com.springrain.erp.modules.ebay.service.EbayOrderService;
import com.springrain.erp.modules.psi.entity.PsiProductGroupCustomerEmail;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.Menu;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;

public class CustomEmailMonitor {

	@Autowired
	private CustomEmailManager[] customEmailManager;
	
	@Autowired
	private SendCustomEmail1Manager customEmailBakManager;

	@Autowired
	private CustomEmailService customEmailService;

	@Autowired
	private AutoReplyService autoReplyService;

	@Autowired
	private AmazonOrderService amazonOrderService;
	
	@Autowired
	private EbayOrderService ebayOrderService;
	
	@Autowired
	private AmazonRefundService  amazonRefundService;
	
	@Autowired
	private AmazonCustomerFilterService customerFilterService;
	@Autowired
	private PsiProductGroupUserService 	    groupUserService;
	@Autowired
	private PsiProductTypeGroupDictService 	   groupDictService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private EventService eventService;
	@Autowired
	private AmazonCustomerService amazonCustomerService;
	
	@Autowired
	private ReviewerEmailService reviewerEmailService;
	
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;

	@Autowired(required = false)
	private SearchTerm filter;

	private static Cache<String, List<User>> masterUserCache = CacheBuilder.newBuilder().expireAfterWrite(24L, TimeUnit.HOURS).build();

	private static Map<String, List<User>> cacheMap = Maps.newConcurrentMap();

	private static Map<String, String> customService = Maps.newConcurrentMap();

	private static boolean isStart = true;
	private static long[] expired;
	private static long[] errorTime;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CustomEmailMonitor.class);

	private static Pattern emailPattern = Pattern.compile("[a-zA-Z_0-9\\.]+@[a-zA-Z0-9]+(\\.[a-zA-Z]+)+");
	
	private static String flag = "";

	private static Integer size = 0;
	
	private static Cache<String, Integer> cache = CacheBuilder.newBuilder().expireAfterWrite(2L, TimeUnit.HOURS).build();
	private static Cache<String, Set<String>> cache1 = CacheBuilder
			.newBuilder().expireAfterWrite(1L, TimeUnit.DAYS).build();

	private static final String[] commomEmails = { "do-not-reply@amazon\\..+",
			"non-rispondere@amazon\\..+", "noreply@inateck\\.com",
			"noreply_ca@inateck\\.com" };
	
	public void monitor() {
		final List<AmazonAccountConfig> emailList=amazonAccountConfigService.findEmail();
		if(emailList!=null&&emailList.size()>0){
			customEmailManager=new CustomEmailManager[emailList.size()];
			if(size == 0||(size!=emailList.size())){
				isStart=true;
				size=emailList.size();
				expired=new long[emailList.size()];
				errorTime=new long[emailList.size()];
			}
			long st = System.currentTimeMillis();
			Thread thread = new Thread(){
				public void run() {
					int hour = new Date().getHours();
					String date = DateUtils.getDate();
					if (isStart) {
						if (hour > 11)
							flag = date;
						try {
							for (int i=0;i<emailList.size();i++) {
								expired[i]= 60 * Integer.parseInt(Global.getConfig("custom.email.expire"));
							}
						} catch (NumberFormatException localNumberFormatException) {
						}
						isStart = false;
					}
					if ((!flag.equals(date)) && (hour > 10)) {
						for (int i=0;i<emailList.size();i++) {
							expired[i]= 1500L;
						}
						flag = date;
					}
					if(expired[0]>25){
						LOGGER.info("开始下载邮件start get Email!!!!!!" + expired[0]);
					}
					
					for (int i=0;i<emailList.size();i++) {
						errorTime[i]=expired[i]+ 5L;
						customEmailManager[i]=new CustomEmailManager();
						try {
							AmazonAccountConfig cfg=emailList.get(i);
							MailManagerInfo  mailInfo=customEmailManager[i].setCustomEmailManager(cfg.getEmailType(), cfg.getCustomerEmail(),cfg.getCustomerEmailPassword());
							customEmailManager[i].setManagerInfo(mailInfo);
							receiveNeededMail(expired[i], customEmailManager[i]);
							errorTime[i] = 0L;
						} catch (Exception e) {
							LOGGER.error(customEmailManager[i].getManagerInfo().getUserName()
									+ ":" + e.getMessage(), e);
						}
					}
					if(expired[0]>25){
						LOGGER.info("结束下载邮件!!!!");
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
				if((expired[0]>=1000L&&proess>120L)|| (expired[0]<1000L&&proess>20L)){
					LOGGER.warn("线程超时了,准备打断中...");
					try {
						thread.stop();
					} catch (Exception e) {}
					masterUserCache.cleanUp();
					cache.cleanUp();
					cache1.cleanUp();
					for (int i=0;i<emailList.size();i++) {
						customEmailManager[i].clearConnection();
					}
					break;
				}
			}
			for (int i=0;i<emailList.size();i++) {
				expired[i] = errorTime[i] + 5L + proess;
			}
		}
	}

	private void receiveNeededMail(long expired, final MailManager mailManager)
			throws Exception {
		List<Message> messages = mailManager.receiveMail(Integer
				.parseInt(expired + ""));
		List<String> delRepeat = Lists.newArrayList();
		String server = mailManager.getManagerInfo().getUserName();
		Map<String,Map<String,List<PsiProductGroupCustomerEmail>>> customerMap=groupUserService.findAllGroupCustomerEmail();//产品线ID-国家-用户ID
		Map<String,String> nameAndLineMap=groupDictService.getLineByName();//产品名-产品线ID
		Set<String> userIdSet=systemService.findUserIdByPermissionName(); 
		for (final Message message : messages) {
			String fromEmail = "";
			try {
				fromEmail = MailManager.getFrom(message);
			} catch (Exception e) {
				LOGGER.error(fromEmail + ":转化出错了！", e);
			}
			if(server.equalsIgnoreCase(fromEmail)){
				continue;
			}
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
				isProcessed = this.customEmailService
						.isProcessedEmail(messageId);
			}
			if (delRepeat.contains(messageId))
				continue;
			if ((isProcessed)
					|| ((this.filter != null) && (!this.filter.match(message)))) {
				continue;
			}
			//去掉ashley提出要排除的 12-30
			String subject = mailManager.getSubject(message);
			try{
				subject=stringFilter(subject);
			}catch(Exception e){}
			if("seller-notification@amazon.com".equals(fromEmail)&&("Notification of Active Listings Report availability").equals(subject)||("Your payment is on its way").equals(subject)||("Your Seller Account Funds: Action Required").equals(subject)){
				continue;
			}
			
			CustomEmail customEmail = new CustomEmail(messageId, subject,
					date == null ? new Date() : date, "0", null, fromEmail);
			customEmail.setRevertServerEmail(server);
			try {
				mailManager.getMail(message, customEmail);
				customEmail.setUrgent(mailManager.getPriority(message));
				customEmail.setReceiveContent(stringFilter(customEmail.getReceiveContent()));
			} catch (Exception e) {
				LOGGER.error(subject + ":转化出错了！", e);
			}
			String key = fromEmail.substring(fromEmail.lastIndexOf(".") + 1);
			boolean isJp = server.contains("jp");
			
			boolean isJudge=true;
			if(subject!=null&&subject.startsWith("Offline message sent by")){
				String email = getMail(customEmail.getReceiveContent());
				if(email!=null&&email.length()>0){
					key = email.substring(email.lastIndexOf(".") + 1);
				}
			}else if(StringUtils.isNotEmpty(fromEmail)&&("noreply@inateck.com".equals(fromEmail.toLowerCase().trim())||"site-noreply@inateck.de".equals(fromEmail.toLowerCase().trim()))){
				//解析官网过来的邮件的回信邮箱
				String email = getMail(customEmail.getReceiveContent());
				if(StringUtils.isNotEmpty(email)){
					fromEmail = email ;
					customEmail.setRevertEmail(email);
					key = email.substring(email.lastIndexOf(".") + 1);
					isJudge=false;
				}
			}
			if(key==null||key.length()==0){
				if(isJp){
					key = "jp";
				}else if("support@inateck.com".equals(server)&&ebayOrderService.getisEbayEmail(fromEmail)){
					key = "de";
				}else if (!cacheMap.keySet().contains(key)) {
					key = "com";
				}
			}
			User master = null;
			
			
			
			AmazonComment comment = null;
			//关联售后邮件
			try {
				if ("support@inateck.com".equals(server)) {	//发送到support邮箱的判断是否有售后邮件记录
					comment = customerFilterService.getCommentByEmailOrCustomerId(fromEmail, null);
					if (comment != null) {//如果有售后邮件记录,则关联起来
						//master = comment.getTask().getCreateBy();
						if (comment.getCustomEmail() == null) {
							//回复数加一
							AmazonCustomFilter customFilter = comment.getTask();
							customFilter.setReplyNum(customFilter.getReplyNum() + 1);
							customerFilterService.save(customFilter);
						}
						comment.setCustomEmail(customEmail);
					} else if (fromEmail.contains("@marketplace.amazon")){	//站内非售后邮件判断是不是评测推广回复邮件
						master = reviewerEmailService.findMastByFromEmail(Lists.newArrayList(fromEmail));
						if(master!=null){
							LOGGER.info(master.getName()+","+fromEmail+"站内非售后邮件判断是不是评测推广回复邮件");
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("support邮件关联售后邮件任务异常", e);
			}
			
			
			if(fromEmail.contains("@amazon.")){//发给客服主管
				List<User> userList=systemService.findUsersByRoleName("客服部主管");
				if(userList!=null&&userList.size()>0){
					master=userList.get(0);
					userIdSet.add(master.getId());
				}
			}
			
			//先根据最近联系人找负责人
			if(master == null&&isJudge){
				master = getMasterByEmail(fromEmail, subject,key, comment);
				if(master!=null){
				   LOGGER.info(master.getName()+","+fromEmail+"最近联系人找负责人");
				}
			}
			
			
			//没有找到最近联系人,按产品线分发相应负责人
			if (master == null) {
				try{
					Set<String> orderIdSet= CustomEmailController.getOrders(subject);
					orderIdSet= CustomEmailController.getOrders(customEmail.getReceiveContent());
					if(orderIdSet!=null&&orderIdSet.size()>0){
						for (String orderId: orderIdSet) {
							String productName="";
							AmazonOrder order=amazonOrderService.findByEgNoAdress(orderId);
							if(order!=null){
								for (AmazonOrderItem item: order.getItems()) {
									if(StringUtils.isNotBlank(item.getProductName())){
										productName=item.getName();
										break;
									}
								}
								if(StringUtils.isNotBlank(productName)){
									String lineId=nameAndLineMap.get(productName);
									if(lineId!=null){
										if(customerMap.get(lineId)!=null&&customerMap.get(lineId).get(order.getCountryChar())!=null&&customerMap.get(lineId).get(order.getCountryChar()).size()>0){
											List<PsiProductGroupCustomerEmail> customerList=customerMap.get(lineId).get(order.getCountryChar());
											if(customerList.size()==1){
												master=systemService.getUser(customerList.get(0).getUserId());
											}else{
												master=eventService.getMaster(order.getCountryChar(),customerList);
											}
										}else{
											LOGGER.error(subject+"按产品线分配邮件负责人-该产品线"+lineId+"国家"+order.getCountryChar()+"还未分配具体负责人！");
											master = getMaster(fromEmail, subject,order.getCountryChar(), comment);
										}
									}else{
										LOGGER.error(subject+"按产品线分配邮件负责人产品名"+productName+"未分到具体产品线！");
										master = getMaster(fromEmail, subject,order.getCountryChar(), comment);
									}
								}else{
									LOGGER.error(subject+"按产品线分配邮件负责人订单号"+orderId+"产品名为空！");
									master = getMaster(fromEmail, subject,order.getCountryChar(), comment);
								}
								break;
							}
						}
					}else{
						String customId=amazonCustomerService.findCustomId(fromEmail);
						if(StringUtils.isNotBlank(customId)){
							String productName=amazonCustomerService.findProductName(customId);
							String lineId=nameAndLineMap.get(productName);
							if(lineId!=null){
								if(customerMap.get(lineId)!=null&&customerMap.get(lineId).get(key)!=null&&customerMap.get(lineId).get(key).size()>0){
									List<PsiProductGroupCustomerEmail> customerList=customerMap.get(lineId).get(key);
									if(customerList.size()==1){
										master=systemService.getUser(customerList.get(0).getUserId());
									}else{
										master=eventService.getMaster(key,customerList);
									}
								}else{
									LOGGER.error(subject+"按产品线分配邮件负责人-该产品线"+lineId+"国家"+key+"还未分配具体负责人！");
									master = getMaster(fromEmail, subject,key, comment);
								}
							}else{
								LOGGER.error(subject+"按产品线分配邮件负责人产品名"+productName+"未分到具体产品线！");
								master = getMaster(fromEmail, subject,key, comment);
							}
						}else{
							LOGGER.info(subject+"按产品线分配邮件负责人未找到相应订单号！");
							master = getMaster(fromEmail, subject,key, comment);
						}
					}
				}catch(Exception e){
					LOGGER.error(subject+"按产品线分配邮件负责人异常", e);
					master = getMaster(fromEmail, subject,key, comment);
				}
			}
			//前两步都没有找到最近联系人,直接按国家分发给客服
			if(master == null){
				master = getMaster(fromEmail, subject, key, comment);
			}
			customEmail.setMasterBy(master);
			customEmail.setCreateBy(master);
			
			
			AutoReply autoReply = new AutoReply();
			autoReply.setType("1");
			autoReply.setCreateBy(master);
			autoReply = this.autoReplyService.findByUser(autoReply);
			//转发目标人已经离职
			if (autoReply !=null && (autoReply.getForwardTo() != null) && "1".equals(autoReply.getForwardTo().getDelFlag())) {
				autoReply.setUsedForward("0");	//停止
				autoReplyService.save(autoReply);
			}
			if ((autoReply != null) && ("1".equals(autoReply.getUsedForward()))
					&& (autoReply.getForwardTo() != null) && "0".equals(autoReply.getForwardTo().getDelFlag())) {
				customEmail.setMasterBy(autoReply.getForwardTo());
				customEmail.setTransmit(master.getName() + "  auto forwardTo "
						+ customEmail.getMasterBy().getName());
			}
			if (fromEmail.startsWith("do-not-reply@amazon")||"non-rispondere@amazon.it".equals(fromEmail)) {
				AmazonWSConfig country = AmazonWSConfig.get(key);
				if ((country != null)
						&& (subject.startsWith(country.getReturnGoodsSubj()))) {
					customEmail.setState("4");
					/*Set<String> temps = CustomEmailController
							.getOrders(subject);
					if (temps.size() == 1) {
						AmazonOrder order = this.amazonOrderService
								.findByLazy((String) temps.iterator().next());
						if (order != null) {
							float price = findPrice(
									customEmail.getReceiveContent(),
									country.getPricePattern());
							//如果在ERP上退款则不处理邮件了
							if(!amazonRefundService.hasRefundRecord(order.getAmazonOrderId())){
								if (order.getOrderTotal()!=null && price == order.getOrderTotal().floatValue()) {
									autoReturnGoodsEmail(order.getBuyerEmail(),
											customEmail, key);
									Thread.sleep(3000);
									continue;
								}else{
									LOGGER.warn(order.getAmazonOrderId()+":邮件价"+price+"订单价:"+order.getOrderTotal());
								}
							}else{
								customEmail.setState("4");
							}
						}
					}*/
				}
			}

			String serverId = Global.getConfig("server.id");
			if("1".equals(serverId)&&"support@inateck.com".equals(fromEmail)){
				if ((autoReply != null) && ("1".equals(autoReply.getUsed()))
						&& cache.getIfPresent(fromEmail) == null) {
					Set<String> no = cache1.getIfPresent("no");
					if (cache1.size() == 0L|| no==null) {
						cache1.put("no", this.customEmailService.findNoAutoReply());
						no = cache1.getIfPresent("no");
					}
					String temp = fromEmail.toLowerCase().replace("-","").replace("_","");
					if (!no.contains(fromEmail)
							&& !temp.contains("notreply")&&!temp.contains("noreply")&&!"PostMaster@inateck.com".equals(fromEmail)) {
						if(StringUtils.isNotBlank(autoReply.getContent())){
							if (autoReply.getSubject() == null
									|| autoReply.getSubject().length() == 0) {
								autoReply.setSubject(isJp?"自動応答:":"AutoReply:"+ customEmail.getSubject());
							}
							noteCustom(autoReply, fromEmail, mailManager);
							Thread.sleep(3000);
							cache.put(fromEmail, 1);
							customEmail.setRemarks("1");
						}
					}
				}
			}
			
			try{
				//michael 2015-10-16 start   问题不根据客人邮箱号继承
//				String email = customEmail.getRevertEmail();
//				if(email!=null&&!email.toLowerCase().contains("notreply")&&!email.toLowerCase().contains("noreply")&&!"PostMaster@inateck.com".equals(email)){
//					CustomEmail customTemp= customEmailService.getSingleByEmail(email);
//					if(customTemp!=null){
//						//如果该客户以前有过往来邮件，就把里面的信息问题信息插入
//						customEmail.setCountry(customTemp.getCountry());
//						customEmail.setProductName(customTemp.getProductName());
//						customEmail.setProblemType(customTemp.getProblemType());
//						customEmail.setProblem(customTemp.getProblem());
//						customEmail.setOrderNos(customTemp.getOrderNos());
//					}
//				}
				//michael 2015-10-16 end
				this.customEmailService.save(customEmail);
                if(!userIdSet.contains(customEmail.getMasterBy().getId())){
                	customEmailBakManager.transmitEmail(message,customEmail.getMasterBy().getEmail());
                }
				//保存售后邮件记录
				if (comment != null) {
					try {
						customerFilterService.saveComment(comment);
					} catch (Exception e) {
						LOGGER.error(customEmail.getRevertEmail()+"关联售后邮件存储错误"+e.getMessage());
					}
				}
				
			}catch(Exception e){
				LOGGER.error(customEmail.getSubject()+"存储错误"+e.getMessage());
			}
			delRepeat.add(messageId);
		}
	}

	public static Set<String> getOrders(String input){
		Pattern orderPattern=Pattern.compile("\\d{3}-\\d{7}-\\d{7}");
		Set<String> rs = Sets.newHashSet();
		if(StringUtils.isNotEmpty(input)){
			Matcher matcher = orderPattern.matcher(input);
			while(matcher.find()){
				rs.add(matcher.group());
			}
		}
		return rs;
	}  
	
	private void autoReturnGoodsEmail(final String toEmail,
			final CustomEmail email, final String key) {
		new Thread() {
			public void run() {
				String mail = toEmail;
				if(toEmail.indexOf(",")>0){
					mail = toEmail.substring(0,toEmail.indexOf(","));
				}
				String content = SendEmailController.getTemplate(mail,"replyReturnGoods",
						"_" + key);
				content = content
						+ "<br/>------------------ Original ------------------<br/>"
						+ email.getReceiveContent();
				final SendEmail sendEmail = new SendEmail("RE:" + email.getSubject(),
						content, toEmail, "1");

				final MailInfo mailInfo = sendEmail.getMailInfo();
				if (sendEmail.getCreateBy() == null) {
					sendEmail.setCreateBy(email.getMasterBy());
				}
				if (customEmailBakManager.send(mailInfo)) {
					sendEmail.setSentDate(new Date());
					sendEmail.setSendFlag("1");
					if (email.getEndDate() == null) {
						email.setEndDate(new Date());
					}
					sendEmail.setId(IdGen.uuid());
					email.setState("2");
					email.setUpdateDate(new Date());
					email.setRemarks("2");
					sendEmail.setCustomEmail(email);
					email.setSendEmails(Lists
							.newArrayList(new SendEmail[] { sendEmail }));
				}
				try{
					customEmailService.save(email);
				}catch(Exception e){
					LOGGER.error(email.getSubject()+"存储错误"+e.getMessage());
				}
			};
		}.start();
	}

	private void noteCustom(final AutoReply autoReply, final String email,
			final MailManager mailManager) {
		new Thread() {
			public void run() {
				try {
					MailInfo mailInfo = new MailInfo(email,
							autoReply.getSubject(), new Date());
					mailInfo.setContent(HtmlUtils.htmlUnescape(autoReply
							.getContent()));
					boolean rs = false;
					if (email.contains("@marketplace.amazon"))
						rs = CustomEmailMonitor.this.customEmailBakManager
								.send(mailInfo);
					else {
						rs = mailManager.send(mailInfo);
					}
					if (!rs)
						CustomEmailMonitor.LOGGER.error("auto reply error:"
								+ email);
				} catch (Exception e) {
					CustomEmailMonitor.LOGGER.error("auto reply error:" + email
							+ ":" + e.getMessage(),e);
				}
			}
		}.start();
	}

	public static Map<String, String> getMasterUserCache(
			CustomEmailService customEmailService, String userId) {
		initMasterUsers(customEmailService, false);
		Map<String, String> rs = Maps.newHashMap(customService);

		for (Entry<String, List<User>> entry : cacheMap.entrySet()) {
			if (entry.getValue().size() != 1
					|| !entry.getValue().get(0).getId().equals(userId))
				continue;
			rs.remove(entry.getKey());
		}
		rs.remove("other.");
		return rs;
	}

	public static List<User> getOtherMaster(
			CustomEmailService customEmailService) {
		initMasterUsers(customEmailService, false);
		return cacheMap.get("other.");
	}

	public static Set<User> getAllMaster(CustomEmailService customEmailService) {
		initMasterUsers(customEmailService, false);
		Set<User> rs = Sets.newHashSet();
		for (List<User> users : cacheMap.values()) {
			rs.addAll(users);
		}
		return rs;
	}

	public static synchronized void initMasterUsers(
			CustomEmailService customEmailService, boolean forced) {
		if ((masterUserCache.size() == 0L)
				|| (masterUserCache.size() != customService.size()) || (forced)) {
			List<Role> roles = customEmailService.findCustomMasterRole();
			if (roles.size() == 0) {
				throw new RuntimeException("无初始化客服!");
			}
			customService = Maps.newConcurrentMap();
			masterUserCache.cleanUp();
			for (Role role : roles) {
				List<User> users = role.getUserList();
				List<Menu> menuList=role.getMenuList();
				if (users.size() > 0) {
					for (Menu menu : menuList) {
						String permission=menu.getPermission();
						if(StringUtils.isNotBlank(permission)&&permission.contains("custom:service:")){
							customService.put(permission.split(":")[2], role.getName());
							List<User> tempList=masterUserCache.getIfPresent(permission.split(":")[2]);
							if(tempList==null||tempList.size()==0){
								masterUserCache.put(permission.split(":")[2], users);
							}else{
								for (User newUser :users) {
									boolean flag=true;
									for (User user: tempList) {
										if(user.getId().equals(newUser.getId())){
											flag=false;
											break;
										}
									}
									if(flag){
										tempList.add(newUser);
									}
								}
							}
						}
					}	
				}
			}
			cacheMap.clear();
			cacheMap.putAll(masterUserCache.asMap());
		}
	}

	public static User getMaster(String key,
			CustomEmailService customEmailService) {
		User master = null;
		initMasterUsers(customEmailService, false);
		List<User> users = cacheMap.get(key);
		if (users == null) {
			users = cacheMap.get("com");
		}
		if ((users != null) && (users.size() > 0)) {
			master = (User) users.remove(0);
			users.add(master);
		} else {
			throw new RuntimeException("无初始化美国客服!");
		}
		return master;
	}

	/**
	 * 根据发件人邮箱自动分配负责人
	 * @param fromEmail
	 * @param subject
	 * @param key
	 * @param comment
	 * @return
	 */
	private User getMaster(String fromEmail, String subject,String key, AmazonComment comment) {
		initMasterUsers(this.customEmailService, false);
		User master = null;
		if ("com".equals(key)) {
			String temp = subject.substring(subject.lastIndexOf(".") + 1);
			if (cacheMap.keySet().contains(temp)) {
				key = temp;
			}
		}
		List<User> users = cacheMap.get(key);
		if (users == null) {
			users = cacheMap.get("com");
		}
		if ((users != null) && (users.size() > 0)) {
			master = (User) users.remove(0);
			users.add(master);
		} else {
			throw new RuntimeException("无初始化美国客服!");
		}
		return master;
	}

	/**
	 * 根据发件人邮箱找到最近联系人作为负责人
	 * @param fromEmail
	 * @param subject
	 * @param key
	 * @param comment
	 * @return
	 */
	private User getMasterByEmail(String fromEmail, String subject,String key, AmazonComment comment) {
		User master = null;
		if (!match(fromEmail)) {
			if(subject!=null&&!subject.startsWith("Offline message sent by")){
				master = this.customEmailService.findMaster(fromEmail);
			}
			if (master != null) {
				Set<Role> roles = master.getRoleList();
				if (roles != null) {
					int flag = 0;
					for (Role role : roles) {
						List<Menu> menuList=role.getMenuList();
						for (Menu menu : menuList) {
							String permission=menu.getPermission();
							if(StringUtils.isNotBlank(permission)&&permission.contains("custom:service:")){
								flag = 1;
								break;
							}
						}	
					}
					if (flag == 0)
						master = null;
				} else {
					master = null;
				}
			}
		}
		
	/*	if (master == null && comment != null && "0".equals(comment.getTask().getCreateBy().getDelFlag())) {
			master = comment.getTask().getCreateBy();
		}*/
		return master;
	}

	private boolean match(String email) {
		try {
			for (String rule : commomEmails)
				if (Pattern.matches(rule, email))
					return true;
		} catch (Exception localException) {
		}
		return false;
	}

	private float findPrice(String content, Pattern pattern) {
		Matcher matcher = pattern.matcher(content);
		float rs = 0.0F;
		if (matcher.find()) {
			String num = matcher.group();
			num = num.replaceAll("\\$|EUR|￥|£|CDN\\$", "").trim();
			num = num.replace(",", ".");
			try {
				rs = Float.parseFloat(num);
			} catch (NumberFormatException localNumberFormatException) {
			}
		}
		return rs;
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
	
	 public static String getMail(String content) throws Exception{  
        Matcher m=emailPattern.matcher(content);  
        while(m.find())  {  
           return m.group();  
        }  
        return "";
	 }  
	 
	 
	 public void updateEmail(){
		 Map<String, String> emails =  customEmailService.findWebSiteEmailMap(); 
		 for (Entry<String, String> entry : emails.entrySet()) {
			try {
				String email = getMail(entry.getValue());
				customEmailService.updateEmail(entry.getKey(), email);
			} catch (Exception e) {
				LOGGER.error("更新Email异常", e);
			}
		 }
	 } 
	
	 
	 public void matchAmazonEmail(){
		 List<CustomEmail>  emailList=customEmailService.findBeforeEmail();
		 if(emailList!=null&&emailList.size()>0){
			 for (CustomEmail customEmail : emailList) {
				 try{
					 if(!customEmail.getRevertEmail().contains("marketplace.amazon.")&&!customEmail.getRevertEmail().contains("@amazon")&&!customEmail.getRevertEmail().contains("noreply")&&!customEmail.getRevertEmail().contains("notreply")&&!customEmail.getRevertEmail().contains("inateck.com")){//个人邮箱
						 Set<String> ordersStr = getOrders(customEmail.getSubject());
						 ordersStr.addAll(getOrders(customEmail.getReceiveContent()));
						 List<AmazonOrder> orders = Lists.newArrayList();
						 for (String ordStr : ordersStr) {
								AmazonOrder order = amazonOrderService.findByEg(ordStr);
								if(order!=null){
									orders.add(order);
								}
						  }
						 if(orders!=null&&orders.size()>0){
							   for (AmazonOrder amazonOrder : orders) {
								   String customId = amazonOrderService.getCustomIdByOrderId(amazonOrder.getAmazonOrderId());
								   if(StringUtils.isNotBlank(customId)){
									   amazonCustomerService.updateEmail(customId,customEmail.getRevertEmail(),amazonOrder.getBuyerEmail());
								   }else{
									   LOGGER.error(customEmail.getRevertEmail()+"="+amazonOrder.getAmazonOrderId()+"的CustomId为空");
									   amazonCustomerService.updateEmail2(customEmail.getRevertEmail(),amazonOrder.getBuyerEmail());
								   }
							   }
						 }
					 }
				}catch(Exception e){
					LOGGER.error(customEmail.getRevertEmail()+"个人邮箱匹配亚马逊邮箱错误",e.getMessage());
				}
			 }
		 }
		LOGGER.info("邮箱容量start==");
		
		List<AmazonAccountConfig> emailLists=amazonAccountConfigService.findEmail();
		customEmailManager=new CustomEmailManager[emailList.size()];
		for (int i=0;i<emailLists.size();i++) {
			try {
				customEmailManager[i]=new CustomEmailManager();
				AmazonAccountConfig cfg=emailLists.get(i);
				MailManagerInfo  mailInfo=customEmailManager[i].setCustomEmailManager(cfg.getEmailType(), cfg.getCustomerEmail(),cfg.getCustomerEmailPassword());
				customEmailManager[i].setManagerInfo(mailInfo);
				customEmailManager[i].deleteEmailToInbox();
			} catch (Exception e) {
				LOGGER.error(customEmailManager[i].getManagerInfo().getUserName()
						+ ":" + e.getMessage(), e);
			}
		}
		
		/*try {
			 customEmailManager.deleteEmailToInbox();
		} catch (Exception e) {
			LOGGER.error("邮箱容量",e.getMessage());
		}*/
		LOGGER.info("邮箱容量end==");
	 }
	 
	 public static void main(String[] args) {
		try {
				System.out.println(getMail("Name: Mindy Hay"+
					"Email: mhay.xxx@setcreative.com "+
					"Telephone: 360-281-9131"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}