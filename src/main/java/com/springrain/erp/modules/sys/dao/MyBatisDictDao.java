/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.springrain.erp.modules.sys.dao;

import java.util.List;

import com.springrain.erp.common.persistence.annotation.MyBatisDao;
import com.springrain.erp.modules.sys.entity.Dict;

/**
 * MyBatis字典DAO接口
 * @author ThinkGem
 * @version 2013-8-23
 */
@MyBatisDao
public interface MyBatisDictDao {
	
    Dict get(String id);
    
    List<Dict> find(Dict dict);
    
}
