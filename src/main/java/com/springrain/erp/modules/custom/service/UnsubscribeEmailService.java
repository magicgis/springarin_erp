package com.springrain.erp.modules.custom.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.custom.dao.UnsubscribeEmailDao;
import com.springrain.erp.modules.custom.entity.UnsubscribeEmail;

@Component
@Transactional(readOnly = true)
public class UnsubscribeEmailService extends BaseService{
	@Autowired
	private UnsubscribeEmailDao subscribeDao;
	private static String key = Global.getConfig("ws.key");
	
	@Transactional(readOnly = false)
	public void save(UnsubscribeEmail email){
		this.subscribeDao.save(email);
	}
	
	public static String getUnsubscribeEmaliHref(String email){
		String rs = "";
		String key1=getKeyByMD5(email);
		rs = BaseService.BASE_WEBPATH+"/php/cancelEmailNote?email="+email+"&key="+key1;
		return rs;
	}
	
	public static String getKeyByMD5(String email){
		return DigestUtils.md5DigestAsHex((email+key).getBytes());
	}

	
	public boolean  isNotExist(String email){
		DetachedCriteria dc = subscribeDao.createDetachedCriteria();
		dc.add(Restrictions.eq("customEmail", email));
		return subscribeDao.count(dc)==0;
	}
	
	
	public List<Object> findEmails(){
		return this.subscribeDao.findBySql("SELECT DISTINCT custom_email FROM custom_unsubscribe_email where custom_email is not null ");
	}
	
	
}
