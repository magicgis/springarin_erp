/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.custom.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.custom.dao.AutoReplyDao;
import com.springrain.erp.modules.custom.entity.AutoReply;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 邮件自动回复Service
 * @author tim
 * @version 2014-09-24
 */
@Component
@Transactional(readOnly = true)
public class AutoReplyService extends BaseService {

	@Autowired
	private AutoReplyDao autoReplyDao;
	
	public AutoReply get(String id) {
		return autoReplyDao.get(id);
	}
	
	public AutoReply findByType(AutoReply autoReply) {
		DetachedCriteria dc = autoReplyDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(autoReply.getType())){
			dc.add(Restrictions.eq("type",autoReply.getType()));
		}
		dc.add(Restrictions.eq("createBy",UserUtils.getUser()));
		dc.add(Restrictions.eq(AutoReply.FIELD_DEL_FLAG, AutoReply.DEL_FLAG_NORMAL));
		List<AutoReply> rs = autoReplyDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	public AutoReply findByUser(AutoReply autoReply) {
		DetachedCriteria dc = autoReplyDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(autoReply.getType())){
			dc.add(Restrictions.eq("type",autoReply.getType()));
		}
		if (autoReply.getCreateBy()!=null){
			dc.add(Restrictions.eq("createBy",autoReply.getCreateBy()));
		}
		dc.add(Restrictions.eq(AutoReply.FIELD_DEL_FLAG, AutoReply.DEL_FLAG_NORMAL));
		List<AutoReply> rs = autoReplyDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public void save(AutoReply autoReply) {
		autoReplyDao.save(autoReply);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		autoReplyDao.deleteById(id);
	}
	
}
