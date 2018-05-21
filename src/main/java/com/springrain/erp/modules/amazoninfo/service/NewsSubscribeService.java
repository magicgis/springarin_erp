package com.springrain.erp.modules.amazoninfo.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.NewsSubscribeDao;
import com.springrain.erp.modules.amazoninfo.entity.NewsSubscribe;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 邮件订阅Service
 */
@Component
@Transactional(readOnly = true)
public class NewsSubscribeService extends BaseService {

	@Autowired
	private NewsSubscribeDao newsSubscribeDao;
	
	@Autowired
	private PsiProductTypeGroupDictService dictService;
	
	public NewsSubscribe get(Integer id) {
		return newsSubscribeDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(NewsSubscribe newsSubscribe) {
		newsSubscribeDao.save(newsSubscribe);
	}
	
	public Page<NewsSubscribe> find(Page<NewsSubscribe> page, NewsSubscribe newsSubscribe) {
		DetachedCriteria dc = newsSubscribeDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(newsSubscribe.getPlatform())){
			dc.add(Restrictions.like("platform", "%" + newsSubscribe.getPlatform() + "%"));
		}
		if(StringUtils.isNotEmpty(newsSubscribe.getState())){
			dc.add(Restrictions.eq("state", newsSubscribe.getState()));
		}
		if(StringUtils.isNotEmpty(newsSubscribe.getType())){
			dc.add(Restrictions.eq("type", newsSubscribe.getType()));
		}
		if (!UserUtils.getUser().isAdmin()) {
			dc.add(Restrictions.eq("createBy", UserUtils.getUser()));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		return newsSubscribeDao.find(page, dc);
	}
	
	public List<NewsSubscribe> findByEmailType(String emailType) {
		DetachedCriteria dc = newsSubscribeDao.createDetachedCriteria();
		dc.add(Restrictions.like("emailType", "%" + emailType + "%"));
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.eq("state", "1"));
		dc.createAlias("createBy", "createBy");
		dc.add(Restrictions.eq("createBy.delFlag", "0"));
		return newsSubscribeDao.find(dc);
	}
	
	//根据产品获取需要发消息的email地址
	public List<String> findEmailList(String country, String productName) {
		String sql = "SELECT DISTINCT t.`email` FROM `amazoninfo_news_subscribe` t WHERE t.`platform` LIKE :p1 AND t.`product_name` LIKE :p2 AND t.`state`='1' AND t.`del_flag`='0'";
		return newsSubscribeDao.findBySql(sql, new Parameter("%" + country + "%", "%" + productName + "%"));
	}
	
	//根据邮件类型编号获取信息Map key 1:country集合  2：产品集合  3：邮箱集合
	public Map<String, Set<String>> findByNum(String num) {
		Map<String, Set<String>> rs = Maps.newHashMap();
		String sql = "SELECT t.`platform`,t.`product_name`,t.`email` FROM `amazoninfo_news_subscribe` t,sys_user u WHERE t.`create_by`=u.id AND t.`email_type` LIKE :p1 AND t.`state`='1' AND t.`del_flag`='0' AND u.del_flag='0'";
		List<Object[]> list = newsSubscribeDao.findBySql(sql, new Parameter("%" + num + "%"));
		Set<String> countrySet = Sets.newHashSet();
		Set<String> productSet = Sets.newHashSet();
		Set<String> emailSet = Sets.newHashSet();
		for (Object[] obj : list) {
			String platform = obj[0].toString();
			String productNames = obj[1].toString();
			String emails = obj[2].toString();
			for (String country : platform.split(",")) {
				countrySet.add(country);
			}
			for (String productName : productNames.split(",")) {
				productSet.add(productName);
			}
			for (String email : emails.split(",")) {
				emailSet.add(email);
			}
		}
		if (countrySet.size() > 0) {
			rs.put("1", countrySet);
			rs.put("2", productSet);
			rs.put("3", emailSet);
		}
		return rs;
	}
	
