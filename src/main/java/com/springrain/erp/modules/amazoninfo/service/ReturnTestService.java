/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.ReturnTestDao;
import com.springrain.erp.modules.amazoninfo.entity.ReturnTest;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 测试检测Service
 * @author Michael
 * @version 2015-08-24
 */
@Component
@Transactional(readOnly = true)
public class ReturnTestService extends BaseService {
	@Autowired
	private ReturnTestDao returnTestDao;
	public ReturnTest get(Integer id) {
		return returnTestDao.get(id);
	}
	
	public Page<ReturnTest> find(Page<ReturnTest> page, ReturnTest returnTest) {
		DetachedCriteria dc = returnTestDao.createDetachedCriteria();
		if(returnTest.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate", returnTest.getCreateDate()));
		}
		if (returnTest.getUpdateDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(returnTest.getUpdateDate(),1)));
		}
		page.setOrderBy("id desc");
		return returnTestDao.find(page, dc);
	}
	
	public List<ReturnTest> find() {
		DetachedCriteria dc = returnTestDao.createDetachedCriteria();
		dc.add(Restrictions.eq("testSta","0"));
		return returnTestDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void save(ReturnTest returnTest) {
		if(returnTest.getId()==null){
			returnTest.setCreateDate(new Date());
			returnTest.setCreateUser(UserUtils.getUser());
		}else{
			returnTest.setUpdateDate(new Date());
			returnTest.setCreateUser(UserUtils.getUser());
		}
		returnTestDao.save(returnTest);
	}
	
	//更新状态为已出库，并设置出库单号
	public void updateStaAndStockInNo(Integer id,String stockInNo){
		String sql="UPDATE amazoninfo_return_test AS a SET a.`stock_in_no`=:p2,a.`test_sta`='2' AND a.`id`=:p1";
		this.returnTestDao.updateBySql(sql, new Parameter(id,stockInNo));
	}
	
}
