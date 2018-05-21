package com.springrain.erp.modules.amazoninfo.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.amazoninfo.dao.AmazonCatalogRankDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonCatalogRank;

@Component
@Transactional(readOnly = true)
public class AmazonCatalogRankService extends BaseService {

	@Autowired
	private AmazonCatalogRankDao amazonCatalogRankDao;
	
	public AmazonCatalogRank get(Integer id) {
		return amazonCatalogRankDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(List<AmazonCatalogRank> catalogRankList) {
		amazonCatalogRankDao.save(catalogRankList);
	}
	
	public Map<Date,List<AmazonCatalogRank>> getCatalogRank(String asin,String country,Date startDate,Date endDate){
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<Date,List<AmazonCatalogRank>> rsMap = Maps.newHashMap();
		DetachedCriteria dc = amazonCatalogRankDao.createDetachedCriteria();
		dc.add(Restrictions.eq("asin",asin));
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.ge("queryTime",startDate));
		dc.add(Restrictions.le("queryTime",endDate));
		List<AmazonCatalogRank> list=amazonCatalogRankDao.find(dc);
		for(AmazonCatalogRank rank:list){
			List<AmazonCatalogRank>  inList = null;
			if(rsMap.get(rank.getQueryTime())==null){
				inList=Lists.newArrayList();
			}else{
				inList = rsMap.get(rank.getQueryTime());
			}
			inList.add(rank);
			rsMap.put(rank.getQueryTime(), inList);
		}
		return rsMap;
		
	}
}