	//国家、产品对应的发件地址集合[country[productName emailset]]
	public Map<String, Map<String, Set<String>>> findRelationsByNum(String num) {
		Map<String, Map<String, Set<String>>> rs = Maps.newHashMap();
		String sql = "SELECT t.`platform`,t.`product_name`,t.`email` FROM `amazoninfo_news_subscribe` t WHERE t.`email_type` LIKE :p1 AND t.`state`='1' AND t.`del_flag`='0'";
		List<Object[]> list = newsSubscribeDao.findBySql(sql, new Parameter("%" + num + "%"));
		for (Object[] obj : list) {
			String platform = obj[0].toString();
			String productNames = obj[1].toString();
			String email = obj[2].toString();
			for (String country : platform.split(",")) {
				Map<String, Set<String>> countryMap = rs.get(country);
				if (countryMap == null) {
					countryMap = Maps.newHashMap();
					rs.put(country, countryMap);
				}
				for (String productName : productNames.split(",")) {
					Set<String> set = countryMap.get(productName);
					if (set == null) {
						set = Sets.newHashSet();
						countryMap.put(productName, set);
					}
					set.add(email);
				}
			}
		}
		return rs;
	}
	
	//国家、产品对应的发件地址集合[country[productName email]]
	public Map<String, Map<String, String>> findRelationsBySubscribe(NewsSubscribe newsSubscribe) {
		Map<String, Map<String, String>> rs = Maps.newHashMap();
		if ("1".equals(newsSubscribe.getType())) {	//直接按产品查询
			String sql = "SELECT t.`platform`,t.`product_name`,t.`email` FROM `amazoninfo_news_subscribe` t WHERE t.`id`= :p1 ";
			List<Object[]> list = newsSubscribeDao.findBySql(sql, new Parameter(newsSubscribe.getId()));
			for (Object[] obj : list) {
				String platform = obj[0].toString();
				String productNames = obj[1]==null?"":obj[1].toString();
				String email = obj[2].toString();
				for (String country : platform.split(",")) {
					Map<String, String> countryMap = rs.get(country);
					if (countryMap == null) {
						countryMap = Maps.newHashMap();
						rs.put(country, countryMap);
					}
					for (String productName : productNames.split(",")) {
						countryMap.put(productName, email);
					}
				}
			}
		} else if ("2".equals(newsSubscribe.getType())) {	//按产品类型
			List<String> typeList = Lists.newArrayList(newsSubscribe.getProductName().split(","));
			List<String> countryList = Lists.newArrayList(newsSubscribe.getPlatform().split(","));
			String sql = "SELECT t.`country`,CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END AS productName " +
					" FROM psi_product_eliminate t, psi_product p WHERE t.`product_id`=p.`id` AND t.`del_flag`='0' AND p.`del_flag`='0' "+
					" AND p.`TYPE` IN :p1 AND t.`country` IN :p2 ";
			List<Object[]> list = newsSubscribeDao.findBySql(sql, new Parameter(typeList, countryList));
			for (Object[] obj : list) {
				String country = obj[0].toString();
				String productName = obj[1].toString();
				Map<String, String> countryMap = rs.get(country);
				if (countryMap == null) {
					countryMap = Maps.newHashMap();
					rs.put(country, countryMap);
				}
				countryMap.put(productName, newsSubscribe.getEmail());
			}
		} else if ("3".equals(newsSubscribe.getType())) { //按产品线
			Map<String, String> typeLine = dictService.getTypeLine(null);
			String lines = newsSubscribe.getProductName();
			List<String> countryList = Lists.newArrayList(newsSubscribe.getPlatform().split(","));
			List<String> typeList = Lists.newArrayList();
			for (Map.Entry<String,String> entry : typeLine.entrySet()) {  
			    String pType=entry.getKey();
				if (lines.contains(entry.getValue())) {
					typeList.add(pType);
				}
			}
			String sql = "SELECT t.`country`,CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END AS productName " +
					" FROM psi_product_eliminate t, psi_product p WHERE t.`product_id`=p.`id` AND t.`del_flag`='0' AND p.`del_flag`='0' "+
					" AND p.`TYPE` IN :p1 AND t.`country` IN :p2 ";
			List<Object[]> list = newsSubscribeDao.findBySql(sql, new Parameter(typeList, countryList));
			for (Object[] obj : list) {
				String country = obj[0].toString();
				String productName = obj[1].toString();
				Map<String, String> countryMap = rs.get(country);
				if (countryMap == null) {
					countryMap = Maps.newHashMap();
					rs.put(country, countryMap);
				}
				countryMap.put(productName, newsSubscribe.getEmail());
			}
		} else if ("4".equals(newsSubscribe.getType())) { //按产品属性(1:新品、2:主力、3:淘汰)
			List<String> countryList = Lists.newArrayList(newsSubscribe.getPlatform().split(","));
			String attrs = newsSubscribe.getProductName();
			String temp = "";
			Parameter parameter = null;
			boolean flag = true;
			if (attrs.contains("1")) {
				flag = false;
				temp += " AND (t.`is_new`='1'";
			}
			if (attrs.contains("2")) {
				//统计主力产品
				if (flag) {
					temp += " AND (t.`is_sale`='3'";
				} else {
					temp += " OR t.`is_sale`='3'";
				}
				flag = false;
			}
			if (attrs.contains("3")) {
				if (flag) {
					temp += " AND (t.`is_sale`='4'";
				} else {
					temp += " OR t.`is_sale`='4'";
				}
			}
			temp += " )";
			String sql = "SELECT t.`country`,CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END AS productName " +
					" FROM psi_product_eliminate t, psi_product p WHERE t.`product_id`=p.`id` AND t.`del_flag`='0' AND p.`del_flag`='0' AND t.`country` IN :p1 "+ temp;
			List<Object[]> list = Lists.newArrayList();
			list = newsSubscribeDao.findBySql(sql, new Parameter(countryList));
			
			for (Object[] obj : list) {
				String country = obj[0].toString();
				String productName = obj[1].toString();
				Map<String, String> countryMap = rs.get(country);
				if (countryMap == null) {
					countryMap = Maps.newHashMap();
					rs.put(country, countryMap);
				}
				countryMap.put(productName, newsSubscribe.getEmail());
			}
		}
		return rs;
	}
	
