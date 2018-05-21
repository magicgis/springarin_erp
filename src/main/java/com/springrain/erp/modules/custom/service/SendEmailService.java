package com.springrain.erp.modules.custom.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;
import com.springrain.erp.modules.custom.dao.SendEmailDao;
import com.springrain.erp.modules.custom.entity.CustomEmail;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 发送邮件Service
 * @author tim
 * @version 2014-05-13
 */
@Component
@Transactional(readOnly = true)
public class SendEmailService extends BaseService {

	@Autowired
	private SendEmailDao sendEmailDao;
	@Autowired
	private AmazonCustomerService amazonCustomerService;
	
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	public SendEmail get(String id) {
		return sendEmailDao.get(id);
	}
	
	public Page<SendEmail> find(Page<SendEmail> page, SendEmail sendEmail) {
		DetachedCriteria dc = sendEmailDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(sendEmail.getSendSubject())){
			String rs=sendEmail.getSendSubject();
			if(sendEmail.getSendSubject().contains("@")&&sendEmail.getSendSubject().startsWith("erp")){
				 String[] temp=sendEmail.getSendSubject().split("@");
				 rs=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
			}
			dc.add(Restrictions.or(Restrictions.like("sendSubject", "%"+sendEmail.getSendSubject()+"%")
					,Restrictions.like("sendEmail", "%"+sendEmail.getSendSubject()+"%"),Restrictions.like("sendEmail", "%"+rs+"%")   ));
		}
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			dc.add(Restrictions.eq("createBy",user));
		}	
		dc.add(Restrictions.eq(SendEmail.FIELD_DEL_FLAG, SendEmail.DEL_FLAG_NORMAL));
		if(StringUtils.isNotEmpty(sendEmail.getSendFlag())){
			dc.add(Restrictions.eq("sendFlag",sendEmail.getSendFlag()));
		}
	/*	if (!"1".equals(Global.getConfig("server.id"))) {
			dc.add(Restrictions.in("serverEmail",amazonAccountConfigService.findAllEmailByServer()));
		} else {
			dc.add(Restrictions.or(Restrictions.isNull("serverEmail"),Restrictions.in("serverEmail",amazonAccountConfigService.findAllEmailByServer())));
		}
		*/
		
		page=sendEmailDao.find(page, dc);
		if("2".equals(sendEmail.getSendFlag())){
			for (SendEmail email : page.getList()) {
				if(email.getSendEmail().contains("@marketplace.amazon")){
					List<Object> list = sendEmailDao.findBySql("select id from custom_event_manager where custom_email = :p1 and type in('1','2') order by type,create_date desc",new Parameter(email.getSendEmail()));
					if(list.size()>0){
						email.setRemark(list.get(0).toString());
					}
				}
			}	
		}
		
		return page;
	}
	
	public Page<SendEmail> findCheck(Page<SendEmail> page, SendEmail sendEmail) {
		DetachedCriteria dc = sendEmailDao.createDetachedCriteria();
		dc.add(Restrictions.in("sendFlag",Sets.newHashSet("0","1","2")));
		if(sendEmail.getSentDate()==null){
			Date today=new Date();
			sendEmail.setSentDate(DateUtils.addDays(today,-8));
			sendEmail.setEndDate(DateUtils.addDays(today,1));
		}
		dc.add(Restrictions.ge("sentDate",sendEmail.getSentDate()));
		dc.add(Restrictions.lt("sentDate",DateUtils.addDays(sendEmail.getEndDate(),1)));
		
		if (StringUtils.isNotEmpty(sendEmail.getSendSubject())){
			String rs=sendEmail.getSendSubject();
			if(sendEmail.getSendSubject().contains("@")&&sendEmail.getSendSubject().startsWith("erp")){
				 String[] temp=sendEmail.getSendSubject().split("@");
				 rs=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
			}
			dc.add(Restrictions.or(Restrictions.like("sendSubject", "%"+sendEmail.getSendSubject()+"%")
					,Restrictions.like("sendEmail", "%"+sendEmail.getSendSubject()+"%"),Restrictions.like("sendEmail", "%"+rs+"%")   ));
		}
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			Set<String> countrySet=Sets.newHashSet();
			List<String> countryList=Lists.newArrayList("de","fr","it","es","uk","com","ca","jp","mx");
			for (String country : countryList) {
				if(SecurityUtils.getSubject().isPermitted("custom:emailCheck:"+country) || UserUtils.hasPermission("fbap0:email:approve")){
					countrySet.add(country);
				}
			}
			if(countrySet!=null&&countrySet.size()>0){
				dc.add(Restrictions.or(Restrictions.eq("createBy",user),Restrictions.in("country",countrySet)));
			}else{
				dc.add(Restrictions.eq("createBy",user));
			}
			
		}
		if (!UserUtils.hasPermission("fbap0:email:approve")) {
			if(StringUtils.isNotBlank(sendEmail.getCheckState())){
				dc.add(Restrictions.eq("checkState",sendEmail.getCheckState()));
			}else{
				dc.add(Restrictions.or(Restrictions.in("checkState",Sets.newHashSet("0","2")),Restrictions.and(Restrictions.eq("checkState","1"),Restrictions.isNotNull("reason")  )));
			}
		}
		if (UserUtils.hasPermission("fbap0:email:approve")) {
			if(StringUtils.isNotBlank(sendEmail.getCheckState())){
				//FBA贴P0未审核标记为3
				dc.add(Restrictions.eq("checkState", "0".equals(sendEmail.getCheckState())?"4":sendEmail.getCheckState()));
			}else{
				dc.add(Restrictions.in("checkState",Sets.newHashSet("4")));
			}
		}
		dc.add(Restrictions.eq(SendEmail.FIELD_DEL_FLAG, SendEmail.DEL_FLAG_NORMAL));
		/*if (!"1".equals(Global.getConfig("server.id"))) {
			dc.add(Restrictions.in("serverEmail",amazonAccountConfigService.findAllEmailByServer()));
		} else {
			dc.add(Restrictions.or(Restrictions.isNull("serverEmail"),Restrictions.in("serverEmail",amazonAccountConfigService.findAllEmailByServer())));
		}*/
		page=sendEmailDao.find(page, dc);
		for (SendEmail email : page.getList()) {
			if(email.getSendEmail().contains("@marketplace.amazon")){
				List<Object> list = sendEmailDao.findBySql("select id from custom_event_manager where custom_email = :p1 and type in('1','2') order by type,create_date desc",new Parameter(email.getSendEmail()));
				if(list.size()>0){
					email.setRemark(list.get(0).toString());
				}
			}else{
				String amzEmail=amazonCustomerService.findAmzEmail(email.getSendEmail());
				if(StringUtils.isNotBlank(amzEmail)){
					List<Object> list = sendEmailDao.findBySql("select id from custom_event_manager where custom_email = :p1 and type in('1','2') order by  type,create_date desc",new Parameter(amzEmail));
					if(list.size()>0){
						email.setRemark(list.get(0).toString());
					}
				}
			}
		}
		return page;
	}
	
	
	public Page<SendEmail> findCheck2(Page<SendEmail> page, SendEmail sendEmail) {
		DetachedCriteria dc = sendEmailDao.createDetachedCriteria();
		dc.add(Restrictions.in("sendFlag",Sets.newHashSet("0","1","2")));
		if(sendEmail.getSentDate()==null){
			Date today=new Date();
			sendEmail.setSentDate(DateUtils.addDays(today,-8));
			sendEmail.setEndDate(DateUtils.addDays(today,1));
		}
		dc.add(Restrictions.ge("sentDate",sendEmail.getSentDate()));
		dc.add(Restrictions.lt("sentDate",DateUtils.addDays(sendEmail.getEndDate(),1)));
		
		if (StringUtils.isNotEmpty(sendEmail.getSendSubject())){
			String rs=sendEmail.getSendSubject();
			if(sendEmail.getSendSubject().contains("@")&&sendEmail.getSendSubject().startsWith("erp")){
				 String[] temp=sendEmail.getSendSubject().split("@");
				 rs=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
			}
			dc.add(Restrictions.or(Restrictions.like("sendSubject", "%"+sendEmail.getSendSubject()+"%")
					,Restrictions.like("sendEmail", "%"+sendEmail.getSendSubject()+"%"),Restrictions.like("sendEmail", "%"+rs+"%")));
		}
		
		if(StringUtils.isNotBlank(sendEmail.getCheckState())){
			dc.add(Restrictions.eq("checkState",sendEmail.getCheckState()));
		}else{
			dc.add(Restrictions.or(Restrictions.in("checkState",Sets.newHashSet("0","2")),Restrictions.and(Restrictions.eq("checkState","1"),Restrictions.isNotNull("reason")  )));
		}
		
		dc.add(Restrictions.eq(SendEmail.FIELD_DEL_FLAG, SendEmail.DEL_FLAG_NORMAL));
		
		page=sendEmailDao.find(page, dc);
		for (SendEmail email : page.getList()) {
			if(email.getSendEmail().contains("@marketplace.amazon")){
				List<Object> list = sendEmailDao.findBySql("select id from custom_event_manager where custom_email = :p1 and type in('1','2') order by type,create_date desc",new Parameter(email.getSendEmail()));
				if(list.size()>0){
					email.setRemark(list.get(0).toString());
				}
			}else{
				String amzEmail=amazonCustomerService.findAmzEmail(email.getSendEmail());
				if(StringUtils.isNotBlank(amzEmail)){
					List<Object> list = sendEmailDao.findBySql("select id from custom_event_manager where custom_email = :p1 and type in('1','2') order by  type,create_date desc",new Parameter(amzEmail));
					if(list.size()>0){
						email.setRemark(list.get(0).toString());
					}
				}
			}
		}
		return page;
	}
	
	public boolean findEvent(String email){
		String sql="select id from custom_event_manager where custom_email = :p1 and type in('1','2')";
		List<Object[]> list=sendEmailDao.findBySql(sql,new Parameter(email));
		if(list!=null&&list.size()>0){
			return true;
		}
		return false;
	}
	
	public Map<String,Map<String, String>> count(CustomEmail customEmail) {
		List<SimpleExpression> ses = Lists.newArrayList();
		ses.add(Restrictions.ge("sentDate",customEmail.getCreateDate()));
		Date date =  customEmail.getEndDate();
		date.setHours(23);
		date.setMinutes(59);
		ses.add(Restrictions.le("sentDate",date));
		ses.add(Restrictions.eq(SendEmail.FIELD_DEL_FLAG, SendEmail.DEL_FLAG_NORMAL));
		return sendEmailDao.count(ses);
	}
	
	public SendEmail findBlankEmail(String email){
		if(email==null)email ="";
		DetachedCriteria dc = sendEmailDao.createDetachedCriteria();
		User user = UserUtils.getUser();
		dc.add(Restrictions.eq("createBy",user));
		dc.add(Restrictions.eq("sendEmail",email));
		dc.add(Restrictions.eq(SendEmail.FIELD_DEL_FLAG, SendEmail.DEL_FLAG_NORMAL));
		dc.add(Restrictions.eq("type","0"));
		dc.add(Restrictions.eq("sendFlag","0"));
		List<SendEmail> list = sendEmailDao.find(dc);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public void save(SendEmail sendEmail) {
		sendEmailDao.save(sendEmail);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		sendEmailDao.deleteById(id);
	}
	
	@Transactional(readOnly = false)
	public void  saveRemark(String remark,String customEmailId){
		try{
			if(StringUtils.isNotEmpty(remark)){
				String sql ="UPDATE custom_send_email AS a SET a.`reason`=:p2 WHERE a.id=:p1  ";
				this.sendEmailDao.updateBySql(sql, new Parameter(customEmailId,remark));  
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
