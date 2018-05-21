package com.springrain.erp.modules.amazoninfo.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.FollowAsinDao;
import com.springrain.erp.modules.amazoninfo.entity.FollowAsin;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Component
@Transactional(readOnly = true)
public class FollowAsinService extends BaseService {

	@Autowired
	private FollowAsinDao FollowAsinDao;

	public Page<FollowAsin> find(Page<FollowAsin> page, FollowAsin FollowAsin,String isCheck) {
		DetachedCriteria dc = FollowAsinDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(FollowAsin.getProductName())){
			dc.add(Restrictions.eq("productName", FollowAsin.getProductName()));
		}
		if(StringUtils.isNotEmpty(FollowAsin.getCountry())){
			dc.add(Restrictions.eq("country", FollowAsin.getCountry()));
		}
		if(FollowAsin.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate", FollowAsin.getCreateDate()));
		}
		if (FollowAsin.getEndDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(FollowAsin.getEndDate(),1)));
		}
		if("1".equals(isCheck)){
			dc.add(Restrictions.eq("createUser.id",UserUtils.getUser().getId()));
		}
		if(StringUtils.isNotEmpty(FollowAsin.getState())){
			dc.add(Restrictions.eq("state", FollowAsin.getState()));
		}
		page.setOrderBy("id desc");
		return FollowAsinDao.find(page, dc);
	}
	
	
	public FollowAsin get(Integer id) {
		return FollowAsinDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(List<FollowAsin> FollowAsin) {
		FollowAsinDao.save(FollowAsin);
	}
	
	@Transactional(readOnly = false)
	public void save(FollowAsin FollowAsin) {
		if(FollowAsin.getId()==null){
			FollowAsin.setCreateDate(new Date());
			FollowAsin.setCreateUser(UserUtils.getUser());
			FollowAsin.setState("1");
		}
		FollowAsinDao.save(FollowAsin);
	}
	
	public List<FollowAsin> findAllByE() {
		DetachedCriteria dc = FollowAsinDao.createDetachedCriteria();
		dc.add(Restrictions.eq("state","1"));
		List<FollowAsin> rs =  FollowAsinDao.find(dc);
		for (FollowAsin FollowAsin : rs) {
			Hibernate.initialize(FollowAsin.getReviews());
		}
		return rs;
	}
	
	//查出需要扫描库存的国家和asin   
	public Map<String,Set<String>>  getAsinAndCountry(Set<String> selfAsins){
		Map<String,Set<String>> resMap = Maps.newHashMap();
		String sql ="SELECT a.`asin`,a.`country` FROM amazoninfo_follow_asin AS a WHERE a.asin NOT IN :p1 AND a.`state`='1'";
		List<Object[]> list = this.FollowAsinDao.findBySql(sql,new Parameter(selfAsins));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				Set<String> set =Sets.newHashSet();
				String asin =obj[0].toString();
				String country=obj[1].toString();
				if(resMap.get(country)==null){
					set = Sets.newHashSet();
				}else{
					set=resMap.get(country);
				}
				set.add(asin);
				resMap.put(country, set);
			}
		}
	   return resMap;
	}
	
	//查询当天已经扫描过库存的asin
	public Map<String,Set<String>>  getAsinAndCountryToday(Date date){
		Map<String,Set<String>> resMap = Maps.newHashMap();
		String sql ="SELECT a.`asin`,a.`country` FROM amazoninfo_follow_stock AS a WHERE a.`data_date`=:p1";
		List<Object[]> list = this.FollowAsinDao.findBySql(sql,new Parameter(date));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				Set<String> set =Sets.newHashSet();
				String asin =obj[0].toString();
				String country=obj[1].toString();
				if(resMap.get(country)==null){
					set = Sets.newHashSet();
				}else{
					set=resMap.get(country);
				}
				set.add(asin);
				resMap.put(country, set);
			}
		}
	   return resMap;
	}
	
	
}
