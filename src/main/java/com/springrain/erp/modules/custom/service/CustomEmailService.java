package com.springrain.erp.modules.custom.service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;
import com.springrain.erp.modules.custom.dao.CustomEmailDao;
import com.springrain.erp.modules.custom.dao.SendEmailDao;
import com.springrain.erp.modules.custom.entity.CustomEmail;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.sys.dao.MenuDao;
import com.springrain.erp.modules.sys.dao.RoleDao;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.Menu;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 邮件Service
 * @author tim
 * @version 2014-04-30
 */
@Component
@Transactional(readOnly = true)
public class CustomEmailService extends BaseService{

	@Autowired
	private CustomEmailDao customEmailDao;
	@Autowired
	private SendEmailDao sendEmailDao;
	
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private MenuDao menuDao;
	
	@Autowired
	private AmazonCustomerService amazonCustomerService;
	
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	public CustomEmail get(String id) {
		return customEmailDao.get(id);
	}
	
	public Page<CustomEmail> find(Page<CustomEmail> page, CustomEmail customEmail) {
		DetachedCriteria dc = customEmailDao.createDetachedCriteria();
		String customId = customEmail.getCustomId();
		boolean dateFlag = true;	//是否使用时间条件筛选,使用客户ID或邮箱精确查询时忽略时间范围
		if (StringUtils.isNotEmpty(customId)){
			dateFlag = false;
			customId = customId.trim();
			String sql = "SELECT DISTINCT a.`amz_email` FROM amazoninfo_customer a WHERE a.customer_id LIKE :p1 ";
			List<String> lists = customEmailDao.findBySql(sql, new Parameter(customId+"%"));
			if(lists!=null && lists.size()>0){
				dc.add(Restrictions.in("revertEmail", lists));
			} else {
				//没有查询到客户信息,利用索引字段快速返回结果
				dc.add(Restrictions.eq("revertEmail", customId));
			}
		}
		if (StringUtils.isNotEmpty(customEmail.getRevertEmail())){
			dateFlag = false;
			
			String rs=customEmail.getRevertEmail().trim();
			if(rs.contains("@")&&rs.startsWith("erp")){
				 String[] temp=rs.split("@");
				 rs=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
			}
			
			dc.add(Restrictions.or(Restrictions.eq("revertEmail", customEmail.getRevertEmail().trim()),Restrictions.eq("revertEmail",rs)));
		}
		if (StringUtils.isNotEmpty(customEmail.getSubject())){
			dateFlag = false;
			dc.add(Restrictions.like("subject", customEmail.getSubject().trim()+"%"));
		}
		if (StringUtils.isNotEmpty(customEmail.getReceiveContent())){
			dc.add(Restrictions.like("receiveContent","%"+customEmail.getReceiveContent().trim()+"%"));
		}
		
		if (StringUtils.isNotEmpty(customEmail.getRemarks())){
			dc.add(Restrictions.like("remarks","%"+customEmail.getRemarks().trim()+"%"));
		}
		
		if(StringUtils.isNotEmpty(customEmail.getAttchmentPath())){
			dc.add(Restrictions.or(Restrictions.isNotNull("attchmentPath"),Restrictions.isNotNull("inlineAttchmentPath")));
		}
		
		if(StringUtils.isNotEmpty(customEmail.getFlag())){
			dc.add(Restrictions.eq("flag", customEmail.getFlag()));
		}else{
			if (StringUtils.isNotEmpty(customEmail.getState())){
				if ("5".equals(customEmail.getState())) {
					dc.add(Restrictions.in("state", Lists.newArrayList("0", "1")));
				} else {
					dc.add(Restrictions.eq("state", customEmail.getState()));
				}
				
			}
		}
		if (dateFlag) {
			if (!("0".equals(customEmail.getState()) || "1".equals(customEmail.getState()) || "5".equals(customEmail.getState()))) {
				if (customEmail.getCustomSendDate() != null){
					dc.add(Restrictions.ge("customSendDate", customEmail.getCustomSendDate()));
				}
				if (customEmail.getEndDate() != null){
					dc.add(Restrictions.le("customSendDate", DateUtils.addDays(customEmail.getEndDate(), 1)));
				}
			}
		}
		
		dc.add(Restrictions.eq(CustomEmail.FIELD_DEL_FLAG, CustomEmail.DEL_FLAG_NORMAL));
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			User masterBy = customEmail.getMasterBy();
			//masterBy为空但是没有代回权限,限制查看自己的邮件
			if(null ==masterBy && !UserUtils.hasPermission("custom:email:proxy")){
				customEmail.setMasterBy(user);
				masterBy = user;
			}
			//有代回权限同时也有国家客服角色(其他客服除外)默认查看自己的邮件
			if (null ==masterBy && UserUtils.hasPermission("custom:email:proxy")) {
				if (isCustomerService(user.getId())) {	
					customEmail.setMasterBy(user);
					masterBy = user;
				}
			}
			if (masterBy != null && StringUtils.isNotEmpty(masterBy.getId())){
				dc.add(Restrictions.eq("masterBy",masterBy));
			}	
		} else if (customEmail.getMasterBy() != null && StringUtils.isNotEmpty(customEmail.getMasterBy().getId())) {
			dc.add(Restrictions.eq("masterBy", customEmail.getMasterBy()));
		}
		
