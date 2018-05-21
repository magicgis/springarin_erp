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
import com.springrain.erp.modules.amazoninfo.dao.OpponentAsinDao;
import com.springrain.erp.modules.amazoninfo.entity.OpponentAsin;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Component
@Transactional(readOnly = true)
public class OpponentAsinService extends BaseService {

	@Autowired
	private OpponentAsinDao OpponentAsinDao;

	public Page<OpponentAsin> find(Page<OpponentAsin> page, OpponentAsin OpponentAsin,String isCheck) {
		DetachedCriteria dc = OpponentAsinDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(OpponentAsin.getProductName())){
			dc.add(Restrictions.eq("productName", OpponentAsin.getProductName()));
		}
		if(StringUtils.isNotEmpty(OpponentAsin.getCountry())){
			dc.add(Restrictions.eq("country", OpponentAsin.getCountry()));
		}
		if(OpponentAsin.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate", OpponentAsin.getCreateDate()));
		}
		if (OpponentAsin.getEndDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(OpponentAsin.getEndDate(),1)));
		}
		if("1".equals(isCheck)){
			dc.add(Restrictions.eq("createUser.id",UserUtils.getUser().getId()));
		}
		if(StringUtils.isNotEmpty(OpponentAsin.getState())){
			dc.add(Restrictions.eq("state", OpponentAsin.getState()));
		}
		page.setOrderBy("id desc");
		return OpponentAsinDao.find(page, dc);
	}
	
	
	public OpponentAsin get(Integer id) {
		return OpponentAsinDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(List<OpponentAsin> OpponentAsin) {
		OpponentAsinDao.save(OpponentAsin);
	}
	
	@Transactional(readOnly = false)
	public void save(OpponentAsin OpponentAsin) {
		if(OpponentAsin.getId()==null){
			OpponentAsin.setCreateDate(new Date());
			OpponentAsin.setCreateUser(UserUtils.getUser());
			OpponentAsin.setState("1");
		}
		OpponentAsinDao.save(OpponentAsin);
	}
	
	public List<OpponentAsin> findAllByE() {
		DetachedCriteria dc = OpponentAsinDao.createDetachedCriteria();
		dc.add(Restrictions.eq("state","1"));
		List<OpponentAsin> rs =  OpponentAsinDao.find(dc);
		for (OpponentAsin OpponentAsin : rs) {
			Hibernate.initialize(OpponentAsin.getReviews());
		}
		return rs;
	}
	
	//查出需要扫描库存的国家和asin   
	public Map<String,Set<String>>  getAsinAndCountry(Set<String> selfAsins){
		Map<String,Set<String>> resMap = Maps.newHashMap();
		String sql ="SELECT a.`asin`,a.`country` FROM amazoninfo_opponent_asin AS a WHERE a.asin NOT IN :p1 AND a.`state`='1'";
		List<Object[]> list = this.OpponentAsinDao.findBySql(sql,new Parameter(selfAsins));
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
		String sql ="SELECT a.`asin`,a.`country` FROM amazoninfo_opponent_stock AS a WHERE a.`data_date`=:p1";
		List<Object[]> list = this.OpponentAsinDao.findBySql(sql,new Parameter(date));
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
