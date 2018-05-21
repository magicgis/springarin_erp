/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.custom.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.custom.dao.CommentDao;
import com.springrain.erp.modules.custom.entity.Comment;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 评论Service
 * @author tim
 * @version 2014-05-21
 */
@Component
@Transactional(readOnly = true)
public class CommentService extends BaseService {

	@Autowired
	private CommentDao commentDao;
	
	public Comment get(String id) {
		return commentDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(Comment comment) {
		commentDao.save(comment);
	}
	
	@Transactional(readOnly = false)
	public void save(List<Comment> comment) {
		commentDao.save(comment);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		commentDao.deleteById(id);
	}
	
	public long findNewSysComment(Event event) {
		DetachedCriteria dc = commentDao.createDetachedCriteria(Restrictions.eq("event", event),Restrictions.eq("createBy",UserUtils.getUserById("1")));
		Date today = new Date();
		if(today.getDay()==1){
			dc.add(Restrictions.gt("createDate", DateUtils.addHours(today, -72)));
		}else{
			dc.add(Restrictions.gt("createDate", DateUtils.addHours(today, -24)));
		}
		return commentDao.count(dc);
	}
	
	public long findStarChangeComment(Event event) {
		DetachedCriteria dc = commentDao.createDetachedCriteria(Restrictions.eq("event", event),Restrictions.eq("createBy",UserUtils.getUserById("1")),Restrictions.like("comment","%事件发生了改变%"));
		Date today = new Date();
		if(today.getDay()==1){
			dc.add(Restrictions.gt("createDate", DateUtils.addHours(today, -72)));
		}else{
			dc.add(Restrictions.gt("createDate", DateUtils.addHours(today, -24)));
		}
		return commentDao.count(dc);
	}
	
	public long findAddChangeComment(Event event) {
		DetachedCriteria dc = commentDao.createDetachedCriteria(Restrictions.eq("event", event),Restrictions.eq("createBy",UserUtils.getUserById("1")),Restrictions.like("comment","%Review Order add comment%"));
		Date today = new Date();
		if(today.getDay()==1){
			dc.add(Restrictions.gt("createDate", DateUtils.addHours(today, -72)));
		}else{
			dc.add(Restrictions.gt("createDate", DateUtils.addHours(today, -24)));
		}
		return commentDao.count(dc);
	}
	
	@Transactional(readOnly = false)
	public void updateEvent(Set<Integer> eventId){
		String sql="update custom_event_manager set update_date=:p1 where id in :p2";
		commentDao.updateBySql(sql, new Parameter(new Date(),eventId));
	}

}