		if(StringUtils.isNotBlank(customEmail.getProductName())){
			String sql="SELECT d.name FROM ( "+
						" SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.type "+ 
						" FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.del_flag='0' ) d "+
						" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type' "+
						" JOIN psi_product_type_group g ON t.id=g.`dict_id`  "+
						" JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0' WHERE p.id=:p1 ";
			List<String> lists = customEmailDao.findBySql(sql, new Parameter(customEmail.getProductName()));
			if(lists!=null && lists.size()>0){
				dc.add(Restrictions.in("productName",lists));
			}else{
				dc.add(Restrictions.eq("productName", customEmail.getProductName()));
			}
		}
		if (StringUtils.isNotEmpty(customEmail.getFollowState())){
			Date today=new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			dc.add(Restrictions.and(Restrictions.eq("followState","0"),Restrictions.ge("followDate",today)));
		}
		//dc.add(Restrictions.in("revertServerEmail",amazonAccountConfigService.findAllEmailByServer()));
		
		page =  customEmailDao.find(page, dc);
		for (CustomEmail email : page.getList()) {
			if(email.getRevertEmail().contains("@marketplace.amazon")){
				List<Object> list = customEmailDao.findBySql("select id from custom_event_manager where custom_email = :p1 and type in('1','2') order by type,create_date desc",new Parameter(email.getRevertEmail()));
				if(list.size()>0){
					email.setCustomId(list.get(0).toString());
				}
			}else{
				String amzEmail=amazonCustomerService.findAmzEmail(email.getRevertEmail());
				if(StringUtils.isNotBlank(amzEmail)){
					List<Object> list = customEmailDao.findBySql("select id from custom_event_manager where custom_email = :p1 and type in('1','2') order by  type,create_date desc",new Parameter(amzEmail));
					if(list.size()>0){
						email.setCustomId(list.get(0).toString());
					}
				}
			}
			if(StringUtils.isNotBlank(email.getCustomId())&&"2".equals(email.getState())){
				String sql="SELECT GROUP_CONCAT(check_state) FROM custom_send_email e WHERE e.`custom_email`=:p1 AND del_flag='0'";
				List<String> stateList=customEmailDao.findBySql(sql,new Parameter(email.getId()));
				if(stateList!=null){
					String checkState="";
					if(stateList.contains("2")){//0:未审核 1:审核通过  2：审核未通过
						checkState="<span style='color:#ff0033;'>Audit failure</span>";
					}else if(stateList.contains("0")){
						checkState="<span style='color:#ff0033;'>Unaudited</span>";
					}
					email.setCheckState(checkState);
				}
			}
		}
		return page;
	}
	
	/**
	 * 判断用户是否有其他客服以外的客服权限
	 * @param userId 用户ID
	 * @return
	 */
	public boolean isCustomerService(String userId) {
		if (StringUtils.isEmpty(userId)) {
			return false;
		}
		String sql="SELECT COUNT(*) FROM sys_menu n " +
				" JOIN sys_role_menu m ON n.`id`=m.`menu_id`"+
				" JOIN sys_role r ON r.`id`=m.`role_id` AND r.`del_flag`='0' "+
				" JOIN sys_user_role s ON s.`role_id`=r.`id`"+
				" WHERE n.`permission` LIKE 'custom:service:%' "+
				" AND n.`del_flag`='0' AND s.`user_id`=:p1 ";
		int num = ((BigInteger)customEmailDao.findBySql(sql, new Parameter(userId)).get(0)).intValue();
		if (num > 0) {
			return true;
		}
		return false;
	}
	
	public Map<String,Set<String>> findAllEventByEmail(Set<String> emailSet){
		Map<String,Set<String>> map=Maps.newHashMap();
		String sql="select custom_email,id from custom_event_manager where custom_email in :p1 and type in('1','2') ";
		List<Object[]> list = customEmailDao.findBySql(sql,new Parameter(emailSet));
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				Set<String> temp=map.get(obj[0].toString());
				if(temp==null){
					temp=Sets.newHashSet();
					map.put(obj[0].toString(), temp);
				}
				temp.add(obj[1].toString());
			}
		}
		return map;
	}
	
	public Page<CustomEmail> findProblems(Page<CustomEmail> page, CustomEmail customEmail) {
		DetachedCriteria dc = customEmailDao.createDetachedCriteria();
		if(customEmail.getAnswerDate()!=null){
			Restrictions.ge("answerDate", customEmail.getAnswerDate());
		}
		
		if(customEmail.getEndDate()!=null){
			Restrictions.ge("endDate", customEmail.getEndDate());
		}
		
		if(StringUtils.isNotEmpty(customEmail.getProductName())){
			Restrictions.like("productName", "%"+customEmail.getProductName()+"%");
		}
		
		dc.add(Restrictions.eq(CustomEmail.FIELD_DEL_FLAG, CustomEmail.DEL_FLAG_NORMAL));
		dc.add(Restrictions.isNotNull("problemType"));//查询填写了备注的
		dc.add(Restrictions.isNotNull("answerDate"));
		return customEmailDao.find(page, dc);
	}
	
	public CustomEmail  getSingleByEmail(String email){
		DetachedCriteria dc = customEmailDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(email)){
			dc.add(Restrictions.eq("revertEmail", email));
			dc.add(Restrictions.isNotNull("productName"));
			dc.add(Restrictions.ne("state", "4"));
		}else{
			return null;
		}
		dc.addOrder(Order.desc("createDate"));
		
		List<CustomEmail> list = this.customEmailDao.find(dc);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public Page<CustomEmail> findAll(Page<CustomEmail> page, CustomEmail customEmail) {
		DetachedCriteria dc = customEmailDao.createDetachedCriteria();
		boolean dateFlag = true;	//是否使用时间条件筛选,使用客户ID或邮箱精确查询时忽略时间范围
		String customId = customEmail.getCustomId();
		if (StringUtils.isNotEmpty(customId)){
			dateFlag = false;
			customId = customId.trim();
			String sql = "SELECT DISTINCT a.`amz_email` FROM amazoninfo_customer a WHERE a.customer_id LIKE :p1 ";
			List<String> lists = customEmailDao.findBySql(sql, new Parameter(customId+"%"));
			if(lists!=null && lists.size()>0){
				dc.add(Restrictions.in("revertEmail", lists));
			} else {
				dc.add(Restrictions.eq("revertEmail", customId));
			}
		}
		if (StringUtils.isNotEmpty(customEmail.getRevertEmail())){
			dateFlag = false;
			
			String rs=customEmail.getRevertEmail().trim();
			if(rs.contains("@")&&rs.startsWith("erp")){
				 String[] temp=rs.split("@");
				 rs=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
			}
			
			dc.add(Restrictions.or(Restrictions.eq("revertEmail", customEmail.getRevertEmail().trim()),Restrictions.eq("revertEmail",rs)));
			
			//dc.add(Restrictions.eq("revertEmail", customEmail.getRevertEmail().trim()));
		}
		if (StringUtils.isNotEmpty(customEmail.getSubject())){
			dateFlag = false;
			dc.add(Restrictions.like("subject", customEmail.getSubject().trim()+"%"));
		}
		if (StringUtils.isNotEmpty(customEmail.getState())){
			dc.add(Restrictions.eq("state", customEmail.getState()));
		}
		if (customEmail.getMasterBy()!=null&&customEmail.getMasterBy().getId().length()>0){
			dc.add(Restrictions.eq("masterBy",customEmail.getMasterBy()));
		}
		if (dateFlag) {
			if (!("0".equals(customEmail.getState()) || "1".equals(customEmail.getState()))) {
				if (customEmail.getCustomSendDate() != null){
					dc.add(Restrictions.ge("customSendDate", customEmail.getCustomSendDate()));
				}
				if (customEmail.getEndDate() != null){
					dc.add(Restrictions.le("customSendDate", DateUtils.addDays(customEmail.getEndDate(), 1)));
				}
			}
		}
		dc.add(Restrictions.eq(CustomEmail.FIELD_DEL_FLAG, CustomEmail.DEL_FLAG_NORMAL));	
		return customEmailDao.find(page, dc);
	}
	
	public Map<String,Map<String, String>> count(CustomEmail customEmail) {
		List<Criterion> ses = Lists.newArrayList();
		//ses.add(Restrictions.ge("createDate",customEmail.getCreateDate()));
		Date date =  customEmail.getEndDate();
		date.setHours(23);
		date.setMinutes(59);
		ses.add(Restrictions.or(Restrictions.ne("remarks","2"),Restrictions.isNull("remarks")));
		//ses.add(Restrictions.le("createDate",date));
		ses.add(Restrictions.eq(CustomEmail.FIELD_DEL_FLAG, CustomEmail.DEL_FLAG_NORMAL));
		return customEmailDao.findCount(ses,customEmail.getCreateDate(),date);
	}
	
	public List<CustomEmail> findNoreply() {
		DetachedCriteria dc = customEmailDao.createDetachedCriteria();
		dc.add(Restrictions.eq("state", "4"));
		dc.add(Restrictions.eq(CustomEmail.FIELD_DEL_FLAG, CustomEmail.DEL_FLAG_NORMAL));
		dc.add(Restrictions.eq("result", "正在等待结果..."));
		return customEmailDao.find(dc);
	}
	
	
	public User findMaster(String email) {
		
		Page<SendEmail> page1 = new Page<SendEmail>();
		page1.setPageSize(1);
		page1.setPageNo(1);
		page1 = sendEmailDao.find(page1,"from SendEmail where delFlag = '0' and sendEmail=:p1 and createBy.delFlag = '0' and sendFlag = '1' Order by sentDate Desc ",new Parameter(email));
		List<SendEmail> lists  = page1.getList();
		if(lists.size()>0){
			return lists.get(0).getCreateBy();
		}
		Page<CustomEmail> page = new Page<CustomEmail>();
		page.setPageSize(1);
		page.setPageNo(1);
		page = customEmailDao.find(page,"from CustomEmail where delFlag = '0' and state !='4'  and revertEmail=:p1 and masterBy.delFlag = '0' Order by endDate Desc",new Parameter(email));
		List<CustomEmail> list  = page.getList();
		if(list.size()>0){
			return list.get(0).getMasterBy();
		}
		return null;
	}
	
	public List<CustomEmail> findRelativeEmail(String revertEmail,String emailId) {
		DetachedCriteria dc = customEmailDao.createDetachedCriteria();
		dc.add(Restrictions.eq("revertEmail",revertEmail));
		dc.add(Restrictions.ne("id",emailId));
		dc.add(Restrictions.eq(CustomEmail.FIELD_DEL_FLAG, CustomEmail.DEL_FLAG_NORMAL));	
		dc.addOrder(Order.desc("customSendDate"));
		return customEmailDao.find(dc);
	}
	
	public List<CustomEmail> findExceptionEmail() {
		return customEmailDao.find("from CustomEmail where delFlag = '0' and customSendDate > '"+DateUtils.getDate(DateUtils.addMonths(new Date(),-3),"yyyy-MM-dd")+"' and (state='1' or state ='0') and masterBy.delFlag = '1'");
	}
	  
	@Transactional(readOnly = false)
	public void  saveProblem(String country,String productName,String  problemType,String problem,String id,String orderNos ){
		if(StringUtils.isNotEmpty(orderNos)){
			String sql ="UPDATE custom_email_manager  SET country=:p2,product_name=:p3,problem_type=:p4,problem=:p5,order_nos=:p6 WHERE id=:p1  ";
			this.customEmailDao.updateBySql(sql, new Parameter(id,country,productName,problemType,problem,orderNos));  
		}else{
			String sql ="UPDATE custom_email_manager  SET country=:p2,product_name=:p3,problem_type=:p4,problem=:p5 WHERE id=:p1  ";
			this.customEmailDao.updateBySql(sql, new Parameter(id,country,productName,problemType,problem));  
		}
		
	}
	
	
	/**
	 *保存备注 
	 */
	@Transactional(readOnly = false)
	public void  saveRemark(String remark,String customEmailId){
		try{
			if(StringUtils.isNotEmpty(remark)){
				String sql ="UPDATE custom_email_manager AS a SET a.`remarks`=:p2 WHERE a.id=:p1  ";
				this.customEmailDao.updateBySql(sql, new Parameter(customEmailId,remark));  
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Transactional(readOnly = false)
	public void save(CustomEmail customEmail) {
		customEmailDao.save(customEmail);
	}
	
	@Transactional(readOnly = false)
	public void updateRemindFlag(String id) {
		String sql ="UPDATE custom_email_manager AS a SET a.`remind_flag`='0' WHERE a.id=:p1  ";
		this.customEmailDao.updateBySql(sql, new Parameter(id));  
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		customEmailDao.deleteById(id);
	}
	
	public List<Role>  findCustomMasterRole(){
		 DetachedCriteria dc = menuDao.createDetachedCriteria();
		 dc.add(Restrictions.like("permission","custom:service:%"));
		 dc.add(Restrictions.eq("delFlag","0"));
		 List<Menu> list=menuDao.find(dc);
		 List<Role> roles =Lists.newArrayList();
		 for (Menu menu : list) {
			 List<Role> roleList=menu.getRoleList();
			 for (Role role : roleList) {
				 Hibernate.initialize(role.getUserList());
			 }
			 roles.addAll(roleList);
		 }
		 return roles;
		/* DetachedCriteria criteria = roleDao.createDetachedCriteria();
		 criteria.createAlias("this.menuList", "menuList");
		 List<Dict> dicts = DictUtils.getDictList("platform");
		 Set<String> permissionSet=Sets.newHashSet("custom:service:other.");
		 for (Dict dict : dicts) {
			permissionSet.add("custom:service:"+dict.getValue());
		 }
		 criteria.add(Restrictions.in("menuList.permission",permissionSet));
		 List<Role> roles =  roleDao.find(criteria);
		 for (Role role : roles) {
			Hibernate.initialize(role.getUserList());
		 }
		 return roles;*/
	}
	
	public boolean isProcessedEmail(String emailId){
		List<Object> list = customEmailDao.findBySql("select count(1) from custom_email_manager where email_id = :p1",new Parameter(emailId));
		if(list!=null&&list.size()==1){
			return Integer.parseInt(list.get(0).toString())>0;
		}
		return true;
	}
	
	public Set<String> findNoAutoReply(){
		return customEmailDao.findNoAutoReply();
	}

	public Map<String,Map<String,Object[]>> getUnDealEmailMap(){
		String sql="SELECT s.name,m.state,COUNT(*) total,min(m.custom_send_date) FROM custom_email_manager m JOIN sys_user  s ON m.`master_by`=s.`id`  "+
                 " WHERE (m.`state`='0' or m.`state`='1') AND m.del_flag='0' AND s.del_flag='0' AND  HOUR(TIMEDIFF(NOW(),m.custom_send_date))>24 and s.name is not null GROUP BY s.`name`,m.state  order by total desc ";
		Map<String,Map<String,Object[]>> map=Maps.newLinkedHashMap();
		List<Object[]> list=customEmailDao.findBySql(sql);
		for (Object[] obj : list) {
			Map<String,Object[]> emailMap=map.get(obj[0].toString());
			if(emailMap==null){
				emailMap=Maps.newLinkedHashMap();
				map.put(obj[0].toString(),emailMap);
			}
			Object[] temp=new Object[2];
			temp[0]=obj[2];
			temp[1]=obj[3];
			emailMap.put(obj[1].toString(),temp);	
		}
		return map;
	}

	public List<CustomEmail> findByEmail(String email) {
		DetachedCriteria dc = customEmailDao.createDetachedCriteria();
		dc.add(Restrictions.eq("revertEmail", email));
		dc.add(Restrictions.eq("revertServerEmail", "support@inateck.com"));
		dc.add(Restrictions.eq(CustomEmail.FIELD_DEL_FLAG, CustomEmail.DEL_FLAG_NORMAL));
		return customEmailDao.find(dc);
	}
	
	public Map<String,String> findAllEmail(Set<String> emaliSet){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT id,CONCAT(create_date,',',SUBJECT) FROM custom_email_manager r WHERE r.`revert_email` IN :p1";
		List<Object[]> list=customEmailDao.findBySql(sql, new Parameter(emaliSet));
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				map.put(obj[0].toString(),obj[1].toString());
			}
		}
		return map;
	}
	
	public Map<String,SendEmail> findSendEmail(Set<String> emaliSet){
		Map<String,SendEmail> map=Maps.newLinkedHashMap();
		 String sendSql="SELECT s.send_email,r.name,s.id,s.send_subject,s.sent_date,send_attchment_path FROM custom_send_email s JOIN sys_user r ON r.id=s.create_by "+
					" WHERE s.send_email in :p1 AND TYPE='0' AND s.del_flag='0' and s.sent_date is not null order by s.sent_date desc";
		 List<Object[]> sendList=customEmailDao.findBySql(sendSql, new Parameter(emaliSet));
		 if(sendList!=null&&sendList.size()>0){
			 for (Object[] obj: sendList) {
				 //String id=obj[0].toString();
				 String name=obj[1].toString();
				 String sendId=obj[2].toString();
				 String subject=(obj[3]==null?"":obj[3].toString());
				 Date date=(Timestamp)obj[4];
				 String sendAttchmentPath=(obj[5]==null?"":obj[5].toString());
				 SendEmail sendEmail=new SendEmail();
				 sendEmail.setId(sendId);
				 sendEmail.setSendSubject(subject);
				 sendEmail.setSentDate(date);
				 sendEmail.setSendContent(name);
				 sendEmail.setSendAttchmentPath(sendAttchmentPath);
				 map.put(sendId, sendEmail);
			 }
		 }
		 return map;
	}
	
	public Map<String,CustomEmail> findAllTypeEmail(Set<String> emaliSet){
		 Map<String,CustomEmail> map=Maps.newLinkedHashMap();
		 String sql="SELECT r.id,r.create_date,r.subject,s.name,attchment_path,inline_attchment_path  FROM custom_email_manager r JOIN sys_user s ON r.master_by=s.id WHERE r.`revert_email` IN :p1 order by create_date desc";
		 List<Object[]> list=customEmailDao.findBySql(sql, new Parameter(emaliSet));
		 if(list!=null&&list.size()>0){
			 for (Object[] obj: list) {
				String id=obj[0].toString();
				Date date=(Timestamp)obj[1];
				String subject=obj[2].toString();
				String name=obj[3].toString();
				String attchmentPath=(obj[4]==null?"":obj[4].toString());
				String inlineAttchmentPath=(obj[5]==null?"":obj[5].toString());
				CustomEmail email=new CustomEmail();
				email.setId(id);
				email.setCreateDate(date);
				email.setSubject(subject);
				email.setCustomId(name);
				email.setAttchmentPath(attchmentPath);
				email.setInlineAttchmentPath(inlineAttchmentPath);
				map.put(id, email);
			 }
		 }
		 if(map!=null&&map.size()>0){
			 Set<String> idSet=map.keySet();
			 String sendSql="SELECT s.custom_email,r.name,s.id,s.send_subject,s.sent_date,send_attchment_path FROM custom_send_email s JOIN sys_user r ON r.id=s.create_by "+
						" WHERE s.custom_email IN :p1 AND TYPE='1' AND s.del_flag='0' order by s.sent_date desc ";
			 List<Object[]> sendList=customEmailDao.findBySql(sendSql, new Parameter(idSet));
			 if(sendList!=null&&sendList.size()>0){
				 for (Object[] obj: sendList) {
					 String id=obj[0].toString();
					 String name=obj[1].toString();
					 String sendId=obj[2].toString();
					 String subject=obj[3].toString();
					 Date date=(Timestamp)obj[4];
					 String sendAttchmentPath=(obj[5]==null?"":obj[5].toString());
					 SendEmail sendEmail=new SendEmail();
					 sendEmail.setId(sendId);
					 sendEmail.setSendSubject(subject);
					 sendEmail.setSentDate(date);
					 sendEmail.setSendContent(name);
					 sendEmail.setSendAttchmentPath(sendAttchmentPath);
					 CustomEmail customEmail=map.get(id);
					 sendEmail.setCustomEmail(customEmail);
					 List<SendEmail> emailList=customEmail.getSendEmails();
					 if(emailList==null){
						 emailList=Lists.newArrayList();
					 }
					 emailList.add(sendEmail);
					 customEmail.setSendEmails(emailList);
				 }
			 }
		 }
		 return map;
	}
	
	public Map<String, String> findWebSiteEmailMap(){
		String sql = "SELECT DISTINCT a.`id`,a.`receive_content` FROM custom_email_manager a WHERE a.`revert_email` = 'noreply@inateck.com'";
		List<Object[]> list = customEmailDao.findBySql(sql);
		Map<String, String> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			rs.put(objs[0].toString(), objs[1].toString());
		}
		return rs;
	}
	
	
    //查出历史问题
	public List<Object[]> findHistoryEmail(String email,String id){
		if(email.indexOf("do-not-reply@amazon.")<0){
			String sql ="";
			if(StringUtils.isNotEmpty(id)){
				sql = "SELECT DATE_FORMAT( a.`create_date`,'%Y-%m-%d'),a.`product_name`,a.`problem_type`,REPLACE(a.`problem`,'\"','&quot;') FROM custom_email_manager AS a " +
						" WHERE a.`revert_email`=:p1 AND a.`problem_type`<>'' AND a.`id`<>:p2 ORDER BY a.`create_date` DESC";
				return customEmailDao.findBySql(sql,new Parameter(email,id));
			}else{
				sql = "SELECT DATE_FORMAT( a.`create_date`,'%Y-%m-%d'),a.`product_name`,a.`problem_type`,REPLACE(a.`problem`,'\"','&quot;') FROM custom_email_manager AS a " +
						" WHERE a.`revert_email`=:p1 AND a.`problem_type`<>'' ORDER BY a.`create_date` DESC";
				return customEmailDao.findBySql(sql,new Parameter(email));
			}
		}else{
			return null;
		}
	}
 
	
	
	@Transactional(readOnly = false)
	public void  updateEmail(String id ,String email){
		String sql ="UPDATE custom_email_manager  SET revert_email = :p2 WHERE id=:p1  ";
		this.customEmailDao.updateBySql(sql, new Parameter(id,email));  
	}
	
	public List<CustomEmail> findBeforeEmail() {
		DetachedCriteria dc = customEmailDao.createDetachedCriteria();
		dc.add(Restrictions.ge("customSendDate",DateUtils.addDays(new Date(),-2)));
		dc.add(Restrictions.le("customSendDate",new Date()));
		dc.add(Restrictions.eq(CustomEmail.FIELD_DEL_FLAG, CustomEmail.DEL_FLAG_NORMAL));
		return customEmailDao.find(dc);
	}
	
	@Transactional(readOnly = false) 
	public void  updateSendFlag(Set<String> emailSet){
		String sql ="UPDATE custom_send_email  SET send_flag =2 WHERE send_email=:p1 and send_subject=:p2";
		String customSql="update amazoninfo_customer set message_state='1' where amz_email=:p1";
		
		for (String email: emailSet) {
			String[] arr=email.split(";;;");
			this.customEmailDao.updateBySql(sql, new Parameter(arr[1],arr[0])); 
			this.customEmailDao.updateBySql(customSql, new Parameter(arr[1])); 
		}
	}
	
	@Transactional(readOnly = false) 
	public void  updateEstimateFlag(Set<String> flagSet,String country){
		String tempSql="update custom_email_estimate set end_flag=:p1,update_date=now() where country=:p2";
		for (String flag: flagSet) {
			this.customEmailDao.updateBySql(tempSql, new Parameter(flag,country)); 
		}
	}
	
	
	
	public boolean findUndelivered(String email){
		String sql="select 1 from amazoninfo_customer where amz_email=:p1 and message_state='1' ";
		List<String> list=customEmailDao.findBySql(sql,new Parameter(email));
		if(list!=null&&list.size()>0){
			return true;
		}
		return false;
	}
	
	public Map<String,String> findEmailFlag(){
		Map<String,String>  map=Maps.newHashMap();
		String sql="select country,end_flag from custom_email_estimate";
		List<Object[]> list=customEmailDao.findBySql(sql);
		for (Object[] obj: list) {
			map.put(obj[0].toString(), obj[1]==null?"":obj[1].toString());
		}
		return map;
	}
	
	@Transactional(readOnly = false) 
	public void  updateCountryFlag(String country,String flag){
		String sql ="UPDATE custom_email_estimate  SET end_flag =:p1 WHERE country=:p2 ";
		this.customEmailDao.updateBySql(sql, new Parameter(flag,country)); 
	}
	
	 
	@Transactional(readOnly = false)
	public void  followEmail(String state,Date date,String id){
		if("0".equals(state)){//0:继续跟进 1：不用跟进
			String sql ="UPDATE custom_email_manager  SET follow_date =:p1,follow_state=:p2 WHERE id=:p3 ";
			this.customEmailDao.updateBySql(sql, new Parameter(date,state,id)); 
		}else{
			String sql ="UPDATE custom_email_manager  SET follow_date =:p1 WHERE id=:p3 ";
			this.customEmailDao.updateBySql(sql, new Parameter(state,id)); 
		}
	}
	
	public Map<String,Set<String>> findEmailByUser(){
		 Map<String,Set<String>> map=Maps.newHashMap();
		 String sql="SELECT r.id,r.`revert_email`,u.`email` FROM custom_email_manager r "+
				" JOIN sys_user u ON r.`master_by`=u.id AND u.`del_flag`='0' "+
				" WHERE r.`follow_date`=CURRENT_DATE() AND r.`follow_state`='0'";
		 List<Object[]> list=customEmailDao.findBySql(sql);
		 if(list!=null&&list.size()>0){
			 for (Object[] obj: list) {
				String id=obj[0].toString();
				String customEmail=obj[1].toString();
				String email=obj[2].toString();
				String emailLink=("<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/emailManager/form?id="+id+"'>"+customEmail+"</a>");
				Set<String> temp=map.get(email);
				if(temp==null){
					temp=Sets.newHashSet();
					map.put(email, temp);
				}
				temp.add(emailLink);
			 }
		 }
		 return map;
	}
	
		
}
