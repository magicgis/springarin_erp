package com.springrain.erp.modules.custom.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.modules.custom.entity.CustomEmail;
import com.springrain.erp.modules.sys.entity.Menu;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.DictUtils;

/**
 * 邮件DAO接口
 * @author tim
 * @version 2014-04-30
 */
@Repository
public class CustomEmailDao extends BaseDao<CustomEmail> {
	
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, String>> findCount(List<Criterion> ses,Date start ,Date end) {
		DetachedCriteria detachedCriteria = this.createDetachedCriteria(Restrictions.ne("state","4"));
		for (Criterion simpleExpression : ses) {
			detachedCriteria.add(simpleExpression);
		}
		detachedCriteria.add(Restrictions.ge("createDate",start));
		detachedCriteria.add(Restrictions.lt("createDate",end));
		
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());	
		ProjectionList projection = Projections.projectionList().add(Projections.property("masterBy"))
				.add(Projections.rowCount()).add(Projections.groupProperty("masterBy"));
		criteria.setProjection(projection);
		List<Object[]> list = criteria.list();
		
		Map<String,Map<String, String>> listdata = Maps.newLinkedHashMap();
		for (Object[] objs : list) {
			User user = (User)objs[0];
			Map<String, String> temp = Maps.newHashMap();
			temp.put("noFor", objs[1].toString());
			temp.put("user", getUserName(user));
			listdata.put(user.getId(),temp);
		}
		detachedCriteria = this.createDetachedCriteria(Restrictions.eq("state","2"));
		for (Criterion simpleExpression : ses) {
			detachedCriteria.add(simpleExpression);
		}
		detachedCriteria.add(Restrictions.ge("createDate",start));
		detachedCriteria.add(Restrictions.lt("createDate",end));
		detachedCriteria.add(Restrictions.ge("endDate",start));
		detachedCriteria.add(Restrictions.lt("endDate",end));
		projection = Projections.projectionList().add(Projections.property("masterBy"))
				.add(Projections.rowCount()).add(Projections.sqlProjection("avg(UNIX_TIMESTAMP(end_date)-UNIX_TIMESTAMP(answer_date)) as avg", 
				new String[]{"avg"}, new Type[]{LongType.INSTANCE})).add(Projections.groupProperty("masterBy"))
				.add(Projections.sqlProjection("avg(UNIX_TIMESTAMP(answer_date)-UNIX_TIMESTAMP(create_date)) as resp", new String[]{"resp"}, new Type[]{LongType.INSTANCE}));
		criteria = detachedCriteria.getExecutableCriteria(getSession());	
		criteria.setProjection(projection);
		
		list = criteria.list();
		
		for (Object[] objs : list) {
			User user = (User)objs[0];
			String key = user.getId();
			if(listdata.get(key)!=null){
				listdata.get(key).put("two", objs[1].toString());
				listdata.get(key).put("avg", objs[2]==null?"0":objs[2].toString());
				listdata.get(key).put("resp", objs[4]==null?"0":objs[4].toString());
			}else{
				Map<String, String> temp = Maps.newHashMap();
				temp.put("two", objs[1].toString());
				temp.put("avg", objs[2].toString());
				temp.put("user", getUserName(user));
				temp.put("resp", objs[4].toString());
				listdata.put(key,temp);
			}
		}
		
		detachedCriteria = this.createDetachedCriteria(Restrictions.eq("state","4"));
		for (Criterion simpleExpression : ses) {
			detachedCriteria.add(simpleExpression);
		}
		detachedCriteria.add(Restrictions.ge("createDate",start));
		detachedCriteria.add(Restrictions.lt("createDate",end));
		projection = Projections.projectionList().add(Projections.property("masterBy"))
				.add(Projections.rowCount()).add(Projections.groupProperty("masterBy"));
		criteria = detachedCriteria.getExecutableCriteria(getSession());	
		criteria.setProjection(projection);
		list = criteria.list();
		for (Object[] objs : list) {
			User user = (User)objs[0];
			String key = user.getId();
			if(listdata.get(key)!=null){
				listdata.get(key).put("for", objs[1].toString());
			}else{
				Map<String, String> temp = Maps.newHashMap();
				temp.put("for", objs[1].toString());
				temp.put("user",  getUserName(user));
				listdata.put(key,temp);
			}
		}
		return listdata;
	}
	
	private String getUserName(User user){
		if(user==null){
			return "";
		}
		Set<Role> roles = user.getRoleList();
		StringBuffer sbBuffer = new StringBuffer("");
		for (Role role : roles) {
			List<Menu> menuList=role.getMenuList();
			for (Menu menu : menuList) {
				String permission=menu.getPermission();
				if(StringUtils.isNotBlank(permission)&&permission.contains("custom:service:")&&!sbBuffer.toString().contains(DictUtils.getDictLabel(permission.split(":")[2], "platform","其他")+"客服,")){
					sbBuffer.append(DictUtils.getDictLabel(permission.split(":")[2], "platform","其他")+"客服,");
				}
			}	
		}
		String suff = sbBuffer.toString();
		if(suff.length()>0){
			suff = "["+suff.substring(0,suff.length()-1)+"]";
		}
		return user.getName()+suff;
	}
	
	public Set<String> findNoAutoReply(){
		Set<String> rs = Sets.newHashSet();
		List<Object> temp = findBySql("SELECT DISTINCT revert_email FROM custom_email_manager WHERE state = '4' AND revert_email not LIKE '%@marketplace.amazon%'");
		List<Object> temp1 = findBySql("SELECT DISTINCT revert_email FROM custom_email_manager WHERE state = '2' AND revert_email not LIKE '%@marketplace.amazon%'");
		String tempStr = temp1.toString().replace("]", ",");
		for (Object object : temp) {
			String email = object.toString();
			if(!tempStr.contains(email+",")){
				rs.add(email);
			}
		}
		return rs;
	}
	
}
