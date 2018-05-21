/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.dao.ProductDirectoryCommentDetailDao;
import com.springrain.erp.modules.amazoninfo.entity.ProductDirectoryCommentDetail;

/**
 * 产品目录扫描Service
 * @author Michael
 * @version 2015-03-03
 */
@Component
@Transactional(readOnly = true)
public class ProductDirectoryCommentDetailService extends BaseService {
	@Autowired
	private ProductDirectoryCommentDetailDao directoryCommentDetailDao;
	@Transactional(readOnly=false)
	public void saveList(List<ProductDirectoryCommentDetail> details){
		for(ProductDirectoryCommentDetail detail:details){
			this.directoryCommentDetailDao.save(detail);
		}
	}
	
	/***
	 *查询最近一个月的评论数 
	 */
	public Map<String,Integer> getOneMonthReviews(Set<String> asins,String country){
		Map<String,Integer> res =Maps.newHashMap();
		String sql="SELECT a.asin,COUNT(*) FROM amazoninfo_directory_comment_detail AS a WHERE  a.`country`=:p1 AND a.`asin` IN :p2 AND a.`review_date` BETWEEN DATE_ADD(CURDATE(),INTERVAL -31 DAY ) AND DATE_ADD(CURDATE(),INTERVAL -1 DAY) GROUP BY a.`asin`";
		List<Object[]> list =this.directoryCommentDetailDao.findBySql(sql, new Parameter(country,asins));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				res.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
			}
		}
		return res;
	}
	
	
	/***
	 *查询有评论那天的七天的总数  key:asin    value：7天, 去年七天 , 前年七天 , 一个月
	 */
	public Map<String,String> getLast7DaysReviews(Set<String> asins,String country){
		Map<String,String> res =Maps.newHashMap();
		String sql ="SELECT a.`review_date` FROM amazoninfo_directory_comment_detail AS a WHERE a.`asin` IN :p1 AND a.`country`= :p2 ORDER BY a.`review_date` DESC LIMIT 1";
		List<Date> lastDates =this.directoryCommentDetailDao.findBySql(sql, new Parameter(asins,country));
		if(lastDates!=null&&lastDates.size()>0){
			Date beforeDate1 =lastDates.get(0);
			Date beforeDate2 = DateUtils.addYears(beforeDate1, -1);
			Date beforeDate3 = DateUtils.addYears(beforeDate1, -3);
			sql="SELECT a.`asin`, SUM(CASE WHEN a.`review_date` BETWEEN DATE_ADD(:p3,INTERVAL -7 DAY ) AND :p3 THEN 1 ELSE 0 END) AS aa , " +
					"SUM(CASE WHEN a.`review_date` BETWEEN DATE_ADD(:p3,INTERVAL -30 DAY ) AND :p3 THEN 1 ELSE 0 END) AS dd ,"+
					"SUM(CASE WHEN a.`review_date` BETWEEN DATE_ADD(:p4,INTERVAL -7 DAY ) AND :p4 THEN 1 ELSE 0 END) AS bb," +
					"SUM(CASE WHEN a.`review_date` BETWEEN DATE_ADD(:p5,INTERVAL -7 DAY ) AND :p5 THEN 1 ELSE 0 END) AS cc" +
					" FROM amazoninfo_directory_comment_detail AS a WHERE  a.`asin` IN :p1 AND a.`country`= :p2 AND a.`review_date` >=DATE_ADD(:p5,INTERVAL -7 DAY )  GROUP BY a.`asin`";
			List<Object[]> list =this.directoryCommentDetailDao.findBySql(sql, new Parameter(asins,country,beforeDate1,beforeDate2,beforeDate3));
			if(list!=null&&list.size()>0){
				for(Object[] obj:list){
					res.put(obj[0].toString(),obj[1]+","+obj[3]+","+obj[4]+","+obj[2]);
				}
			}
		}
		return res;
	}
	
	/**
	 *查询最近一个月的每天评论数 
	 * 
	 */
	public List<Map<String,Integer>>  get30DaysComms(String country,String asin,Date endDate){
		List<Map<String,Integer>> rs = Lists.newArrayList();
		Map<String,Integer>  goodMap = Maps.newHashMap();
		Map<String,Integer>  badMap = Maps.newHashMap();
		Map<String,Integer>  totalMap = Maps.newHashMap();
		String sql="SELECT DATE_FORMAT(a.`review_date`,'%Y-%m-%d') AS reviewDate,a.`star` FROM amazoninfo_directory_comment_detail AS a  WHERE  a.`country`=:p1 AND a.`asin`=:p2 AND a.`review_date` BETWEEN DATE_ADD(:p3,INTERVAL -31 DAY ) AND DATE_ADD(:p3,INTERVAL 0 DAY)";
		List<Object[]> list = this.directoryCommentDetailDao.findBySql(sql,new Parameter(country,asin,endDate));
		for(Object[] obj:list){
			String reviewDate = obj[0].toString();
			Integer star = Integer.parseInt(obj[1].toString());
			if(star>3){
				if(goodMap.get(reviewDate)!=null){
					goodMap.put(reviewDate,goodMap.get(reviewDate)+1);
				}else{
					goodMap.put(reviewDate, 1);
				}
			}else{
				if(badMap.get(reviewDate)!=null){
					badMap.put(reviewDate,badMap.get(reviewDate)+1);
				}else{
					badMap.put(reviewDate, 1);
				}
			}
			
			if(totalMap.get(reviewDate)!=null){
				totalMap.put(reviewDate,totalMap.get(reviewDate)+1);
			}else{
				totalMap.put(reviewDate, 1);
			}
		}   
		
		rs.add(goodMap);
		rs.add(badMap);
		rs.add(totalMap);
		return rs;
	}
	

	/**
	 *查询最新的评论
	 * 
	 */
	public Date  getLastCommDay(String country,String asin){
		String sql="SELECT a.`review_date` FROM amazoninfo_directory_comment_detail AS a  WHERE  a.`country`=:p1 AND a.`asin`=:p2  ORDER BY a.`review_date` DESC LIMIT 1";
		List<Date> list = this.directoryCommentDetailDao.findBySql(sql,new Parameter(country,asin));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}else{
			return new Date();
		}   
	}
	
}