	//查询前一个小时的改价情况
	public List<Object[]> findPriceChange() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
		String dateStr = format.format(DateUtils.addHours(new Date(), -1));
		String sql = "SELECT p.`sku`,t.`country`,IFNULL(p.`sale_price`,p.`price`),t.`reason` FROM `amazoninfo_price` p ,`amazoninfo_price_feed` t "+
				" WHERE p.`feed_price_feed_id`=t.`id` AND t.`request_date`>=:p1 AND t.`request_date`<=:p2  "+
				" AND t.`create_by`!='1' AND t.`reason`!='包邮调价' AND t.`state`='3' ORDER BY t.`country`,t.`request_date`";
		return newsSubscribeDao.findBySql(sql, new Parameter(dateStr+":00:00", dateStr+":59:59"));
	}
	
	//查询所有可用的邮件类型
	public List<Object[]> findNews() {
		String sql = "SELECT t.`number`,t.`name`,t.`remark`,t.`auto` FROM `amazoninfo_news_type` t WHERE t.`state`='1' AND t.`del_flag`='0' ORDER BY t.`type`,t.`sort`";
		return newsSubscribeDao.findBySql(sql);
	}
	
	public static void main(String[] args) throws Exception {
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
//		String dateStr = format.format(DateUtils.addHours(new Date(), -1));
//		System.out.println(dateStr);
		
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		NewsSubscribeService  a= applicationContext.getBean(NewsSubscribeService.class);
		NewsSubscribe newsSubscribe = a.get(4);
		a.findRelationsBySubscribe(newsSubscribe);
		applicationContext.close();
	}
	
	
}
