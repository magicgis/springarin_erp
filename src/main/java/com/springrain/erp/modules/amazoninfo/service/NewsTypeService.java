package com.springrain.erp.modules.amazoninfo.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.NewsTypeDao;
import com.springrain.erp.modules.amazoninfo.entity.NewsType;

/**
 * 邮件订阅对象Service
 */
@Component
@Transactional(readOnly = true)
public class NewsTypeService extends BaseService {

	@Autowired
	private NewsTypeDao newsTypeDao;
	
	public NewsType get(Integer id) {
		return newsTypeDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(NewsType newsType) {
		newsTypeDao.save(newsType);
	}
	
	public Page<NewsType> find(Page<NewsType> page, NewsType newsType) {
		DetachedCriteria dc = newsTypeDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(newsType.getState())){
			dc.add(Restrictions.eq("state", newsType.getState()));
		}
		if(StringUtils.isNotEmpty(newsType.getNumber())){
			dc.add(Restrictions.eq("number", newsType.getNumber()));
		}
		if(StringUtils.isNotEmpty(newsType.getName())){
			dc.add(Restrictions.like("name", "%" + newsType.getName() + "%"));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		return newsTypeDao.find(page, dc);
	}
	
	/**
	 * 生成一个新的编号
	 * @return
	 */
	public String getNewNumber() {
		String sql = "SELECT MAX(t.`number`+1) FROM `amazoninfo_news_type` t";
		List<Object> list = newsTypeDao.findBySql(sql);
		return list.get(0).toString();
	}
	
	public static void main(String[] args) throws Exception {
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
//		String dateStr = format.format(DateUtils.addHours(new Date(), -1));
//		System.out.println(dateStr);
		
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		//NewsTypeService  a= applicationContext.getBean(NewsTypeService.class);
		applicationContext.close();
	}
	
	
}
