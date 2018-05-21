package com.springrain.erp.modules.amazoninfo.service;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonPostsFeedDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonPostsRelationshipFeedDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsChange;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsDetail;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsFeed;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsRelationshipFeed;
import com.springrain.erp.modules.sys.entity.User;

@Component
@Transactional(readOnly = true)
public class AmazonPostsFeedService extends BaseService {

	@Autowired
	private AmazonPostsFeedDao amazonPostsFeedDao;
	
	@Autowired
	private AmazonPostsRelationshipFeedDao amazonPostsRelationshipFeedDao;
	
	public AmazonPostsFeed get(Integer id) {
		return amazonPostsFeedDao.get(id);
	}
	
	public AmazonPostsRelationshipFeed getAmazonPostsRelationshipFeed(Integer id) {
		return amazonPostsRelationshipFeedDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(AmazonPostsFeed amazonPostsFeed) {
		amazonPostsFeedDao.save(amazonPostsFeed);
	}
	
	@Transactional(readOnly = false)
	public void save(AmazonPostsRelationshipFeed amazonPostsRelationshipFeed) {
		amazonPostsRelationshipFeedDao.save(amazonPostsRelationshipFeed);
	}
	
	
	public List<AmazonPostsFeed> find() {
		DetachedCriteria dc = amazonPostsFeedDao.createDetachedCriteria();
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		dc.add(Restrictions.ge("createDate",today));
		dc.add(Restrictions.lt("createDate",DateUtils.addDays(today,1)));
		dc.add(Restrictions.eq("createUser",new User("1")));
		List<AmazonPostsFeed> list= amazonPostsFeedDao.find(dc);
		for (AmazonPostsFeed amazonPostsFeed : list) {
			Hibernate.initialize(amazonPostsFeed.getItems());
		}
		return list;
	}
	
	public Map<String,List<AmazonPostsFeed>> getByCountry() {
		DetachedCriteria dc = amazonPostsFeedDao.createDetachedCriteria();
		Map<String,List<AmazonPostsFeed>> map=Maps.newHashMap();
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		dc.add(Restrictions.ge("createDate",today));
		dc.add(Restrictions.lt("createDate",DateUtils.addDays(today,1)));
		dc.add(Restrictions.eq("createUser",new User("1")));
		List<AmazonPostsFeed> list= amazonPostsFeedDao.find(dc);
		for (AmazonPostsFeed amazonPostsFeed : list) {
			Hibernate.initialize(amazonPostsFeed.getItems());
		}
		for (AmazonPostsFeed amazonPostsFeed : list) {
			List<AmazonPostsFeed> temp =map.get(amazonPostsFeed.getCountry());
			if(temp==null){
				temp=Lists.newArrayList();
				map.put(amazonPostsFeed.getCountry(), temp);
			}
			temp.add(amazonPostsFeed);
		}
		return map;
	}
	
	
	public Map<String,Map<String,List<AmazonPostsFeed>>> findByCountry() {
		Map<String,Map<String,List<AmazonPostsFeed>>> map=Maps.newHashMap();
		DetachedCriteria dc = amazonPostsFeedDao.createDetachedCriteria();
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		dc.add(Restrictions.ge("createDate",today));
		dc.add(Restrictions.lt("createDate",DateUtils.addDays(today,1)));
		dc.add(Restrictions.eq("createUser",new User("1")));
		List<AmazonPostsFeed> list= amazonPostsFeedDao.find(dc);
		for (AmazonPostsFeed amazonPostsFeed : list) {
			Hibernate.initialize(amazonPostsFeed.getItems());
		}
		for (AmazonPostsFeed feed : list) {
			Map<String,List<AmazonPostsFeed>> temp=map.get(feed.getCountry());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(feed.getCountry(), temp);
			}
			for (AmazonPostsChange change : feed.getItems()) {
				List<AmazonPostsFeed> feedList=temp.get(change.getAsin());
				if(feedList==null){
					feedList=Lists.newArrayList();
					temp.put(change.getAsin(), feedList);
				}
				feedList.add(feed);
			}
		}
		return map;
	}
	
