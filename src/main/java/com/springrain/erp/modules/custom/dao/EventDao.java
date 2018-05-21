/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.custom.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.type.FloatType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 事件DAO接口
 * @author tim
 * @version 2014-05-21
 */
@Repository
public class EventDao extends BaseDao<Event> {
	
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, String>> findCount(List<SimpleExpression> ses,Date start,Date end) {
		DetachedCriteria detachedCriteria = this.createDetachedCriteria(Restrictions.ne("state","4"));
		end.setHours(23);
		end.setMinutes(59);
		ses.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		for (SimpleExpression simpleExpression : ses) {
			detachedCriteria.add(simpleExpression);
		}
		detachedCriteria.add(Restrictions.or(Restrictions.and(Restrictions.ge("createDate",start),Restrictions.lt("createDate",end)),Restrictions.and(Restrictions.eq("state","2"),Restrictions.ge("endDate",start),Restrictions.lt("endDate",end))));
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());	
		ProjectionList projection = Projections.projectionList().add(Projections.property("masterBy"))
				.add(Projections.rowCount()).add(Projections.groupProperty("masterBy"));
		criteria.setProjection(projection);
		List<Object[]> list = criteria.list();
		Map<String,Map<String, String>> listdata = Maps.newLinkedHashMap();
		for (Object[] objs : list) {
			User user = (User)objs[0];
			if(user!=null){
				Map<String, String> temp = Maps.newHashMap();
				temp.put("noFor", objs[1].toString());
				temp.put("user", user.getName());
				listdata.put(user.getId(),temp);
			}
		}
		
		detachedCriteria = this.createDetachedCriteria(Restrictions.eq("state","2"),Restrictions.ne("result","Unchangeable"),Restrictions.not(Restrictions.like("result","%unchangeable%")),Restrictions.not(Restrictions.like("result","%无法更改%")));
		for (SimpleExpression simpleExpression : ses) {
			detachedCriteria.add(simpleExpression);
		}
		detachedCriteria.add(Restrictions.ge("endDate",start));
		detachedCriteria.add(Restrictions.lt("endDate",end));
		projection = Projections.projectionList().add(Projections.property("masterBy"))
				.add(Projections.rowCount()).add(Projections.sqlProjection("avg(UNIX_TIMESTAMP(end_date)-UNIX_TIMESTAMP(case when answer_date is null then end_date else answer_date end)) as avg", 
						new String[]{"avg"}, new Type[]{LongType.INSTANCE})).add(Projections.groupProperty("masterBy"));
		criteria = detachedCriteria.getExecutableCriteria(getSession());	
		criteria.setProjection(projection);
		
		list = criteria.list();
		for (Object[] objs : list) {
			User user = (User)objs[0];
			if(user==null){
				continue;
			}
			String key = user.getId();
			if(listdata.get(key)!=null){
				listdata.get(key).put("two", objs[1].toString());
				listdata.get(key).put("avg", objs[2]==null?"0":objs[2].toString());
			}else{
				Map<String, String> temp = Maps.newHashMap();
				temp.put("two", objs[1].toString());
				temp.put("avg", objs[2].toString());
				temp.put("user", user.getName());
				listdata.put(key,temp);
			}
			detachedCriteria = this.createDetachedCriteria(Restrictions.eq("state","2"),Restrictions.eq("masterBy",user));//,Restrictions.ne("result","Unchangeable"),Restrictions.not(Restrictions.like("result","%unchangeable%")),Restrictions.not(Restrictions.like("result","%无法更改%")));
			for (SimpleExpression simpleExpression : ses) {
				detachedCriteria.add(simpleExpression);
			}
			
			String tempNoFor = listdata.get(key).get("noFor");
			
			detachedCriteria.add(Restrictions.ge("endDate",start));
			detachedCriteria.add(Restrictions.lt("endDate",end));
			projection = Projections.projectionList().add(Projections.property("result")).add(Projections.rowCount())
					.add(Projections.sqlProjection((tempNoFor==null?"1":"count(*)*100/"+tempNoFor)+" as avg", 
							new String[]{"avg"}, new Type[]{FloatType.INSTANCE})).add(Projections.groupProperty("result"));
			criteria = detachedCriteria.getExecutableCriteria(getSession());	
			criteria.setProjection(projection);
			List<Object[]> templist = criteria.list();
			String result = "";
			for (Object[] objects : templist) {
				DetachedCriteria temp = this.createDetachedCriteria(Restrictions.eq("state","2"),Restrictions.eq("masterBy",user),Restrictions.eq("result", objects[0]));
				temp.add(Restrictions.ge("endDate",start));
				temp.add(Restrictions.lt("endDate",end));
				for (SimpleExpression simpleExpression : ses) {
					temp.add(simpleExpression);
				}
				Criteria ctemp = temp.getExecutableCriteria(getSession());	
				ctemp.setProjection(Projections.property("id"));
				List<Integer> ids = ctemp.list();
				StringBuffer stringBuffer = new StringBuffer("");
				for (Integer tid : ids) {
					stringBuffer.append("<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/event/form?id="+tid+"'>SPR-"+tid+"</a>,");
				}
				String id = stringBuffer.toString();
				if(ids.size()>0){
					id = id.substring(0,id.length()-1);
				}
				result +=("处理结果为\""+objects[0]+"\"的有"+objects[1]+"条,占"+objects[2]+"%,事件id为:"+id+"<br/>");
			}
			listdata.get(key).put("result",result);
		}
		
		detachedCriteria = this.createDetachedCriteria(Restrictions.eq("state","4"));
		for (SimpleExpression simpleExpression : ses) {
			detachedCriteria.add(simpleExpression);
		}
		detachedCriteria.add(Restrictions.ge("endDate",start));
		detachedCriteria.add(Restrictions.lt("endDate",end));
		projection = Projections.projectionList().add(Projections.property("masterBy"))
				.add(Projections.rowCount()).add(Projections.groupProperty("masterBy"));
		criteria = detachedCriteria.getExecutableCriteria(getSession());	
		criteria.setProjection(projection);
		list = criteria.list();
		for (Object[] objs : list) {
			User user = (User)objs[0];
			if(user!=null){
				String key = user.getId();
				if(listdata.get(key)!=null){
					listdata.get(key).put("for", objs[1].toString());
				}else{
					Map<String, String> temp = Maps.newHashMap();
					temp.put("for", objs[1].toString());
					temp.put("user", user.getName());
					listdata.put(key,temp);
				}
			}
		}
		return listdata;
	}
	
}
