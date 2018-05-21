package com.springrain.erp.modules.psi.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.PsiProductPostMailInfoDao;
import com.springrain.erp.modules.psi.entity.PsiProductPostMailInfo;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 
 * 主力产品或者新品,发送收货邮件提醒
 * @author computer
 *
 */
@Component
@Transactional(readOnly = true)
public class PsiProductPostMailInfoService extends BaseService{      
	@Autowired
	private PsiProductPostMailInfoDao postMailDao;
	
	
	@Transactional(readOnly=false)
	public void save(PsiProductPostMailInfo mail){
		this.postMailDao.save(mail);
	}
	
	public Page<PsiProductPostMailInfo> find(Page<PsiProductPostMailInfo> page, PsiProductPostMailInfo postMail) {
		DetachedCriteria dc = postMailDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(postMail.getProductName())){
			dc.add(Restrictions.like("productName", "%"+postMail.getProductName()+"%"));
		}
		if(StringUtils.isNotEmpty(postMail.getCountry())){
			dc.add(Restrictions.like("country", postMail.getCountry()));
		}
		if(StringUtils.isNotEmpty(postMail.getStatus())){
			dc.add(Restrictions.like("status", postMail.getStatus()));
		}
		if(StringUtils.isEmpty(page.getOrderBy())){
			dc.addOrder(Order.desc("id"));
		}
		return postMailDao.find(page, dc);
	}
	
	
	@Transactional(readOnly = false)
	public String updateStatus(Integer id,String status){
		PsiProductPostMailInfo info = this.postMailDao.get(id);
		info.setStatus(status);
		info.setUpdateDate(new Date());
		info.setUpdateUser(UserUtils.getUser());
		this.postMailDao.save(info);
		return "true";
	}
	
	
	
	/**
	 *获取主力 4星以上的
	 */
	public List<String> getMainProducts(){
		List<String> rs = Lists.newArrayList();
		//查询所有主力产品
		String sql="SELECT CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`)  END AS proName  FROM psi_product_attribute AS a WHERE a.`is_main`='1' and a.del_flag='0' ";
		List<String> names = this.postMailDao.findBySql(sql);
		if(names!=null&&names.size()>0){
			//查询所有四星以上的
			sql="SELECT MAX(b.`query_time`) FROM amazoninfo_posts_detail AS b ";
			List<Date> dates = this.postMailDao.findBySql(sql);
			sql="SELECT CONCAT(a.`product_name`,',,,,',a.country) FROM amazoninfo_posts_detail AS a WHERE a.`product_name` IS NOT NULL AND a.`star`>4 AND a.`product_name` IN :p1 AND a.`query_time`=:p2 ";
			rs = this.postMailDao.findBySql(sql,new Parameter(names,dates.get(0)));
		}
		return rs;
	}
	
	
	
	/**
	 *获取新品，销量超过100的
	 */
	public List<String> getNewProducts(){
		List<String> rs = Lists.newArrayList();
		//查询所有新品
		String sql=" SELECT CONCAT(a.`product_name`,'_',a.`color`,'_',a.country)  AS proName FROM psi_product_eliminate AS a WHERE a.`del_flag`='1' AND a.`is_new` ='1' AND a.`is_sale`!='4' ";
		List<String> names = this.postMailDao.findBySql(sql);
		if(names!=null&&names.size()>0){
			//查询销量累计大于100的
			sql="SELECT CASE WHEN a.`color`='' THEN CONCAT(a.`product_name`,',,,,',a.country) ELSE CONCAT(a.`product_name`,'_',a.`color`,',,,,',a.country)  END AS proName FROM amazoninfo_sale_report AS a WHERE CONCAT(a.`product_name`,'_',a.`color`,'_',a.country) IN :p1 AND a.date>DATE_SUB(CURDATE(), INTERVAL 6 MONTH) GROUP BY a.`product_name`,a.`color`,a.country HAVING SUM(a.`sales_volume`)>100;";
			rs = this.postMailDao.findBySql(sql,new Parameter(names));
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public  void addProductPostMailInfos(){
		Date dataDate = new Date();
		List<String> mains = this.getMainProducts();
		List<String> news = this.getNewProducts();
		Map<String,Set<String>> oldMap = getOldProducts();
		Set<String> oldMains = (oldMap!=null&&oldMap.get("0")!=null)?oldMap.get("0"):null;
		Set<String> oldNews = (oldMap!=null&&oldMap.get("1")!=null)?oldMap.get("1"):null;
		for(String conKey:mains){
			if(oldMains!=null){
				if(!oldMains.contains(conKey)){
					String arr[]=conKey.split(",,,,");
					PsiProductPostMailInfo info = new PsiProductPostMailInfo("0", arr[1], arr[0], "0", null, dataDate, null, null);
					this.postMailDao.save(info);
				}
			}else{
				String arr[]=conKey.split(",,,,");
				PsiProductPostMailInfo info = new PsiProductPostMailInfo("0", arr[1], arr[0], "0", null, dataDate, null, null);
				this.postMailDao.save(info);
			}
		}
		
		for(String conKey:news){
			if(oldNews!=null){
				if(!oldNews.contains(conKey)){
					String arr[]=conKey.split(",,,,");
					PsiProductPostMailInfo info = new PsiProductPostMailInfo("1", arr[1], arr[0], "1", null, dataDate, null, null);
					this.postMailDao.save(info);
				}
			}else{
				String arr[]=conKey.split(",,,,");
				PsiProductPostMailInfo info = new PsiProductPostMailInfo("1", arr[1], arr[0], "1", null, dataDate, null, null);
				this.postMailDao.save(info);
			}
		}
	}
	
	/**
	 *获取库里面有的信息 
	 *
	 */
	public Map<String,Set<String>> getOldProducts(){
		Map<String,Set<String>> rs=Maps.newHashMap();
		String sql="SELECT CONCAT(a.`product_name`,',,,,',a.country),a.`type` FROM psi_product_post_mail_info AS a";
		List<Object[]> list = this.postMailDao.findBySql(sql);
		for(Object[] obj:list){
			String pro = (String)obj[0];
			String type = (String)obj[1];
			Set<String> set = null;
			if(rs.get(type)==null){
				set = Sets.newHashSet();
			}else{
				set=rs.get(type);
			}
			set.add(pro);
			rs.put(type, set);
		}
		return rs;
	}

	
	/**
	 *获取没发邮件的产品信息
	 *
	 */
	public Map<String,Set<String>> getUnSendProducts(){
		Map<String,Set<String>> rs=Maps.newHashMap();
		String sql="SELECT CONCAT(a.`product_name`,',,,,',a.`type`),a.country FROM psi_product_post_mail_info AS a WHERE a.`status`='0'";
		List<Object[]> list = this.postMailDao.findBySql(sql);
		for(Object[] obj:list){
			String pro = (String)obj[0];
			String country = (String)obj[1];
			Set<String> set = null;
			if(rs.get(country)==null){
				set = Sets.newHashSet();
			}else{
				set=rs.get(country);
			}
			set.add(pro);
			rs.put(country, set);
		}
		return rs;
	}

}
