package com.springrain.erp.modules.custom.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.custom.dao.CustomEmailTemplateDao;
import com.springrain.erp.modules.custom.entity.CustomEmailTemplate;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Component
@Transactional(readOnly = true)
public class CustomEmailTemplateService extends BaseService{
	
	@Autowired
	private CustomEmailTemplateDao customEmailTemplateDao;
	
	public CustomEmailTemplate get(Integer id) {
		return customEmailTemplateDao.get(id);
	}

	public Page<CustomEmailTemplate> find(Page<CustomEmailTemplate> page,
			CustomEmailTemplate customEmailTemplate,User user) {
		DetachedCriteria dc =customEmailTemplateDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		if(StringUtils.isEmpty(customEmailTemplate.getTemplateType())){
			customEmailTemplate.setTemplateType("2");
		}
		if(StringUtils.isNotEmpty(customEmailTemplate.getCountry())){
			dc.add(Restrictions.eq("country", customEmailTemplate.getCountry()));
		}
		
		if(StringUtils.isNotEmpty(customEmailTemplate.getTemplateName())){
			dc.add(Restrictions.or(
					 Restrictions.like("templateName","%"+customEmailTemplate.getTemplateName()+"%" ),
					 Restrictions.like("templateSubject","%"+customEmailTemplate.getTemplateName()+"%" ),
					 Restrictions.like("templateContent", "%"+customEmailTemplate.getTemplateName()+"%")
					));
		}
		
		if(customEmailTemplate.getTemplateType()==null){
		   dc.add(Restrictions.or(
			 Restrictions.eq("templateType", "0"),
			 Restrictions.eq("createBy", user),
			 Restrictions.in("role", user.getRoleList())
		   ));
		}
		
		if("0".equals(customEmailTemplate.getTemplateType())){
			dc.add(Restrictions.eq("templateType", "0"));
		} else if("1".equals(customEmailTemplate.getTemplateType())){
			dc.add(Restrictions.in("role", user.getRoleList()));
		}else if("2".equals(customEmailTemplate.getTemplateType())){
			dc.add(Restrictions.and(
			  Restrictions.eq("createBy", user),
			  Restrictions.isNull("role")
			));
		} else if(StringUtils.isNotEmpty(customEmailTemplate.getTemplateType())){
			dc.add(Restrictions.eq("templateType", customEmailTemplate.getTemplateType()));
		}
		
		page = customEmailTemplateDao.find(page, dc);
			
		return page;
	}
	
	@Transactional(readOnly = false)
	public void delete(Integer id){
		this.customEmailTemplateDao.deleteById(id);
	}

	@Transactional(readOnly = false)
	public void save(CustomEmailTemplate customEmailTemplate){
		customEmailTemplate.setDelFlag("0");
		customEmailTemplate.setCreateDate(new Date(System.currentTimeMillis()));
		customEmailTemplate.setLastUpdateDate(new Date(System.currentTimeMillis()));
		this.customEmailTemplateDao.save(customEmailTemplate);
	}
	
	/**
	 * 精密验证
	 * @param templateName
	 * @param templateType
	 * @param role
	 * @return
	 */
 /*
	public boolean isExistName(String templateName,String templateType,Role role) {
		DetachedCriteria dc =customEmailTemplateDao.createDetachedCriteria();
		User user = UserUtils.getUser();
		dc.add(Restrictions.eq("templateName", templateName));
		if("1".equals(templateType)){
		  dc.add(Restrictions.eq("role", role));
		}
		
		if("2".equals(templateType)){
			dc.add(Restrictions.eq("createBy", user));
		}
		
	    return this.customEmailTemplateDao.count(dc) > 0;
		
	}*/
	
	
	
	public boolean isExistName(String templateName) {
		DetachedCriteria dc =customEmailTemplateDao.createDetachedCriteria();
		dc.add(Restrictions.eq("templateName", templateName));	
		dc.add(Restrictions.eq("delFlag","0"));
	    return this.customEmailTemplateDao.count(dc) > 0;
		
	}

	public List<CustomEmailTemplate> find(String templateType, User user,Role role) {
		DetachedCriteria dc =customEmailTemplateDao.createDetachedCriteria();
		dc.add(Restrictions.eq("templateType", templateType));
		if("1".equals(templateType)){
			dc.add(Restrictions.eq("role", role));
		}else if("2".equals(templateType)){
			dc.add(Restrictions.eq("createBy", user));
		}		
		dc.add(Restrictions.eq("delFlag","0"));
	   return this.customEmailTemplateDao.find(dc);
		
	}
	/**
	 * 找到所有与用户相关的非系统模板.
	 * @param user
	 * @return
	 */
	public List<CustomEmailTemplate> find(User user) {
		DetachedCriteria dc =customEmailTemplateDao.createDetachedCriteria();
		dc.add(Restrictions.or(
		  Restrictions.and(Restrictions.eq("createBy", user)),
		  Restrictions.and(Restrictions.eq("templateType", "1"),Restrictions.in("role", user.getRoleList()))
		));
		dc.add(Restrictions.eq("delFlag","0"));
	   return this.customEmailTemplateDao.find(dc);
	}
	
	/**
	 * 找到所有系统模板
	 * @return
	 */
	public List<CustomEmailTemplate> find(){
		DetachedCriteria dc =customEmailTemplateDao.createDetachedCriteria();
		dc.add(Restrictions.eq("templateType", "0"));
		return this.customEmailTemplateDao.find(dc);
	}
	
	/**
	 * 根据对应类型找到售后模板
	 * @param templateType	模板类型
	 * @return
	 */
	public List<CustomEmailTemplate> findAfterSale(String templateType, String country){
		DetachedCriteria dc =customEmailTemplateDao.createDetachedCriteria();
		dc.add(Restrictions.eq("templateType", templateType));
		if (StringUtils.isNotEmpty(country)) {
			dc.add(Restrictions.eq("country", country));
		}
		dc.add(Restrictions.eq("delFlag","0"));
		//系统开发特殊权限
		if(!(UserUtils.hasPermission("it:special:permission") || UserUtils.getUser().isAdmin())){
			dc.add(Restrictions.eq("createBy", UserUtils.getUser()));
		}
		dc.addOrder(Order.desc("createDate"));
		return this.customEmailTemplateDao.find(dc);
	}
	
	
}
