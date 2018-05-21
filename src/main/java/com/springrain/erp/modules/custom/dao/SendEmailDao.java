package com.springrain.erp.modules.custom.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.sys.entity.Menu;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.DictUtils;

/**
 * 发送邮件DAO接口
 * @author tim
 * @version 2014-05-13
 */
@Repository
public class SendEmailDao extends BaseDao<SendEmail> {
	
	//总回复多少封
	public Map<String,Map<String, String>> count(List<SimpleExpression> ses){
		
		DetachedCriteria detachedCriteria = this.createDetachedCriteria(Restrictions.eq("sendFlag","1"),Restrictions.isNotNull("createBy"));
		for (SimpleExpression simpleExpression : ses) {
			detachedCriteria.add(simpleExpression);
		}
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());	
		ProjectionList projection = Projections.projectionList().add(Projections.property("createBy"))
				.add(Projections.rowCount()).add(Projections.groupProperty("createBy"));
		criteria.setProjection(projection);
		List<Object[]> list = criteria.list();
		Map<String,Map<String, String>> listdata = Maps.newLinkedHashMap();
		for (Object[] objs : list) {
			User user = (User)objs[0];
			String key = user.getId();
			Map<String, String> temp = Maps.newHashMap();
			temp.put("sendEmail", objs[1].toString());
			temp.put("user", getUserName(user));
			listdata.put(key,temp);
		}
		return listdata;
	}
	
	
	private String getUserName(User user){
		if(user==null){
			return "";
		}
		Set<Role> roles = user.getRoleList();
		StringBuffer stringBuffer = new StringBuffer("");
		for (Role role : roles) {
			List<Menu> menuList=role.getMenuList();
			for (Menu menu : menuList) {
				String permission=menu.getPermission();
				if(StringUtils.isNotBlank(permission)&&permission.contains("custom:service:")&&!stringBuffer.toString().contains(DictUtils.getDictLabel(permission.split(":")[2], "platform","其他")+"客服,")){
					stringBuffer.append(DictUtils.getDictLabel(permission.split(":")[2], "platform","其他")+"客服,");
				}
			}	
		}
		String suff = stringBuffer.toString();
		if(suff.length()>0){
			suff = "["+suff.substring(0,suff.length()-1)+"]";
		}
		return user.getName()+suff;
	}
}
