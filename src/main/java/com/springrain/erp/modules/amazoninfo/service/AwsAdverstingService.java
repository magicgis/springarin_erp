package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.amazoninfo.dao.AwsAdvertisingDao;
import com.springrain.erp.modules.amazoninfo.entity.AwsAdversting;

/**
 * 
 * aws广告数据
 * @author computer
 *
 */
@Component
@Transactional(readOnly = true)
public class AwsAdverstingService extends BaseService{      
	@Autowired
	private AwsAdvertisingDao awsAdvertisingDao;
	
	
	@Transactional(readOnly=false)
	public void save(AwsAdversting ad){
		this.awsAdvertisingDao.save(ad);
	}
	
	@Transactional(readOnly=false)
	public void save(List<AwsAdversting> ad){
		this.awsAdvertisingDao.save(ad);
	}
	
	public AwsAdversting existAms(String name,String country,Date date){
		DetachedCriteria dc = awsAdvertisingDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country", country));
		dc.add(Restrictions.eq("campaignName",name));
		dc.add(Restrictions.eq("dataDate",date));
		List<AwsAdversting> awsAdversting=awsAdvertisingDao.find(dc);
		if(awsAdversting!=null&&awsAdversting.size()>0){
			return awsAdversting.get(0);
		}
		return null;
	}
	
	public Map<String, String> findModelBrandMap(){
		Map<String, String> rs = Maps.newHashMap();
		String sql = "SELECT t.`brand`,t.`model` FROM psi_product t WHERE t.`del_flag`='0' AND t.`model` IS NOT NULL AND t.`brand` IS NOT NULL";
		List<Object[]> list = awsAdvertisingDao.findBySql(sql);
		for (Object[] obj : list) {
			rs.put(obj[1].toString(), obj[0].toString());
		}
		return rs;
	}
	
	public Map<String, String> findModelBrandColorMap(){
		Map<String, String> rs = Maps.newHashMap();
		String sql = "SELECT t.`brand`,t.`model`,t.color FROM psi_product t WHERE t.`del_flag`='0' AND t.`model` IS NOT NULL AND t.`brand` IS NOT NULL";
		List<Object[]> list = awsAdvertisingDao.findBySql(sql);
		for (Object[] obj : list) {
			rs.put(obj[1].toString(), obj[0].toString()+";;;"+(obj[2]==null?"":obj[2].toString()));
		}
		return rs;
	}
}
