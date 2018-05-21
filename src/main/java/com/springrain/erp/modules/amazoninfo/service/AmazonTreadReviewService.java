package com.springrain.erp.modules.amazoninfo.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonTreadReviewAccountDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonTreadReviewDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonTreadReview;
import com.springrain.erp.modules.amazoninfo.entity.AmazonTreadReviewAccount;


@Component
@Transactional(readOnly = true)
public class AmazonTreadReviewService extends BaseService {

	@Autowired
	private AmazonTreadReviewDao amazonTreadReviewDao;
	
	@Autowired
	private AmazonTreadReviewAccountDao amazonTreadReviewAccountDao;
	
	public AmazonTreadReview get(Integer id){
		return amazonTreadReviewDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void saveAccount(List<AmazonTreadReviewAccount> amazonTreadReviewAccounts) {
		amazonTreadReviewAccountDao.save(amazonTreadReviewAccounts);
	}
	
	@Transactional(readOnly = false)
	public void saveAccount(AmazonTreadReviewAccount amazonTreadReviewAccount) {
		amazonTreadReviewAccountDao.save(amazonTreadReviewAccount);
	}
	
	@Transactional(readOnly = false)
	public void save(List<AmazonTreadReview> amazonTreadReviews) {
		amazonTreadReviewDao.save(amazonTreadReviews);
	}
	
	@Transactional(readOnly = false)
	public void save(AmazonTreadReview amazonTreadReview) {
		amazonTreadReviewDao.save(amazonTreadReview);
	}
	
	public Page<AmazonTreadReviewAccount> find(Page<AmazonTreadReviewAccount> page, AmazonTreadReviewAccount amazonTreadReviewAccount) {
		DetachedCriteria dc = amazonTreadReviewAccountDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(amazonTreadReviewAccount.getCountry())){
			dc.add(Restrictions.eq("country", amazonTreadReviewAccount.getCountry()));
		}
		if(StringUtils.isNotBlank(amazonTreadReviewAccount.getDelFlag())){
			dc.add(Restrictions.eq("delFlag",amazonTreadReviewAccount.getDelFlag()));
		}
		return amazonTreadReviewAccountDao.find(page, dc);
	}

	
	@Transactional(readOnly = false)
	public void updateDelFlag(Integer id) {
		String sql="update amazoninfo_tread_review_account set del_flag='1' where id=:p1 ";
		amazonTreadReviewAccountDao.updateBySql(sql, new Parameter(id));
	}
	
	public Page<AmazonTreadReview> find(Page<AmazonTreadReview> page, AmazonTreadReview amazonTreadReview) {
		DetachedCriteria dc = amazonTreadReviewDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(amazonTreadReview.getCountry())){
			dc.add(Restrictions.eq("country", amazonTreadReview.getCountry()));
		}
		if(amazonTreadReview.getCreateDate()!= null){
			dc.add(Restrictions.ge("createDate", amazonTreadReview.getCreateDate()));
		}
		if(amazonTreadReview.getEndDate() != null){
			dc.add(Restrictions.lt("createDate",DateUtils.addDays(amazonTreadReview.getEndDate(),1)));
		}
		return amazonTreadReviewDao.find(page, dc);
	}
	
	public Map<String,String> findAccountByCountry(String country,Integer num){
		Map<String,String>  map=Maps.newHashMap();
		String tempCountry=country;
		if(!"jp".equals(country)){
			tempCountry="com";
		}
		//String sql=" SELECT a.`login_name`,a.`password` FROM amazoninfo_tread_review_account a WHERE a.country=:p1 and a.`del_flag`='0' limit :p2 ";
		String sql=" SELECT a.`login_name`,a.`password`,COUNT(*) num FROM amazoninfo_tread_review_account a "+
				" LEFT JOIN (SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(a.account,',',b.help_topic_id+1),',',-1) account "+
				" FROM amazoninfo_tread_review a JOIN mysql.help_topic b  "+
				" ON b.help_topic_id < (LENGTH(a.account) - LENGTH(REPLACE(a.account,',',''))+1)  WHERE country=:p1 ) t ON t.account=a.login_name "+
				" WHERE a.country=:p2 AND a.`del_flag`='0' GROUP BY a.`login_name`,a.`password` ORDER BY num ASC  LIMIT :p3  ";
		List<Object[]> list=amazonTreadReviewDao.findBySql(sql,new Parameter(country,tempCountry,num));
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				map.put(obj[0].toString(), obj[1].toString());
			}
		}
		return map;
	}
	
	public Map<String,String> findAccountByCountry(String country,Integer num,List<String> accList){
		Map<String,String>  map=Maps.newHashMap();
		String sql="SELECT a.`login_name`,a.`password` FROM amazoninfo_tread_review_account a WHERE a.country=:p1 and login_name not in :p2 AND a.`del_flag`='0'   LIMIT :p3  ";
		List<Object[]> list=amazonTreadReviewDao.findBySql(sql,new Parameter(country,accList,num));
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				map.put(obj[0].toString(), obj[1].toString());
			}
		}
		return map;
	}
	
	public List<String> isExist(String country,String asin){
		String sql="SELECT DISTINCT SUBSTRING_INDEX(SUBSTRING_INDEX(a.account,',',b.help_topic_id+1),',',-1) account "+
                " FROM amazoninfo_tread_review a JOIN mysql.help_topic b  "+
                " ON b.help_topic_id < (LENGTH(a.account) - LENGTH(REPLACE(a.account,',',''))+1)  WHERE country=:p1  AND ASIN=:p2 ";
		return amazonTreadReviewDao.findBySql(sql,new Parameter(country,asin));
	}
	
	public List<String> findAccount(String country){
		 String sql="SELECT login_name FROM amazoninfo_tread_review_account WHERE del_flag='0' AND country=:p1";
		 return amazonTreadReviewDao.findBySql(sql,new Parameter(country));
	}
	
	@Transactional(readOnly = false)
	public void updateDelFlagByName(Set<String> name,String country) {
		String sql="update amazoninfo_tread_review_account set del_flag='1' where login_name in :p1 and country=:p2  ";
		amazonTreadReviewAccountDao.updateBySql(sql, new Parameter(name,country));
	}
	
}
