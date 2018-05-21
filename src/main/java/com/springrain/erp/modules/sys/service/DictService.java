/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.springrain.erp.modules.sys.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.CacheUtils;
import com.springrain.erp.modules.sys.dao.DictDao;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.utils.DictUtils;

/**
 * 字典Service
 * @author ThinkGem
 * @version 2013-5-29
 */
@Service
@Transactional(readOnly = true)
public class DictService extends BaseService {

	@Autowired
	private DictDao dictDao;
	public static final String CACHE_DICT_MAP = "dictMap";
	
	public Dict get(String id) {
		return dictDao.get(id);
	}
	
	public Page<Dict> find(Page<Dict> page, Dict dict) {
		DetachedCriteria dc = dictDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(dict.getType())){
			dc.add(Restrictions.eq("type", dict.getType()));
		}
		if (StringUtils.isNotEmpty(dict.getDescription())){
			dc.add(Restrictions.like("description", "%"+dict.getDescription()+"%"));
		}
		dc.add(Restrictions.eq(Dict.FIELD_DEL_FLAG, Dict.DEL_FLAG_NORMAL));
		dc.addOrder(Order.asc("type")).addOrder(Order.asc("sort")).addOrder(Order.desc("id"));
		return dictDao.find(page, dc);
	}
	    
	public List<String> findTypeList(){
		return dictDao.findTypeList();
	}
	
	@Transactional(readOnly = false)
	public void save(Dict dict) {
		dictDao.save(dict);
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		dictDao.deleteById(id);
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}
	
	public boolean isExist(Dict dict,String dicId){
		DetachedCriteria dc = dictDao.createDetachedCriteria();
		dc.add(Restrictions.eq("type",dict.getType().trim()));
		dc.add(Restrictions.eq("value",dict.getValue().trim()));
		if(StringUtils.isNotEmpty(dicId)){
			dc.add(Restrictions.ne("id",dicId));
		}
		return dictDao.count(dc)>0;
	}
	
	
	public List<String> findByType(String type) {
//		DetachedCriteria dc = dictDao.createDetachedCriteria();
//		dc.add(Restrictions.eq("type", type));
//		dc.add(Restrictions.eq(Dict.FIELD_DEL_FLAG, Dict.DEL_FLAG_NORMAL));
//		return dictDao.find(dc);
		String sql ="SELECT a.`value` FROM sys_dict AS a WHERE a.`del_flag`='0' AND a.`type`=:p1 AND a.`value`<>'com.unitek' ORDER BY FIELD(a.`value`,'de','com','uk','fr','jp','it','es','ca','mx')";
		return this.dictDao.findBySql(sql,new Parameter(type));
	}
	
}