	public Map<String,List<AmazonPostsFeed>> findByCountry(String country) {
		Map<String,List<AmazonPostsFeed>> map=Maps.newHashMap();
		DetachedCriteria dc = amazonPostsFeedDao.createDetachedCriteria();
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		dc.add(Restrictions.ge("createDate",today));
		dc.add(Restrictions.lt("createDate",DateUtils.addDays(today,1)));
		dc.add(Restrictions.eq("createUser",new User("1")));
		dc.add(Restrictions.eq("country",country));
		
		dc.createAlias("this.items", "items");
		dc.add(Restrictions.eq("items.isFba", "0"));
		dc.add(Restrictions.eq("items.quantity",0));
		
		List<AmazonPostsFeed> list= amazonPostsFeedDao.find(dc);
		for (AmazonPostsFeed amazonPostsFeed : list) {
			Hibernate.initialize(amazonPostsFeed.getItems());
		}
		for (AmazonPostsFeed feed : list) {
			for (AmazonPostsChange change : feed.getItems()) {
				List<AmazonPostsFeed> feedList=map.get(change.getAsin());
				if(feedList==null){
					feedList=Lists.newArrayList();
					map.put(change.getAsin(), feedList);
				}
				feedList.add(feed);
			}
		}
		return map;
	}
	
	
	public Page<AmazonPostsFeed> find(Page<AmazonPostsFeed> page,AmazonPostsFeed amazonPostsFeed) {
		DetachedCriteria dc = amazonPostsFeedDao.createDetachedCriteria();
		
		if (amazonPostsFeed.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",amazonPostsFeed.getCreateDate()));
		}
		if (amazonPostsFeed.getEndDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(amazonPostsFeed.getEndDate(),1)));
		}
		if (amazonPostsFeed.getCreateUser()!=null && StringUtils.isNotEmpty(amazonPostsFeed.getCreateUser().getId())){
			dc.add(Restrictions.eq("createUser", amazonPostsFeed.getCreateUser()));
		}
		if(StringUtils.isNotEmpty(amazonPostsFeed.getCountry())){
			dc.add(Restrictions.eq("country", amazonPostsFeed.getCountry()));
		}
		if(StringUtils.isNotEmpty(amazonPostsFeed.getOperateType())){
			dc.add(Restrictions.eq("operateType", amazonPostsFeed.getOperateType()));
		}
		if(StringUtils.isNotEmpty(amazonPostsFeed.getSku())){
			dc.createAlias("this.items", "items");
			dc.add(Restrictions.like("items.sku", "%"+amazonPostsFeed.getSku()+"%"));
		}
		return amazonPostsFeedDao.find2(page, dc);
	}
	
	public Page<AmazonPostsRelationshipFeed> find(Page<AmazonPostsRelationshipFeed> page,AmazonPostsRelationshipFeed amazonPostsRelationshipFeed) {
		DetachedCriteria dc = amazonPostsRelationshipFeedDao.createDetachedCriteria();
		
		if (amazonPostsRelationshipFeed.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",amazonPostsRelationshipFeed.getCreateDate()));
		}
		if (amazonPostsRelationshipFeed.getEndDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(amazonPostsRelationshipFeed.getEndDate(),1)));
		}
		if (amazonPostsRelationshipFeed.getCreateUser()!=null && StringUtils.isNotEmpty(amazonPostsRelationshipFeed.getCreateUser().getId())){
			dc.add(Restrictions.eq("createUser", amazonPostsRelationshipFeed.getCreateUser()));
		}
		if(StringUtils.isNotEmpty(amazonPostsRelationshipFeed.getCountry())){
			dc.add(Restrictions.eq("country", amazonPostsRelationshipFeed.getCountry()));
		}
		if(StringUtils.isNotEmpty(amazonPostsRelationshipFeed.getParentSku())){
			dc.add(Restrictions.like("parentSku", "%"+amazonPostsRelationshipFeed.getParentSku()+"%"));
		}
		if(StringUtils.isNotEmpty(amazonPostsRelationshipFeed.getState())){
			dc.createAlias("this.items", "items");
			dc.add(Restrictions.like("items.sku", "%"+amazonPostsRelationshipFeed.getState()+"%"));
		}
		if(StringUtils.isNotBlank(amazonPostsRelationshipFeed.getResult())){
			dc.add(Restrictions.eq("state", amazonPostsRelationshipFeed.getResult()));
		}
		return amazonPostsRelationshipFeedDao.find(page, dc);
	}
	
	public String getAsinBySku(String country,String sku){
		String sql = "SELECT asin FROM amazoninfo_posts_detail  WHERE country='"+country+"' and sku like '%"+sku+"%' and asin is not null LIMIT 1 ";
    	List<String> list = amazonPostsFeedDao.findBySql(sql);
    	if(list.size()>0){
    		return list.get(0);
    	}else{
    		if(sku.contains(",")){
    			String[] arr=sku.split(",");
    			sql = "SELECT asin FROM amazoninfo_posts_detail  WHERE country='"+country+"' and sku like '%"+arr[0]+"%' and asin is not null LIMIT 1 ";
    			list = amazonPostsFeedDao.findBySql(sql);
    			if(list.size()>0){
    	    		return list.get(0);
    	    	}
    		}
    	}
		return null;
	}
	@Transactional(readOnly = false)
	public void deleteByFeedId(Integer feedId){
		String delSql="delete from amazoninfo_posts_change where feed_id=:p1";
		amazonPostsFeedDao.updateBySql(delSql, new Parameter(feedId));
	}
	
	public List<AmazonPostsFeed> findBeforePosts(Date date,AmazonPostsDetail portsDetail) {
		DetachedCriteria dc = amazonPostsFeedDao.createDetachedCriteria();
		dc.add(Restrictions.ge("createDate",date));
		dc.add(Restrictions.lt("createDate",DateUtils.addDays(date,1)));
		dc.createAlias("this.items", "items");
		dc.add(Restrictions.eq("items.asin",portsDetail.getAsin()));
		dc.add(Restrictions.eq("country",portsDetail.getCountry()));
		dc.add(Restrictions.eq("operateType","0"));
		dc.add(Restrictions.eq("state","3"));
		dc.addOrder(Order.asc("createDate"));
		return amazonPostsFeedDao.find(dc);
	}
	
	public Map<String,String> findLatestPosts(AmazonPostsDetail portsDetail){
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,String> map=Maps.newHashMap();
		if(StringUtils.isNotBlank(portsDetail.getTitle())){
			String sql="SELECT f.create_date,c.title FROM amazoninfo_posts_feed f JOIN amazoninfo_posts_change c "+
					" ON f.id=c.`feed_id` WHERE f.create_date<:p1 and f.`operate_type`='0' AND f.`state`='3' AND f.country=:p2 "+
					" AND c.`ASIN`=:p3 and c.title is not null and c.title != :p4 ORDER BY f.`create_date` desc  LIMIT 1 ";
			
			List<Object[]> list=amazonPostsFeedDao.findBySql(sql,new Parameter(portsDetail.getQueryTime(),portsDetail.getCountry(),portsDetail.getAsin(),portsDetail.getTitle()));
		    if(list!=null&&list.size()>0){
		    	Object[] obj=list.get(0);
		    	Date date=(Timestamp)obj[0];
		    	String title=obj[1].toString();
		    	map.put("title",dateFormat.format(date)+"<br/>"+title) ;
		    }
		}
		if(StringUtils.isNotBlank(portsDetail.getDescription())){
			String sql="SELECT f.create_date,c.description FROM amazoninfo_posts_feed f JOIN amazoninfo_posts_change c "+
					" ON f.id=c.`feed_id` WHERE f.create_date<:p1 and f.`operate_type`='0' AND f.`state`='3' AND f.country=:p2 "+
					" AND c.`ASIN`=:p3 and c.description is not null and c.description != :p4 ORDER BY f.`create_date` desc  LIMIT 1 ";
			
			List<Object[]> list=amazonPostsFeedDao.findBySql(sql,new Parameter(portsDetail.getQueryTime(),portsDetail.getCountry(),portsDetail.getAsin(),portsDetail.getDescription()));
		    if(list!=null&&list.size()>0){
		    	Object[] obj=list.get(0);
		    	Date date=(Timestamp)obj[0];
		    	String description=obj[1].toString();
		    	map.put("description",dateFormat.format(date)+"<br/>"+description) ;
		    }
		}
		if(StringUtils.isNotBlank(portsDetail.getFeature1())){
			String sql="SELECT f.create_date,c.feature1 FROM amazoninfo_posts_feed f JOIN amazoninfo_posts_change c "+
					" ON f.id=c.`feed_id` WHERE f.create_date<:p1 and f.`operate_type`='0' AND f.`state`='3' AND f.country=:p2 "+
					" AND c.`ASIN`=:p3 and c.feature1 is not null and c.feature1 != :p4 ORDER BY f.`create_date` desc  LIMIT 1 ";
			
			List<Object[]> list=amazonPostsFeedDao.findBySql(sql,new Parameter(portsDetail.getQueryTime(),portsDetail.getCountry(),portsDetail.getAsin(),portsDetail.getFeature1()));
		    if(list!=null&&list.size()>0){
		    	Object[] obj=list.get(0);
		    	Date date=(Timestamp)obj[0];
		    	String feature=obj[1].toString();
		    	map.put("feature1",dateFormat.format(date)+"<br/>"+feature) ;
		    }
		}
		if(StringUtils.isNotBlank(portsDetail.getFeature2())){
			String sql="SELECT f.create_date,c.feature2 FROM amazoninfo_posts_feed f JOIN amazoninfo_posts_change c "+
					" ON f.id=c.`feed_id` WHERE f.create_date<:p1 and f.`operate_type`='0' AND f.`state`='3' AND f.country=:p2 "+
					" AND c.`ASIN`=:p3 and c.feature2 is not null and c.feature2 != :p4 ORDER BY f.`create_date` desc  LIMIT 1 ";
			
			List<Object[]> list=amazonPostsFeedDao.findBySql(sql,new Parameter(portsDetail.getQueryTime(),portsDetail.getCountry(),portsDetail.getAsin(),portsDetail.getFeature2()));
		    if(list!=null&&list.size()>0){
		    	Object[] obj=list.get(0);
		    	Date date=(Timestamp)obj[0];
		    	String feature=obj[1].toString();
		    	map.put("feature2",dateFormat.format(date)+"<br/>"+feature) ;
		    }
		}
		if(StringUtils.isNotBlank(portsDetail.getFeature3())){
			String sql="SELECT f.create_date,c.feature3 FROM amazoninfo_posts_feed f JOIN amazoninfo_posts_change c "+
					" ON f.id=c.`feed_id` WHERE f.create_date<:p1 and f.`operate_type`='0' AND f.`state`='3' AND f.country=:p2 "+
					" AND c.`ASIN`=:p3 and c.feature3 is not null and c.feature3 != :p4 ORDER BY f.`create_date` desc  LIMIT 1 ";
			
			List<Object[]> list=amazonPostsFeedDao.findBySql(sql,new Parameter(portsDetail.getQueryTime(),portsDetail.getCountry(),portsDetail.getAsin(),portsDetail.getFeature3()));
		    if(list!=null&&list.size()>0){
		    	Object[] obj=list.get(0);
		    	Date date=(Timestamp)obj[0];
		    	String feature=obj[1].toString();
		    	map.put("feature3",dateFormat.format(date)+"<br/>"+feature) ;
		    }
		}
		if(StringUtils.isNotBlank(portsDetail.getFeature4())){
			String sql="SELECT f.create_date,c.feature4 FROM amazoninfo_posts_feed f JOIN amazoninfo_posts_change c "+
					" ON f.id=c.`feed_id` WHERE f.create_date<:p1 and f.`operate_type`='0' AND f.`state`='3' AND f.country=:p2 "+
					" AND c.`ASIN`=:p3 and c.feature4 is not null and c.feature4 != :p4 ORDER BY f.`create_date` desc  LIMIT 1 ";
			
			List<Object[]> list=amazonPostsFeedDao.findBySql(sql,new Parameter(portsDetail.getQueryTime(),portsDetail.getCountry(),portsDetail.getAsin(),portsDetail.getFeature4()));
		    if(list!=null&&list.size()>0){
		    	Object[] obj=list.get(0);
		    	Date date=(Timestamp)obj[0];
		    	String feature=obj[1].toString();
		    	map.put("feature4",dateFormat.format(date)+"<br/>"+feature) ;
		    }
		} 
		if(StringUtils.isNotBlank(portsDetail.getFeature5())){
			String sql="SELECT f.create_date,c.feature5 FROM amazoninfo_posts_feed f JOIN amazoninfo_posts_change c "+
					" ON f.id=c.`feed_id` WHERE f.create_date<:p1 and f.`operate_type`='0' AND f.`state`='3' AND f.country=:p2 "+
					" AND c.`ASIN`=:p3 and c.feature5 is not null and c.feature5 != :p4 ORDER BY f.`create_date` desc  LIMIT 1 ";
			
			List<Object[]> list=amazonPostsFeedDao.findBySql(sql,new Parameter(portsDetail.getQueryTime(),portsDetail.getCountry(),portsDetail.getAsin(),portsDetail.getFeature5()));
		    if(list!=null&&list.size()>0){
		    	Object[] obj=list.get(0);
		    	Date date=(Timestamp)obj[0];
		    	String feature=obj[1].toString();
		    	map.put("feature5",dateFormat.format(date)+"<br/>"+feature) ;
		    }
		} 
		if(StringUtils.isNotBlank(portsDetail.getKeyword1())){
			String sql="SELECT f.create_date,c.keyword1 FROM amazoninfo_posts_feed f JOIN amazoninfo_posts_change c "+
					" ON f.id=c.`feed_id` WHERE f.create_date<:p1 and f.`operate_type`='0' AND f.`state`='3' AND f.country=:p2 "+
					" AND c.`ASIN`=:p3 and c.keyword1 is not null and c.keyword1 != :p4 ORDER BY f.`create_date` desc  LIMIT 1 ";
			
			List<Object[]> list=amazonPostsFeedDao.findBySql(sql,new Parameter(portsDetail.getQueryTime(),portsDetail.getCountry(),portsDetail.getAsin(),portsDetail.getKeyword1()));
		    if(list!=null&&list.size()>0){
		    	Object[] obj=list.get(0);
		    	Date date=(Timestamp)obj[0];
		    	String keyword=obj[1].toString();
		    	map.put("keyword1",dateFormat.format(date)+"<br/>"+keyword) ;
		    }
		}
		
		if(StringUtils.isNotBlank(portsDetail.getKeyword2())){
			String sql="SELECT f.create_date,c.keyword2 FROM amazoninfo_posts_feed f JOIN amazoninfo_posts_change c "+
					" ON f.id=c.`feed_id` WHERE f.create_date<:p1 and f.`operate_type`='0' AND f.`state`='3' AND f.country=:p2 "+
					" AND c.`ASIN`=:p3 and c.keyword2 is not null and c.keyword2 != :p4 ORDER BY f.`create_date` desc  LIMIT 1 ";
			
			List<Object[]> list=amazonPostsFeedDao.findBySql(sql,new Parameter(portsDetail.getQueryTime(),portsDetail.getCountry(),portsDetail.getAsin(),portsDetail.getKeyword2()));
		    if(list!=null&&list.size()>0){
		    	Object[] obj=list.get(0);
		    	Date date=(Timestamp)obj[0];
		    	String keyword=obj[1].toString();
		    	map.put("keyword2",dateFormat.format(date)+"<br/>"+keyword) ;
		    }
		}
		
		if(StringUtils.isNotBlank(portsDetail.getKeyword3())){
			String sql="SELECT f.create_date,c.keyword3 FROM amazoninfo_posts_feed f JOIN amazoninfo_posts_change c "+
					" ON f.id=c.`feed_id` WHERE f.create_date<:p1 and f.`operate_type`='0' AND f.`state`='3' AND f.country=:p2 "+
					" AND c.`ASIN`=:p3 and c.keyword3 is not null and c.keyword3 != :p4 ORDER BY f.`create_date` desc  LIMIT 1 ";
			
			List<Object[]> list=amazonPostsFeedDao.findBySql(sql,new Parameter(portsDetail.getQueryTime(),portsDetail.getCountry(),portsDetail.getAsin(),portsDetail.getKeyword3()));
		    if(list!=null&&list.size()>0){
		    	Object[] obj=list.get(0);
		    	Date date=(Timestamp)obj[0];
		    	String keyword=obj[1].toString();
		    	map.put("keyword3",dateFormat.format(date)+"<br/>"+keyword) ;
		    }
		}
		
		if(StringUtils.isNotBlank(portsDetail.getKeyword4())){
			String sql="SELECT f.create_date,c.keyword4 FROM amazoninfo_posts_feed f JOIN amazoninfo_posts_change c "+
					" ON f.id=c.`feed_id` WHERE f.create_date<:p1 and f.`operate_type`='0' AND f.`state`='3' AND f.country=:p2 "+
					" AND c.`ASIN`=:p3 and c.keyword4 is not null and c.keyword4 != :p4 ORDER BY f.`create_date` desc  LIMIT 1 ";
			
			List<Object[]> list=amazonPostsFeedDao.findBySql(sql,new Parameter(portsDetail.getQueryTime(),portsDetail.getCountry(),portsDetail.getAsin(),portsDetail.getKeyword4()));
		    if(list!=null&&list.size()>0){
		    	Object[] obj=list.get(0);
		    	Date date=(Timestamp)obj[0];
		    	String keyword=obj[1].toString();
		    	map.put("keyword4",dateFormat.format(date)+"<br/>"+keyword) ;
		    }
		}
		
		if(StringUtils.isNotBlank(portsDetail.getKeyword5())){
			String sql="SELECT f.create_date,c.keyword5 FROM amazoninfo_posts_feed f JOIN amazoninfo_posts_change c "+
					" ON f.id=c.`feed_id` WHERE f.create_date<:p1 and f.`operate_type`='0' AND f.`state`='3' AND f.country=:p2 "+
					" AND c.`ASIN`=:p3 and c.keyword5 is not null and c.keyword5 != :p4 ORDER BY f.`create_date` desc  LIMIT 1 ";
			
			List<Object[]> list=amazonPostsFeedDao.findBySql(sql,new Parameter(portsDetail.getQueryTime(),portsDetail.getCountry(),portsDetail.getAsin(),portsDetail.getKeyword5()));
		    if(list!=null&&list.size()>0){
		    	Object[] obj=list.get(0);
		    	Date date=(Timestamp)obj[0];
		    	String keyword=obj[1].toString();
		    	map.put("keyword5",dateFormat.format(date)+"<br/>"+keyword) ;
		    }
		}
		
		return map;
	}
	
	
	public List<String> findNameByParentSku(String parentSku,String accountName){
		String sql="SELECT DISTINCT c.`product_name` FROM amazoninfo_posts_relationship_feed p "+
				" JOIN amazoninfo_posts_relationship_change c ON p.id=c.`feed_id` "+
				" WHERE p.`operat`='2' AND p.`create_date`>=:p1 AND p.`parent_sku`=:p2 "+
				" AND p.`account_name`=:p3 AND p.`state`!='0' AND p.`state`!='5' AND c.`product_name` IS NOT NULL";
		
		return amazonPostsFeedDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-30),parentSku,accountName));
	}
	
	
}
