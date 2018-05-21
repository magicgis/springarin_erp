/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.amazoninfo.dao.ProductDirectoryCommentDao;
import com.springrain.erp.modules.amazoninfo.entity.ProductDirectoryComment;
import com.springrain.erp.modules.amazoninfo.entity.ProductDirectoryCommentDetail;

/**
 * 产品目录扫描Service
 * @author Michael
 * @version 2015-03-03
 */
@Component
@Transactional(readOnly = true)
public class ProductDirectoryCommentService extends BaseService {
	@Autowired
	private ProductDirectoryCommentDao directoryCommentDao;
	public ProductDirectoryComment get(Integer id) {
		return directoryCommentDao.get(id);
	}
	
	
	@Transactional(readOnly=false)
	public void save(ProductDirectoryComment comment){
		this.directoryCommentDao.save(comment);
	}
	public List<ProductDirectoryComment> find(Integer directoryId,Date updateDate) {
		DetachedCriteria dc = directoryCommentDao.createDetachedCriteria();
		dc.add(Restrictions.eq("updateDate", updateDate));
		dc.add(Restrictions.eq("directoryId", directoryId));
		return directoryCommentDao.find(dc);
	}
	
	public List<ProductDirectoryComment> findByDirectoryId(Integer directoryId) {
		DetachedCriteria dc = directoryCommentDao.createDetachedCriteria();
		if(directoryId!=null){
			dc.add(Restrictions.eq("directoryId", directoryId));
		}
		return directoryCommentDao.find(dc);
	}
	
	
	//获取需要扫描评论详细的asin和国家
	public Set<String> getAllAsinAndCountry(){
		String sql = "SELECT CONCAT(b.`asin`,',',b.`country`) FROM amazoninfo_directory AS a , amazoninfo_directory_comment AS b WHERE a.`id`=b.`directory_id` AND a.`directory_sta`='0' ORDER BY a.id DESC ";
		List<String> res = this.directoryCommentDao.findBySql(sql);
		Set<String> set = Sets.newHashSet();
		if(res!=null&&res.size()>0){
			set.addAll(res);
		}
		return set;
	}
	
	//获取自己产品的asin
	public Set<String> getSelfAsin(){
		String sql = "SELECT a.`asin` FROM amazoninfo_product2 AS a WHERE a.`active`='1' AND a.`asin` IS NOT NULL   GROUP BY a.`asin` ";
		List<String> res = this.directoryCommentDao.findBySql(sql);
		Set<String> set = Sets.newHashSet();
		if(res!=null&&res.size()>0){
			for(String asin :res){
				set.add(asin);
			}
		}
		return set;
	}
	

	
	
	
	//根据asin查出销量
	public Integer  getSaleQuantityByAsin(String asin,String country){
		//查出这个asin对应的产品名
		String sql ="SELECT a.`product_name` FROM psi_sku AS a  WHERE a.`del_flag`='0'  AND a.`asin`=:p1 GROUP BY a.`product_name`";
		List<String> list = this.directoryCommentDao.findBySql(sql,new Parameter(asin));
		if(list!=null&&list.size()>0){
			//查出该产品名销售总素
			sql="SELECT SUM(a.sales_volume) FROM amazoninfo_sale_report  AS a WHERE  a.order_type='1' and a.product_name IN :p1  AND a.country=:p2 ";
			List<BigDecimal> res = this.directoryCommentDao.findBySql(sql,new Parameter(list,country));
			if(res!=null&&res.size()>0&&res.get(0)!=null){
				return res.get(0).intValue();
			}
		}
	   return null;
	}
	
	//根据asin查出产品名
	public Map<String,String>  getProductNameByAsin(Set<String> asins){
		Map<String,String> resMap = Maps.newHashMap();
		//查出这个asin对应的产品名
		String sql ="SELECT a.`asin`,a.`product_name` FROM psi_sku AS a  WHERE a.`del_flag`='0'  AND a.`asin` IN :p1 AND a.`product_name` not in('Inateck other','Inateck Old') GROUP BY a.`asin`";
		List<Object[]> list = this.directoryCommentDao.findBySql(sql,new Parameter(asins));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				if(obj[1]!=null){
					resMap.put(obj[0].toString(), obj[1].toString());
				}
			}
		}
	   return resMap;
	}
	
	
	//获取最近一天的日期
	public Date getLastDate(Integer directoryId){
		String sql ="SELECT a.`update_date` FROM amazoninfo_directory_comment AS a where a.`directory_id`=:p1 ORDER BY a.`id` DESC LIMIT 1";
		List<Date> endDates = this.directoryCommentDao.findBySql(sql,new Parameter(directoryId));
		if(endDates!=null&&endDates.size()>0){
			return endDates.get(0);
		}
		return null;
	}
	
	
	//更新上架日期
	@Transactional(readOnly=false)
	public Integer getUpDate(){
		String sql ="UPDATE amazoninfo_directory_comment AS b ,(SELECT MIN(a.`review_date`) AS reviewDate,a.`asin`,a.`country`FROM amazoninfo_directory_comment_detail AS a GROUP BY a.`asin`,a.`country`) AS bb SET b.`shelves_date`=bb.reviewDate WHERE b.asin=bb.asin AND b.country=bb.country";
		return this.directoryCommentDao.updateBySql(sql,null);
	}
	
	 
	
	//asin country  reviewId
	public Set<String>  getStarLevelMap(String asin,String country){
		Set<String> setTemp = Sets.newHashSet();
		String sql ="SELECT a.`review_id` FROM amazoninfo_directory_comment_detail AS a WHERE a.`asin` =:p1 AND a.`country`=:p2 ";
		List<String> list = this.directoryCommentDao.findBySql(sql,new Parameter(asin,country));
		if(list!=null&&list.size()>0){
			setTemp.addAll(list);
		}
		return setTemp;
	}
	
	//查出需要扫描库存价格的国家和asin   
	public Map<String,Set<String>>  getAsinAndCountry(Set<String> selfAsins){
		Map<String,Set<String>> resMap = Maps.newHashMap();
		String sql ="SELECT a.`asin`,a.`country` FROM amazoninfo_directory_comment AS a where a.asin not in :p1 GROUP BY a.`asin`,a.`country`";
		List<Object[]> list = this.directoryCommentDao.findBySql(sql,new Parameter(selfAsins));
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
	
	/**
	 * 查出需要扫描库存价格的国家和asin 
	 * 
	 */  
	public String  getTitleByAsin(String asin,String country){
		String sql="SELECT a.`title` FROM amazoninfo_directory_comment AS a WHERE a.`asin`=:p1 AND a.`country`=:p2 ORDER BY a.`data_date` DESC LIMIT 1";
		String title="";
		List<String> list = this.directoryCommentDao.findBySql(sql,new Parameter(asin,country));
		if(list!=null&&list.size()>0){
			title=list.get(0);
		}
		return title;
	}
		
		
		
	@Transactional(readOnly=false)
	public String updateShieldSta(Integer directoryId,String asin,String isShield){
		String sql="UPDATE amazoninfo_directory_comment  SET is_shield=:p3 WHERE directory_id=:p1 AND asin=:p2";
		Integer i =this.directoryCommentDao.updateBySql(sql, new Parameter(directoryId,asin,isShield));
		if(i.intValue()==0){
			return "更新失败";
		}else{
			return "";
		}
	}
}
