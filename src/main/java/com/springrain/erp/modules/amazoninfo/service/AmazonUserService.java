package com.springrain.erp.modules.amazoninfo.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonUserDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonLoginLogDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonUser;
import com.springrain.erp.modules.amazoninfo.entity.AmazonLoginLog;
import com.springrain.erp.modules.sys.dao.UserDao;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 亚马逊后台账号信息Service
 */
@Component
@Transactional(readOnly = true)
public class AmazonUserService extends BaseService {

	@Autowired
	private AmazonUserDao amazonUserDao;
	
	@Autowired
	private AmazonLoginLogDao amazonLoginLogDao;
	
	@Autowired
	private UserDao userDao;
	
	
	public AmazonUser get(Integer id) {
		return amazonUserDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(AmazonUser amazonUser) {
		amazonUserDao.save(amazonUser);
	}

	@Transactional(readOnly = false)
	public void saveLog(AmazonLoginLog log) {
		amazonLoginLogDao.save(log);
	}
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		amazonUserDao.deleteById(id);
	}
	
	/**
	 * 根据erp账户查找对应的亚马逊账户
	 * @return
	 */
	public AmazonUser getAmazonUserByErpInfo(String country, String roleNames, String ip){
		String temp = "";
		if ("23.239.0.246".equals(ip)) {
			temp = " and t.ip='"+ip+"' ";
		}
		if (country.contains("ams")) {
			String sql = "SELECT t.`account`,t.`password`,t.`role_name` FROM `amazon_user` t WHERE t.`country` = :p1 "+temp+" ORDER BY t.`sort` ";
			List<Object[]> list = amazonUserDao.findBySql(sql, new Parameter(country));
			for (Object[] obj : list) {
				String roles = obj[2].toString();
				for (String roleName : roles.split(",")) {
					if (roleNames.contains(roleName)) {
						AmazonUser amazonUser = new AmazonUser();
						amazonUser.setAccount(obj[0].toString());
						amazonUser.setPassword(obj[1].toString());
						return amazonUser;
					}
				}
			}
		}
		if (country.contains("vendor")) {
			String sql = "SELECT t.`account`,t.`password`,t.`role_name` FROM `amazon_user` t WHERE t.`country` = :p1 "+temp+" ORDER BY t.`sort` ";
			List<Object[]> list = amazonUserDao.findBySql(sql, new Parameter(country));
			for (Object[] obj : list) {
				String roles = obj[2].toString();
				for (String roleName : roles.split(",")) {
					if (roleNames.contains(roleName)) {
						AmazonUser amazonUser = new AmazonUser();
						amazonUser.setAccount(obj[0].toString());
						amazonUser.setPassword(obj[1].toString());
						return amazonUser;
					}
				}
			}
		}
		country = country.replace("ams.", "").replace("vendor.", "");
		if ("de,fr,uk,it,es".contains(country)) {
			country = "eu";
		}
		//排除广告账号,广告账号在上一步处理,没有广告账号在查找普通账号
		String sql = "SELECT t.`account`,t.`password`,t.`role_name` FROM `amazon_user` t WHERE t.`country` LIKE :p1 " +
				" and t.`country` NOT LIKE 'ams%' and t.`country` NOT LIKE 'vendor%' "+temp+" ORDER BY t.`sort` ";
		List<Object[]> list = amazonUserDao.findBySql(sql, new Parameter("%"+country+"%"));
		for (Object[] obj : list) {
			String roles = obj[2].toString();
			for (String roleName : roles.split(",")) {
				if (roleNames.contains(roleName)) {
					AmazonUser amazonUser = new AmazonUser();
					amazonUser.setAccount(obj[0].toString());
					amazonUser.setPassword(obj[1].toString());
					return amazonUser;
				}
			}
		}
		return null;
	}

	public List<AmazonUser> findAll() {
		DetachedCriteria dc = amazonUserDao.createDetachedCriteria();
		//页面隐藏机器人账号
		dc.add(Restrictions.sqlRestriction("account not like '%noreply%'"));
		return amazonUserDao.find(dc);
	}
	
	//分页查询日志记录
	public Page<AmazonLoginLog> findLog(Page<AmazonLoginLog> page, AmazonLoginLog amazonUserLog) {
		DetachedCriteria dc = amazonLoginLogDao.createDetachedCriteria();
		if(amazonUserLog.getDataDate() != null){
			dc.add(Restrictions.ge("dataDate", amazonUserLog.getDataDate()));
		}
		if(amazonUserLog.getEndDate() != null){
			dc.add(Restrictions.le("dataDate", DateUtils.addDays(amazonUserLog.getEndDate(), 1)));
		}
		if (amazonUserLog.getUser() != null && StringUtils.isNotEmpty(amazonUserLog.getUser().getId())){
			dc.add(Restrictions.eq("user", amazonUserLog.getUser()));
		}
		if (StringUtils.isNotEmpty(amazonUserLog.getCountry())){
			dc.add(Restrictions.eq("country", amazonUserLog.getCountry()));
		}
		if (StringUtils.isEmpty(page.getOrderBy())){
			dc.addOrder(Order.desc("dataDate"));
		}
		return amazonLoginLogDao.find(page, dc);
	}
	
	public List<User> findAllUsers() {
		return userDao.findAllList();
	}
	
}
