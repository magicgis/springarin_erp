package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.customer.AmazonCommentDao;
import com.springrain.erp.modules.amazoninfo.dao.customer.AmazonCustomFilterDao;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonComment;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonCustomFilter;
import com.springrain.erp.modules.custom.service.CustomEmailService;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

/**
 * 亚马逊客户售后邮件Service
 */
@Component
@Transactional(readOnly = true)
public class AmazonCustomerFilterService extends BaseService {

	@Autowired
	private AmazonCustomFilterDao customerFilterDao;
	
	@Autowired
	private AmazonCommentDao commentDao;

	@Autowired
	private CustomEmailService customEmailService;
	
	@Autowired
	private MailManager mailManager;
	
	public AmazonCustomFilter get(Integer id) {
		return customerFilterDao.get(id);
	}
	
	public AmazonComment getComment(Integer id) {
		return commentDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public  void save(AmazonCustomFilter customFilter) {
		customerFilterDao.clear();
		customerFilterDao.save(customFilter);
	}

	@Transactional(readOnly = false)
	public void saveComment(AmazonComment comment) {
		commentDao.clear();
		commentDao.save(comment);
	}

	@Transactional(readOnly = false)
	public void saveComments(List<AmazonComment> commentList) {
		commentDao.save(commentList);
	}
	
	//根据邮箱查找发送记录
	public AmazonComment getCommentByEmailOrCustomerId(String email, String customerId) {
		DetachedCriteria dc = commentDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(email)) {
			dc.add(Restrictions.eq("sendEmail", email));
		}
		if (StringUtils.isNotEmpty(customerId)) {
			dc.createAlias("customer", "customer");
			dc.add(Restrictions.eq("customer.customerId", customerId));
		}
		dc.add(Restrictions.eq("sendFlag", "1"));
		dc.addOrder(Order.desc("sentDate"));
		List<AmazonComment> list = commentDao.find(dc);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	public Page<AmazonCustomFilter> find(Page<AmazonCustomFilter> page, AmazonCustomFilter amazonCustomerFilter, String aboutMe) {
		DetachedCriteria dc = customerFilterDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(amazonCustomerFilter.getCountry())){
			dc.add(Restrictions.eq("country", amazonCustomerFilter.getCountry()));
		}
		if(amazonCustomerFilter.getStartDate() != null){
			dc.add(Restrictions.ge("createDate", amazonCustomerFilter.getStartDate()));
		}
		if(amazonCustomerFilter.getEndDate() != null){
			dc.add(Restrictions.le("createDate", amazonCustomerFilter.getEndDate()));
		}
		if(StringUtils.isNotEmpty(amazonCustomerFilter.getState())){
			dc.add(Restrictions.eq("state", amazonCustomerFilter.getState()));
		} else {
			dc.add(Restrictions.ne("state", "3"));
		}
		if (StringUtils.isNotEmpty(aboutMe) && "1".equals(aboutMe)) {
			dc.add(Restrictions.eq("createBy", UserUtils.getUser()));
		}
		if (StringUtils.isNotEmpty(amazonCustomerFilter.getTaskType())) {
			dc.add(Restrictions.eq("taskType", amazonCustomerFilter.getTaskType()));
		}
		if (StringUtils.isNotEmpty(amazonCustomerFilter.getAuditState())) {
			dc.add(Restrictions.eq("auditState", amazonCustomerFilter.getAuditState()));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		return customerFilterDao.find(page, dc);
	}

	/**
	 * 
	 * @param page
	 * @param amazonComment
	 * @param isReply	客户已回复
	 * @param isReview	客户已追加评论
	 * @return
	 */
	public Page<AmazonComment> findSendList(Page<AmazonComment> page, AmazonComment amazonComment, 
			String isReply, String isReview, List<String> asinList) {
		DetachedCriteria dc = commentDao.createDetachedCriteria();
		dc.createAlias("task", "task");
		if(amazonComment.getTask() != null && StringUtils.isNotEmpty(amazonComment.getTask().getCountry())){
			dc.add(Restrictions.eq("task.country", amazonComment.getTask().getCountry()));
		}
		boolean dateFlag = true;	//是否进行时间条件过滤
		if(amazonComment.getTask() != null && amazonComment.getTask().getId() != null){
			dateFlag = false;
			dc.add(Restrictions.eq("task.id", amazonComment.getTask().getId()));
		}
		dc.createAlias("customer", "customer");
		if(amazonComment.getCustomer() != null && StringUtils.isNotEmpty(amazonComment.getCustomer().getCustomerId())){
			dateFlag = false;
			dc.add(Restrictions.eq("customer.customerId", amazonComment.getCustomer().getCustomerId()));
		}
		if (StringUtils.isNotEmpty(amazonComment.getSendFlag())) {
			dc.add(Restrictions.eq("sendFlag", amazonComment.getSendFlag()));
		} else {
			dc.add(Restrictions.ne("sendFlag", "2"));
		}
		boolean reviewCommentAlias = false;
		if (StringUtils.isNotEmpty(amazonComment.getStar())) {
			dc.createAlias("reviewComment", "reviewComment");
			reviewCommentAlias = true;
			if ("0".equals(amazonComment.getStar())) {	//差评
				dc.add(Restrictions.in("reviewComment.star", Lists.newArrayList("1","2","3")));
			} else {
				dc.add(Restrictions.in("reviewComment.star", Lists.newArrayList("4","5")));
			}
		}
		//产品评论情况
		if(asinList.size() > 0){
			if (!reviewCommentAlias) {
				dc.createAlias("reviewComment", "reviewComment");
			}
			dateFlag = false;
			dc.add(Restrictions.in("reviewComment.asin", asinList));
		}
		if (dateFlag) {
			if (StringUtils.isNotEmpty(amazonComment.getSendFlag()) && "1".equals(amazonComment.getSendFlag())) {
				if(amazonComment.getCreateDate() != null){
					dc.add(Restrictions.ge("sentDate", amazonComment.getCreateDate()));
				}
				if(amazonComment.getSentDate() != null){
					dc.add(Restrictions.le("sentDate", amazonComment.getSentDate()));
				}
			} else {
				if(amazonComment.getCreateDate() != null){
					dc.add(Restrictions.ge("createDate", amazonComment.getCreateDate()));
				}
				if(amazonComment.getSentDate() != null){
					dc.add(Restrictions.le("createDate", DateUtils.addDays(amazonComment.getSentDate(), 1)));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(isReply) && "1".equals(isReply)) {
			dc.add(Restrictions.isNotNull("customEmail"));
		}
		if (StringUtils.isNotEmpty(isReview) && "1".equals(isReview)) {
			dc.add(Restrictions.isNotNull("reviewComment"));
		} else if (StringUtils.isNotEmpty(isReview) && "2".equals(isReview)) {
			dc.add(Restrictions.isNull("reviewComment"));
		}

		//购买过的产品
		if(amazonComment.getTask() != null && StringUtils.isNotEmpty(amazonComment.getTask().getPn1())){
			String sql = " SELECT t.`customer_id` FROM `amazoninfo_buy_comment` t,`amazoninfo_comment` c " +
					" WHERE t.`customer_id`=c.`customer_id` AND t.`type`='1' AND t.`type_date`>c.`sent_date` AND t.`product_name` LIKE '%"+amazonComment.getTask().getPn1()+"%' ";
			List<String> list = commentDao.findBySql(sql);
			if (list.size() > 0) {
				dc.add(Restrictions.in("customer.customerId", list));
			} else {
				return null;
			}
		}
		return commentDao.find(page, dc);
	}
    
	/**
	 * 查询未完成的任务,根据任务条件筛选出客户,添加到待发送中
	 */
	@Transactional(readOnly = false)
	public void processingTasks() {
		DetachedCriteria dc = customerFilterDao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.eq("state", "0"), Restrictions.eq("state", "1")));	//未开始和进行中的
		dc.add(Restrictions.eq("auditState", "1"));	//审批通过的
		dc.add(Restrictions.eq("delFlag", "0"));
		List<AmazonCustomFilter> list = customerFilterDao.find(dc);
		for (AmazonCustomFilter amazonCustomFilter : list) {
			//已经到截止时间并且状态为1的忽略掉
			if ("1".equals(amazonCustomFilter.getState()) && amazonCustomFilter.getEndDate().before(DateUtils.addDays(new Date(), -1))) {
				continue;
			}
			int count = queryCount(amazonCustomFilter);
			if (count == amazonCustomFilter.getTotalCustomer().intValue()) { //客户总数没有变化
				if (!"0".equals(amazonCustomFilter.getState())) {
					continue;
				}
			} else {
				amazonCustomFilter.setTotalCustomer(count); //更新客户总数
			}
			Integer taskId = amazonCustomFilter.getId();
			List<String> customerIds = getCcommentIdsByTask(taskId);	//查询已经添加了任务的客户,避免重复添加
			Integer templateId = amazonCustomFilter.getTemplate().getId();
			List<Object[]> customerList = query(true, amazonCustomFilter);
			for (Object[] obj : customerList) {
				if (obj[0] != null && !customerIds.contains(obj[0].toString())) {
					String customerId = obj[0].toString();
					String sql = "INSERT INTO `amazoninfo_comment`(create_date,customer_id,task_id,send_flag,template_id)"+
							" VALUES(NOW(),'"+customerId+"','"+taskId+"','0','"+templateId+"') ON DUPLICATE KEY UPDATE customer_id=VALUES(customer_id)";
					customerFilterDao.updateBySql(sql, null);
				}
			}
			amazonCustomFilter.setState("1");
			this.save(amazonCustomFilter);
		}
	}
    
	/**
	 * 查询未完成的任务,根据任务条件筛选出客户,添加到待发送中
	 */
	public List<AmazonCustomFilter> findProcessingTasks() {
		DetachedCriteria dc = customerFilterDao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.eq("state", "0"), Restrictions.eq("state", "1")));	//未开始和进行中的
		dc.add(Restrictions.eq("auditState", "1"));	//审批通过的
		dc.add(Restrictions.eq("delFlag", "0"));
		return customerFilterDao.find(dc);
	}
    
	/**
	 * 查询未完成的任务,根据任务条件筛选出客户,添加到待发送中
	 */
	@Transactional(readOnly = false)
	public void processingSingleTasks(AmazonCustomFilter amazonCustomFilter) {
		if ("1".equals(amazonCustomFilter.getState()) && amazonCustomFilter.getEndDate().before(DateUtils.addDays(new Date(), -1))) {
			return;
		}
		//该方式统计总数太耗资源
		/*int count = queryCount(amazonCustomFilter);
		if (count == amazonCustomFilter.getTotalCustomer().intValue()) { //客户总数没有变化
			if (!"0".equals(amazonCustomFilter.getState())) {
				return;
			}
		} else {
			amazonCustomFilter.setTotalCustomer(count); //更新客户总数
		}*/
		Integer taskId = amazonCustomFilter.getId();
		Integer templateId = amazonCustomFilter.getTemplate().getId();
		List<Object[]> customerList = query(true, amazonCustomFilter, true);
		for (Object[] obj : customerList) {
			if (obj[0] != null) {
				String customerId = obj[0].toString();
				String sql = "INSERT INTO `amazoninfo_comment`(create_date,customer_id,task_id,send_flag,template_id)"+
						" VALUES(NOW(),'"+customerId+"','"+taskId+"','0','"+templateId+"') ON DUPLICATE KEY UPDATE customer_id=VALUES(customer_id)";
				customerFilterDao.updateBySql(sql, null);
			}
		}
		amazonCustomFilter.setState("1");
		this.save(amazonCustomFilter);
	}
	
	/**
	 * 查询指定任务已经添加了的客户id集合
	 */
	public List<String> getCcommentIdsByTask(Integer taskId) {
		List<String> rs = Lists.newArrayList();
		DetachedCriteria dc = commentDao.createDetachedCriteria();
		dc.createAlias("task", "task");
		dc.add(Restrictions.eq("task.id", taskId));
		List<AmazonComment> list = commentDao.find(dc);
		for (AmazonComment amazonComment : list) {
			rs.add(amazonComment.getCustomer().getCustomerId());
		}
		return rs;
	}
	
	/**
	 * 查询待发送邮件列表,指定数量限制(size小于等于0表示不限制)
	 */
	public List<AmazonComment> getSendEmailList(int size) {
		String sql = "SELECT DISTINCT t.`id` FROM `amazoninfo_comment` t   "+
				" JOIN `amazoninfo_buy_comment` b ON t.`customer_id`=b.`customer_id`  "+
				" JOIN amazoninfo_custom_filter f ON t.`task_id`=f.`id` "+
				" AND t.`send_flag`='0' AND b.`type_date`>=f.`start_date` AND b.`type_date` <= DATE_ADD(NOW(),INTERVAL -f.`send_delay` DAY) AND f.`state`='1' "+
				" GROUP BY t.`id` ORDER BY DATE_ADD(MIN(b.`create_date`),INTERVAL f.`send_delay` DAY) LIMIT  " +size;
		List<Integer> idList =  commentDao.findBySql(sql);
		Page<AmazonComment> page = new Page<AmazonComment>();
		page.setPageSize(size);	//一次最多处理邮件数
		page.setOrderBy(" createDate asc");
		DetachedCriteria dc = commentDao.createDetachedCriteria();
		if (idList != null && idList.size() > 0) {
			dc.add(Restrictions.in("id", idList));
			page = commentDao.find(page, dc);
		}
		return page.getList();
	}
	
	/**
	 * 查询最近一个月发送邮件的订单号集合
	 */
	public List<String> getSendOrderList(String taskType) {
		String sql = "SELECT t.`order_id` FROM amazoninfo_comment t WHERE t.`sent_date`>:p1 AND t.`send_flag`='1'";
		if (StringUtils.isNotEmpty(taskType)) {
			sql = "SELECT t.`order_id` FROM amazoninfo_comment t,`amazoninfo_custom_filter` f WHERE "+
					" t.`task_id`=f.`id` AND t.`sent_date`>:p1 AND t.`send_flag`='1' AND f.`task_type`='"+taskType+"'";
		}
		List<String> idList = commentDao.findBySql(sql, new Parameter(DateUtils.addMonths(new Date(), -1)));
		return idList;
	}
    
	/**
	 * 发送邮件之后,标记已完成的任务
	 * 未发送数量为0并且截止时间已到期
	 */
	@Transactional(readOnly = false)
	public void finishTasks() {
		try {
			String sql = "UPDATE `amazoninfo_custom_filter` t "+
					" SET t.`send_num`= (SELECT COUNT(*) FROM amazoninfo_comment c WHERE c.`send_flag`='1' AND t.`id`=c.`task_id`), "+
					" t.`not_send_num`= (SELECT COUNT(*) FROM amazoninfo_comment c WHERE c.`send_flag`='0' AND t.`id`=c.`task_id`) " +
					" WHERE t.`state` IN('0','1')";
			customerFilterDao.updateBySql(sql, null);
			sql = "UPDATE `amazoninfo_custom_filter` t SET t.`total_customer`=(t.`send_num`+t.`not_send_num`) WHERE t.`total_customer`<(t.`send_num`+t.`not_send_num`)";
			customerFilterDao.updateBySql(sql, null);
		} catch (Exception e) {
			WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "统计售后邮件发送数量异常");
			logger.error("统计售后邮件发送数量异常", e);
		}
		DetachedCriteria dc = customerFilterDao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.eq("state", "0"), Restrictions.eq("state", "1")));	//未开始和进行中的
		dc.add(Restrictions.eq("auditState", "1"));	//审批通过的
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.le("endDate", new Date()));	//截止时间小于当前时间的
		List<AmazonCustomFilter> list = customerFilterDao.find(dc);
		for (AmazonCustomFilter amazonCustomFilter : list) {
			if (getNotSendByTask(amazonCustomFilter.getId()) == 0) {
				amazonCustomFilter.setState("2");	//已完成标记
				this.save(amazonCustomFilter);
				try {
					WeixinSendMsgUtil.sendTextMsgToUser(amazonCustomFilter.getCreateBy().getLoginName(), 
							"你创建的编号为"+amazonCustomFilter.getId()+"的售后邮件任务已经完成,请知悉。");
				} catch (Exception e) {
					logger.error("售后邮件完成通知发送失败", e);
				}
			}
		}
	}
	
	/**
	 * 查询指定任务未发送数量
	 */
	public long getNotSendByTask(Integer taskId) {
		DetachedCriteria dc = commentDao.createDetachedCriteria();
		dc.add(Restrictions.eq("sendFlag", "0"));
		dc.createAlias("task", "task");
		dc.add(Restrictions.eq("task.id", taskId));
		return commentDao.count(dc);
	}
	
	
	//测试
	public static void main(String[] args) {
		// TODO TEST
		/*
		 *	说明:邮件模板规定只支持productName、asin两个变量以及模板主题支持orderId变量
		 *	1:processingTasks	处理任务,添加到待发送中
		 *  2：getSendEmailList	查询待发送邮件列表,指定数量限制,然后发送
		 *  3：finishTasks	发送之后监控任务是否已完成
		 *  4：考虑10个线程同时发送、根据编号末位分发(除10取余分组)，map<编号 list<记录>>
		 */
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		AmazonCustomerFilterService  customerFilterService= applicationContext.getBean(AmazonCustomerFilterService.class);
		List<AmazonCustomFilter> taskList = customerFilterService.findProcessingTasks();
		for (AmazonCustomFilter amazonCustomFilter : taskList) {
			if ("1".equals(amazonCustomFilter.getState()) && amazonCustomFilter.getEndDate().before(DateUtils.addDays(new Date(), -1))) {
				continue;
			}
			System.out.println(amazonCustomFilter.getId());
			customerFilterService.processingSingleTasks(amazonCustomFilter);
		}
//		AmazonComment comment = customerFilterService.getCommentByEmailOrCustomerId(null, "A11Z6Z4BJS7GTH");
//		System.out.println(comment.getId());

		//customerFilterService.processingTasks();	//处理任务,添加到待发送中
		/*
		int size = 2;	//测试数量
		List<AmazonComment> list = customerFilterService.getSendEmailList(size);
		//拿到待发送记录,取模板替换变量,然后发送邮件,修改记录状态
		for (AmazonComment comment : list) {
			AmazonCustomer customer = comment.getCustomer();
			if (customer == null || (StringUtils.isEmpty(customer.getAmzEmail()) && StringUtils.isEmpty(customer.getEmail()))) {
				comment.setSendFlag("2");	//无效的记录
				comment.setSentDate(new Date());
				comment.setSendSubject("客户不存在或客户邮箱为空");
				continue;
			}
			AmazonBuyComment buyComment = customer.getBuyComments().get(0);
			//过滤延迟发送时间
			if (buyComment.getCreateDate().after(DateUtils.addDays(new Date(), 0 - comment.getTask().getSendDelay()))) {
				continue;
			}
			CustomEmailTemplate template = comment.getTemplate();
			String content = template.getTemplateContent();
			if (content.contains("${productName}")) {
				String productName = comment.getTask().getPn1();
				if (StringUtils.isEmpty(productName)) {
					productName = comment.getTask().getPn2();
				}
				if (StringUtils.isEmpty(productName)) {
					productName = comment.getTask().getPn3();
				}
				//替换模板中的产品名称变量
				if (StringUtils.isNotEmpty(productName)) {
					content = content.replace("${productName}", productName);
				} else {
					content = content.replace("${productName}", "");
				}
			}
			String orderId = "";
			if (content.contains("${asin}")) {
				String asin = "";
				if (customer.getBuyComments().size() > 0) {
					asin = customer.getBuyComments().get(0).getAsin();
					orderId = customer.getBuyComments().get(0).getOrderId();
				}
				content = content.replace("${asin}", asin);
			}
			String subject = template.getTemplateSubject();
			if (StringUtils.isNotEmpty(subject) && subject.contains("${orderId}")) {
				subject = subject.replace("${orderId}", orderId);
			}
			String sendEmail = customer.getAmzEmail();
			if (StringUtils.isEmpty(sendEmail)) {
				sendEmail = customer.getEmail();
			}
			//发送邮件
			
			//发送成功记录信息
			comment.setSendFlag("1");	//已发送
			comment.setSentDate(new Date());
			comment.setSendSubject(subject);
			comment.setContent(content);
			comment.setSendEmail(sendEmail);
		}
		customerFilterService.finishTasks();	//标记已完成的任务
		*/
		//保存记录
		applicationContext.close();
	}

	@Transactional(readOnly = false)
	public void cancel(AmazonCustomFilter customFilter) {
		logger.info("取消发送:"+customFilter.getId());
		DetachedCriteria dc = commentDao.createDetachedCriteria();
		dc.createAlias("task", "task");
		dc.add(Restrictions.eq("task.id", customFilter.getId()));
		dc.add(Restrictions.eq("sendFlag", "0"));
		List<AmazonComment> list = commentDao.find(dc);
		for (AmazonComment amazonComment : list) {
			amazonComment.setSendFlag("2");
			amazonComment.setSentDate(new Date());
			amazonComment.setSendSubject("取消发送");
		}
		customFilter.setState("3");
		save(customFilter);
		saveComments(list);
		
	}

	@Transactional(readOnly = false)
	public void updateCommentBySql(AmazonComment comment) {
		String sql = "UPDATE amazoninfo_comment t SET t.reivew_id= (SELECT c.`id` FROM amazoninfo_review_comment c WHERE "+
				" c.`customer_id`=:p1 ORDER BY c.`create_date` DESC LIMIT 1) WHERE t.`id`=:p2";
		commentDao.updateBySql(sql, new Parameter(comment.getCustomer().getCustomerId(), comment.getId()));
	}
	
	public List<Object[]> query(boolean byExport, AmazonCustomFilter customFilter){
		return query(byExport, customFilter, false);
	}

    public List<Object[]> query(boolean byExport, AmazonCustomFilter customFilter, boolean insert){
    	String search = "";
    	String name = customFilter.getName();
    	if(StringUtils.isNotEmpty(name)){
    		search +=" and a.`name` LIKE '%"+name+"%'";
    	}
    	String email = customFilter.getEmail();
    	if(StringUtils.isNotEmpty(email)){
    		search +=" and (a.`email` LIKE '%"+email+"%' or a.`amz_email` LIKE '%"+email+"%')";
    	}
    	if (customFilter.getCustomer() != null) {
        	String customerId = customFilter.getCustomer().getCustomerId();
        	if(StringUtils.isNotEmpty(customerId)){
        		search +=" and a.`customer_id` LIKE '%"+customerId+"%'";
        	}
		}
    	String country = customFilter.getCountry();
    	if(StringUtils.isNotEmpty(country)){
    		search +=" and a.`country`='"+country+"'";
    	}
    	String buyTimes = customFilter.getBuyTimes();
    	if(StringUtils.isNotEmpty(buyTimes)){
    		if("1".equals(buyTimes)){
    			search +=" and a.`buy_times`=1";
    		}else{
    			search +=" and a.`buy_times`>1";
    		}
    	}
    	String returnQ = customFilter.getReturnFlag();
    	//退货标识增加退款过滤
    	if(StringUtils.isNotEmpty(returnQ)){
    		if ("1".equals(returnQ)) {	//退货或退款
    			search +=" and (a.`return_quantity` > 0 OR a.`refund_money` > 0)";
			} else {
				search +=" and a.`return_quantity` = 0 and a.`refund_money` = 0";
			}
    	}
    	String bigOrder = customFilter.getBigOrder();
    	if (StringUtils.isNotEmpty(bigOrder)) {
    		if("1".equals(bigOrder)){
    			search +=" and b.`quantity`>=10";
    		}else{
    			search +=" and b.`quantity`<10";
    		}
		}
    	//String search1 = "";
    	StringBuilder buf=new StringBuilder();
    	String productName1 = customFilter.getPn1();
    	String and = customFilter.getPnAnd();	//1:且  0：或
    	if(StringUtils.isNotEmpty(productName1)){
    		List<String> list = Lists.newArrayList(productName1.split(","));
    		int length = list.size();
    		buf.append(" and (");
			for (int i = 0; i < length; i++) {
				if (i == 0) {
					buf.append(" GROUP_CONCAT(DISTINCT b.`product_name`) LIKE '%"+list.get(i)+"%' ");
				} else {
					buf.append(("1".equals(and)?"and":"or")+" GROUP_CONCAT(DISTINCT b.`product_name`) LIKE '%"+list.get(i)+"%' ");
				}
			}
			buf.append(")");
    	}
    	
    	String pn1 = customFilter.getPn11();
    	if (StringUtils.isNotEmpty(customFilter.getPn22())) {
    		pn1 += "," + customFilter.getPn22();
		}
    	if (StringUtils.isNotEmpty(customFilter.getPn33())) {
    		pn1 += "," + customFilter.getPn33();
		}
    	and = customFilter.getPn1And();
    	if(StringUtils.isNotEmpty(pn1)){
    		List<String> list = Lists.newArrayList(pn1.split(","));
    		int length = list.size();
    		buf.append(" and (");
			for (int i = 0; i < length; i++) {
				if (i == 0) {
					buf.append(" GROUP_CONCAT(DISTINCT b.`product_name`) not LIKE '%"+list.get(i)+"%' ");
				} else {
					buf.append(("1".equals(and)?"and":"or")+" GROUP_CONCAT(DISTINCT b.`product_name`) not LIKE '%"+list.get(i)+"%' ");
				}
			}
			buf.append(")");
    	}
    	
    	String good = customFilter.getGood();
    	String error = customFilter.getError();
    	if("0".equals(good)&&"0".equals(error)){
    		buf.append("and ( GROUP_CONCAT(DISTINCT c.`star`) is null)");
    	}else{
	    	if(StringUtils.isNotEmpty(good)){
	    		if("1".equals(good)){
	    			buf.append("and ( GROUP_CONCAT(DISTINCT c.`star`)  LIKE '%4%' or  GROUP_CONCAT(DISTINCT c.`star`) LIKE '%5%' )");
	    		}else{
	    			buf.append("and (( GROUP_CONCAT(DISTINCT c.`star`)  not  LIKE '%4%' and GROUP_CONCAT(DISTINCT c.`star`) not LIKE '%5%' ) or GROUP_CONCAT(DISTINCT c.`star`) is null )");
	    		}
	    	}
	    	if(StringUtils.isNotEmpty(error)){
	    		if("1".equals(error)){
	    			buf.append("and ( GROUP_CONCAT(DISTINCT c.`star`)  LIKE '%1%' or  GROUP_CONCAT(DISTINCT c.`star`) LIKE '%2%'  or  GROUP_CONCAT(DISTINCT c.`star`)  LIKE '%3%' )");
	    		}else{
	    			buf.append("and (( GROUP_CONCAT(DISTINCT c.`star`) not  LIKE '%1%'  and  GROUP_CONCAT(DISTINCT c.`star`)  not  LIKE '%2%'  and  GROUP_CONCAT(DISTINCT c.`star`) not  LIKE '%3%' ) or GROUP_CONCAT(DISTINCT c.`star`) is null)");
	    		}
	    	}
    	}
    	String pl = customFilter.getPl();
    	if(StringUtils.isNotEmpty(pl)){
    		buf.append(" and  TRUNCATE(AVG(TIMESTAMPDIFF(DAY,a.`first_buy_date`,a.`last_buy_date`)/a.`buy_times`),0)<="+pl);
    	}
    	//String sql = "SELECT DISTINCT a.`customer_id`,a.`country`,a.`amz_email`,a.`email`,GROUP_CONCAT(DISTINCT b.`product_name` order by b.type_date ) FROM amazoninfo_customer a JOIN amazoninfo_buy_comment b ON a.`customer_id` = b.`customer_id` LEFT JOIN  amazoninfo_review_comment c ON a.`customer_id` = c.`customer_id`  WHERE  a.`last_buy_date` >:p1 AND a.`last_buy_date`<:p2  "+search+" GROUP BY a.`customer_id` having 1=1  "+search1+" ORDER BY a.`country`"+(byExport?"":"LIMIT 20");		
    	String sql = "SELECT DISTINCT a.`customer_id`,a.`country`,a.`amz_email`,a.`email`,GROUP_CONCAT(DISTINCT b.`product_name` order by b.type_date ) FROM amazoninfo_customer a JOIN amazoninfo_buy_comment b ON a.`customer_id` = b.`customer_id` LEFT JOIN  amazoninfo_review_comment c ON a.`customer_id` = c.`customer_id`  WHERE  b.`type_date` >:p1 AND b.`type_date`<:p2 AND a.`last_buy_date`>:p1 "+search+" GROUP BY a.`customer_id` having 1=1  "+buf.toString()+" ORDER BY a.`country`"+(byExport?"":"LIMIT 20");			
		Date staDate = customFilter.getStartDate();
		Date endDate = customFilter.getEndDate();
		if (!"0".equals(customFilter.getState()) && endDate.after(new Date())) {
			if ("4".equals(customFilter.getTaskType()) && staDate.before(DateUtils.addDays(new Date(), -40))) {
				staDate = DateUtils.addDays(new Date(), -40);
			} else if(!"4".equals(customFilter.getTaskType()) && staDate.before(DateUtils.addMonths(new Date(), -1))){
				staDate = DateUtils.addMonths(new Date(), -1);	//周期超过两个月的任务只处理最近一个月的数据，避免查询数据过多不能使用索引进行区间查询
			}
		}
		if (insert) {	//如果是插入到队列的话,只插入当天需要发送的数据
			endDate = DateUtils.addDays(new Date(), -customFilter.getSendDelay());
		}
    	return commentDao.findBySql(sql,new Parameter(staDate, DateUtils.addDays(endDate, 1)));
    }
    
    /**
     * 查询符号条件的客户数
     * @param customFilter
     * @return
     */
    public Integer queryCount(AmazonCustomFilter customFilter){
    	String search = "";
    	String name = customFilter.getName();
    	if(StringUtils.isNotEmpty(name)){
    		search +=" and a.`name` LIKE '%"+name+"%'";
    	}
    	String email = customFilter.getEmail();
    	if(StringUtils.isNotEmpty(email)){
    		search +=" and (a.`email` LIKE '%"+email+"%' or a.`amz_email` LIKE '%"+email+"%')";
    	}
    	if (customFilter.getCustomer() != null) {
        	String customerId = customFilter.getCustomer().getCustomerId();
        	if(StringUtils.isNotEmpty(customerId)){
        		search +=" and a.`customer_id` LIKE '%"+customerId+"%'";
        	}
		}
    	String country = customFilter.getCountry();
    	if(StringUtils.isNotEmpty(country)){
    		search +=" and a.`country`='"+country+"'";
    	}
    	String buyTimes = customFilter.getBuyTimes();
    	if(StringUtils.isNotEmpty(buyTimes)){
    		if("1".equals(buyTimes)){
    			search +=" and a.`buy_times`=1";
    		}else{
    			search +=" and a.`buy_times`>1";
    		}
    	}
    	String returnQ = customFilter.getReturnFlag();
    	//退货标识增加退款过滤
    	if(StringUtils.isNotEmpty(returnQ)){
    		if ("1".equals(returnQ)) {	//退货或退款
    			search +=" and (a.`return_quantity` > 0 OR a.`refund_money` > 0)";
			} else {
				search +=" and a.`return_quantity` = 0 and a.`refund_money` = 0";
			}
    	}
    	String bigOrder = customFilter.getBigOrder();
    	if (StringUtils.isNotEmpty(bigOrder)) {
    		if("1".equals(bigOrder)){
    			search +=" and b.`quantity`>=10";
    		}else{
    			search +=" and b.`quantity`<10";
    		}
		}
    	//String search1 = "";
    	StringBuilder buf=new StringBuilder();
    	String productName1 = customFilter.getPn1();
    	String and = customFilter.getPnAnd();	//1:且  0：或
    	if(StringUtils.isNotEmpty(productName1)){
    		List<String> list = Lists.newArrayList(productName1.split(","));
    		int length = list.size();
    		buf.append(" and (");
			for (int i = 0; i < length; i++) {
				if (i == 0) {
					buf.append(" GROUP_CONCAT(DISTINCT b.`product_name`) LIKE '%"+list.get(i)+"%' ");
				} else {
					buf.append(("1".equals(and)?"and":"or")+" GROUP_CONCAT(DISTINCT b.`product_name`) LIKE '%"+list.get(i)+"%' ");
				}
			}
			buf.append(")");
    	}
    	
    	String pn1 = customFilter.getPn11();
    	if (StringUtils.isNotEmpty(customFilter.getPn22())) {
    		pn1 += "," + customFilter.getPn22();
		}
    	if (StringUtils.isNotEmpty(customFilter.getPn33())) {
    		pn1 += "," + customFilter.getPn33();
		}
    	and = customFilter.getPn1And();
    	if(StringUtils.isNotEmpty(pn1)){
    		List<String> list = Lists.newArrayList(pn1.split(","));
    		int length = list.size();
    		buf.append( " and (" );
			for (int i = 0; i < length; i++) {
				if (i == 0) {
					buf.append( " GROUP_CONCAT(DISTINCT b.`product_name`) not LIKE '%"+list.get(i)+"%' ");
				} else {
					buf.append(("1".equals(and)?"and":"or")+" GROUP_CONCAT(DISTINCT b.`product_name`) not LIKE '%"+list.get(i)+"%' ");
				}
			}
			buf.append( ")" );
    	}
    	
    	String good = customFilter.getGood();
    	String error = customFilter.getError();
    	if("0".equals(good)&&"0".equals(error)){
    		buf.append("and ( GROUP_CONCAT(DISTINCT c.`star`) is null)");
    	}else{
	    	if(StringUtils.isNotEmpty(good)){
	    		if("1".equals(good)){
	    			buf.append( "and ( GROUP_CONCAT(DISTINCT c.`star`)  LIKE '%4%' or  GROUP_CONCAT(DISTINCT c.`star`) LIKE '%5%' )");
	    		}else{
	    			buf.append( "and (( GROUP_CONCAT(DISTINCT c.`star`)  not  LIKE '%4%' and GROUP_CONCAT(DISTINCT c.`star`) not LIKE '%5%' ) or GROUP_CONCAT(DISTINCT c.`star`) is null )");
	    		}
	    	}
	    	if(StringUtils.isNotEmpty(error)){
	    		if("1".equals(error)){
	    			buf.append( "and ( GROUP_CONCAT(DISTINCT c.`star`)  LIKE '%1%' or  GROUP_CONCAT(DISTINCT c.`star`) LIKE '%2%'  or  GROUP_CONCAT(DISTINCT c.`star`)  LIKE '%3%' )");
	    		}else{
	    			buf.append( "and (( GROUP_CONCAT(DISTINCT c.`star`) not  LIKE '%1%'  and  GROUP_CONCAT(DISTINCT c.`star`)  not  LIKE '%2%'  and  GROUP_CONCAT(DISTINCT c.`star`) not  LIKE '%3%' ) or GROUP_CONCAT(DISTINCT c.`star`) is null)");
	    		}
	    	}
    	}
    	
    	String pl = customFilter.getPl();
    	if(StringUtils.isNotEmpty(pl)){
    		buf.append(" and  TRUNCATE(AVG(TIMESTAMPDIFF(DAY,a.`first_buy_date`,a.`last_buy_date`)/a.`buy_times`),0)<="+pl);
    	}
    	//String sql = "select count(1) from (SELECT DISTINCT a.`customer_id`,a.`country`,a.`amz_email`,a.`email` FROM amazoninfo_customer a JOIN amazoninfo_buy_comment b ON a.`customer_id` = b.`customer_id` LEFT JOIN  amazoninfo_review_comment c ON a.`customer_id` = c.`customer_id`  WHERE  a.`last_buy_date` >:p1 AND a.`last_buy_date`<:p2  "+search+" GROUP BY a.`customer_id` having 1=1  "+search1+" ORDER BY a.`country`) as cc";
    	String sql = "select count(1) from (SELECT DISTINCT a.`customer_id`,a.`country`,a.`amz_email`,a.`email` FROM amazoninfo_customer a JOIN amazoninfo_buy_comment b ON a.`customer_id` = b.`customer_id` LEFT JOIN  amazoninfo_review_comment c ON a.`customer_id` = c.`customer_id`  WHERE  b.`type_date` >:p1 AND b.`type_date`<:p2 AND a.`last_buy_date`>:p1 "+search+" GROUP BY a.`customer_id` having 1=1  "+buf.toString()+" ORDER BY a.`country`) as cc";			
    	List<Object> rs = commentDao.findBySql(sql,new Parameter(customFilter.getStartDate(), DateUtils.addDays(customFilter.getEndDate(),1)));
		return ((BigInteger)rs.get(0)).intValue();
    }
	
	/**
	 * 根据国家查询一个asin和对应的产品
	 */
	public List<String> getAsinByCountry(String country) {
		List<String> rs = Lists.newArrayList();
		String sql = "SELECT t.`ASIN`,t.`product_name` FROM `amazoninfo_posts_detail` t "+
				" WHERE t.`product_name` IS NOT NULL AND t.`country`=:p1 AND t.`star`>0 ORDER BY t.`query_time` DESC LIMIT 1";
		List<Object[]> list = commentDao.findBySql(sql, new Parameter(country));
		for (Object[] obj : list) {
			rs.add(obj[0].toString());
			rs.add(obj[1].toString());
		}
		sql = "SELECT t.`review_asin` FROM `amazoninfo_review_comment` t WHERE t.`country`=:p1 AND t.`star`='5' ORDER BY t.`create_date` DESC LIMIT 1";
		List<Object> reviewAsinList = commentDao.findBySql(sql, new Parameter(country));
		for (Object obj : reviewAsinList) {
			rs.add(obj.toString());
		}
		return rs;
	}
	
	/**
	 * 查询最近7天联系过的客户ID
	 */
	public List<String> getRecentContact() {
		List<String> rs = Lists.newArrayList();
		String sql = "SELECT DISTINCT t.`customer_id` FROM `amazoninfo_comment` t WHERE t.`send_flag`='1' AND TO_DAYS(NOW()) - TO_DAYS(t.`sent_date`)<=7";
		List<Object> list = commentDao.findBySql(sql);
		for (Object obj : list) {
			if (obj == null) {
				continue;
			}
			rs.add(obj.toString());
		}
		return rs;
	}
	
	public boolean isExistOrder(String orderId) {
		if (StringUtils.isEmpty(orderId)) {
			return false;
		}
		String sql="SELECT COUNT(1) FROM amazoninfo_comment t WHERE t.`order_id`=:p1";
		int num = ((BigInteger)commentDao.findBySql(sql, new Parameter(orderId)).get(0)).intValue();
		if (num > 0) {
			return true;
		}
		return false;
	}
	
	public Map<String, String> getAsinProductMap() {
		Map<String, String> rs = Maps.newHashMap();
		String sql = "SELECT t.`product_name`,t.`color`,t.`asin` FROM psi_sku t WHERE t.`del_flag`='0' AND t.`use_barcode`='1' AND t.`asin` IS NOT NULL "+
				" GROUP BY t.`product_name`,t.`color`,t.`asin`";
		List<Object[]> list = commentDao.findBySql(sql);
		for (Object[] obj : list) {
			String productName = obj[0].toString();
			String color = obj[1]==null?"":obj[1].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			String asin = obj[2].toString();
			rs.put(asin, productName);
		}
		return rs;
	}
	
	/**
	 * 根据产品名称和国家查找符合条件的asin
	 * @param country
	 * @param productName
	 * @return
	 */
	public List<String> getProductAsinList(String country, String productName) {
		List<String> rs = Lists.newArrayList();
		String temp = "";
		if (StringUtils.isNotEmpty(country)) {
			temp = " AND country='"+country+"'";
		}
		String sql = "SELECT DISTINCT t.`asin` FROM psi_sku t WHERE t.`del_flag`='0' AND t.`use_barcode`='1' AND t.`asin` IS NOT NULL"+ temp +
				" AND CONCAT(t.`product_name`,CASE WHEN t.`color`='' OR t.`color` IS NULL THEN '' ELSE CONCAT('_',t.`color`) END) LIKE :p1";
		List<Object> list = commentDao.findBySql(sql, new Parameter("%" + productName +"%"));
		for (Object obj : list) {
			if (obj != null) {
				rs.add(obj.toString());
			}
		}
		return rs;
	}
	
	public Map<String, String> getCustomEmailMap() {
		Map<String, String> rs = Maps.newHashMap();
		String sql = "SELECT DISTINCT t.`customer_id`,f.`reason`,f.id FROM amazoninfo_comment t ,amazoninfo_review_comment c, `amazoninfo_custom_filter` f"+
				" WHERE t.`reivew_id`=c.`id` AND t.`task_id`=f.`id` AND t.`sent_date`>DATE_ADD(CURDATE(), INTERVAL -15 DAY) AND c.`review_date`>=DATE_ADD(CURDATE(),INTERVAL -3 DAY)";
		List<Object[]> list = commentDao.findBySql(sql);
		if (list == null) {
			return rs;
		}
		for (Object[] obj : list) {
			String customId = obj[0].toString();
			String reason = obj[1]==null?"EmailCustomer":obj[1].toString();
			Integer id = Integer.parseInt(obj[2].toString());
			rs.put(customId, "<br/><a href='"+ BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/afterSale/view?id="+id+"'>"+reason+"</a>");
		}
		return rs;
	}
	
	public String getReasonByCustomerId(String customerId) {
		StringBuffer rs = new StringBuffer("");
		String sql = "SELECT DISTINCT f.`reason`,f.id FROM amazoninfo_comment t ,amazoninfo_review_comment c, `amazoninfo_custom_filter` f"+
				" WHERE t.`reivew_id`=c.`id` AND t.`task_id`=f.`id` AND f.`reason` IS NOT NULL AND c.`star`<=3 AND t.`customer_id`=:p1";
		List<Object[]> list = commentDao.findBySql(sql, new Parameter(customerId));
		if (list == null) {
			return rs.toString();
		}
		for (Object[] obj : list) {
			String reason = obj[0]==null?"EmailCustomer":obj[0].toString();
			Integer id = Integer.parseInt(obj[1].toString());
			rs.append("<a href='"+ BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/afterSale/view?id="+id+"'>"+reason+"</a>");
		}
		return rs.toString();
	}
	
	/**
	 * 找出即将到期的任务,邮件通知
	 * @return
	 */
	public void notice() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String end = format.format(DateUtils.addDays(new Date(), 1));
		String sql = "SELECT GROUP_CONCAT(t.`id`),u.`login_name`,u.`email` FROM `amazoninfo_custom_filter` t, sys_user u "+
				" WHERE t.`create_by`=u.`id` AND u.`del_flag`='0' AND t.`end_date`=:p1 AND t.`state`='1' AND t.`del_flag`='0' group by t.`create_by`";
		List<Object[]> list = commentDao.findBySql(sql, new Parameter(end));
		for (Object[] obj : list) {
			String ids = obj[0].toString();
			String loginName = obj[1].toString();
			String email = obj[2].toString();
			final MailInfo mailInfo = new MailInfo(email, "售后邮件即将到期通知", new Date());
			String content = "<p><span style='font-size:20px'>Hi "+loginName+":<br/>&nbsp;&nbsp;&nbsp;&nbsp;你创建的编号为："+ids+"的售后邮件任务将于明天("+end+")到期,请知悉。</span>" +
        			"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/afterSale/'>点击处理</a></p>";
			mailInfo.setContent(content);
			new Thread(){
			    public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
			WeixinSendMsgUtil.sendTextMsgToUser(loginName, "Hi "+loginName+":\n你创建的编号为："+ids+"的售后邮件任务将于明天("+end+")到期,请知悉。");
		}
	}
}


