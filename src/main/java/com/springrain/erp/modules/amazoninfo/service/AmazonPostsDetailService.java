package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
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
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonCatalogRankDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonEanDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonNewReleasesRankDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonPostsDetailDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonProductTypeChargeDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonProductTypeCodeDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonCatalogRank;
import com.springrain.erp.modules.amazoninfo.entity.AmazonEan;
import com.springrain.erp.modules.amazoninfo.entity.AmazonNewReleasesRank;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsChange;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsDetail;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsFeed;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProductTypeCharge;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProductTypeCode;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductService;

@Component
@Transactional(readOnly = true)
public class AmazonPostsDetailService extends BaseService {

	@Autowired
	private AmazonPostsDetailDao amazonPostsDetailDao;
	@Autowired
	private AmazonCatalogRankDao amazonCatalogRankDao;
	@Autowired
	private PsiProductService		 psiProductService;
	@Autowired
	private AmazonProductService amazonProductService;
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	@Autowired
	private PsiInventoryService	psiInventoryService;
	@Autowired
	private AmazonNewReleasesRankDao amazonNewReleasesRankDao;
	@Autowired
	private  AmazonEanDao amazonEanDao;
	@Autowired
	private  AmazonProductTypeCodeDao amazonProductTypeCodeDao;
	@Autowired
	private AmazonProductTypeChargeDao amazonProductTypeChargeDao;
	
	@Autowired
	private SaleReportService saleReportService;
	
	public AmazonPostsDetail get(Integer id) {
		return amazonPostsDetailDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(List<AmazonPostsDetail> portsDetailList) {
		amazonPostsDetailDao.save(portsDetailList);
	}
	
	@Transactional(readOnly = false)
	public void saveNewReleasesRank(List<AmazonNewReleasesRank> newReleasesRank) {
		amazonNewReleasesRankDao.save(newReleasesRank);
	}
	
	public AmazonPostsDetail getDetailBySkuAndCountry(String country,String sku){
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.like("sku",sku+",%"),Restrictions.like("sku","%,"+sku),Restrictions.eq("sku",sku),Restrictions.like("sku","%,"+sku+",%")));
		dc.add(Restrictions.eq("country",country));
		Date date=new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
    	dc.add(Restrictions.ge("queryTime",date));
		dc.add(Restrictions.lt("queryTime",DateUtils.addDays(date,1)));
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		if(list.size()>0){
			return list.get(0);
		}else{
			int i=1;
			while(list.size()==0&&i<10){
				dc = amazonPostsDetailDao.createDetachedCriteria();
				dc.add(Restrictions.or(Restrictions.like("sku",sku+",%"),Restrictions.like("sku","%,"+sku),Restrictions.eq("sku",sku),Restrictions.like("sku","%,"+sku+",%")));
				dc.add(Restrictions.eq("country",country));
				date=DateUtils.addDays(date,-1);
				date.setHours(0);
				date.setMinutes(0);
				date.setSeconds(0);
		    	dc.add(Restrictions.ge("queryTime",date));
				dc.add(Restrictions.lt("queryTime",DateUtils.addDays(date,1)));
				list=amazonPostsDetailDao.find(dc);
				if(list.size()>0){
					return list.get(0);
				}else{
					i++;
				}
			}
			return null;
		}
	}
	
	
	public AmazonPostsDetail getRecentPostDetailEan(String country,String sku){
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.like("sku",sku+",%"),Restrictions.like("sku","%,"+sku),Restrictions.eq("sku",sku),Restrictions.like("sku","%,"+sku+",%")));
		dc.add(Restrictions.eq("country",country));
		//增加时间限制,避免全表查询
		dc.add(Restrictions.ge("queryTime", DateUtils.addDays(new Date(), -15)));
		dc.add(Restrictions.isNotNull("ean"));
		dc.addOrder(Order.desc("queryTime"));
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public AmazonPostsDetail getRecentPostDetailDesc(String country,String sku){
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.like("sku",sku+",%"),Restrictions.like("sku","%,"+sku),Restrictions.eq("sku",sku),Restrictions.like("sku","%,"+sku+",%")));
		dc.add(Restrictions.eq("country",country));
		//增加时间限制,避免全表查询
		dc.add(Restrictions.ge("queryTime", DateUtils.addDays(new Date(), -15)));
		dc.add(Restrictions.isNotNull("description"));
		dc.addOrder(Order.desc("queryTime"));
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public AmazonPostsDetail getRecentPostDetailPicture(String country,String sku){
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.like("sku",sku+",%"),Restrictions.like("sku","%,"+sku),Restrictions.eq("sku",sku),Restrictions.like("sku","%,"+sku+",%")));
		dc.add(Restrictions.eq("country",country));
		//增加时间限制,避免全表查询
		dc.add(Restrictions.ge("queryTime", DateUtils.addDays(new Date(), -15)));
		dc.add(Restrictions.isNotNull("picture1"));
		dc.addOrder(Order.desc("queryTime"));
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	
	public AmazonPostsDetail getRecentPostDetailParentSku(String country,String asin){
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		dc.add(Restrictions.eq("asin",asin));
		dc.add(Restrictions.eq("country",country));
		//增加时间限制,避免全表查询
		dc.add(Restrictions.ge("queryTime", DateUtils.addDays(new Date(), -15)));
		dc.add(Restrictions.isNotNull("sku"));
		dc.addOrder(Order.desc("queryTime"));
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public AmazonPostsDetail getRecentPostDetailKeyword(String country,String sku){
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.like("sku",sku+",%"),Restrictions.like("sku","%,"+sku),Restrictions.eq("sku",sku),Restrictions.like("sku","%,"+sku+",%")));
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.isNotNull("keyword1"));
		//增加时间限制,避免全表查询
		dc.add(Restrictions.ge("queryTime", DateUtils.addDays(new Date(), -15)));
		dc.addOrder(Order.desc("queryTime"));
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public AmazonPostsDetail getRecentPostDetailFeature(String country,String sku){
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.like("sku",sku+",%"),Restrictions.like("sku","%,"+sku),Restrictions.eq("sku",sku),Restrictions.like("sku","%,"+sku+",%")));
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.isNotNull("feature1"));
		//增加时间限制,避免全表查询
		dc.add(Restrictions.ge("queryTime", DateUtils.addDays(new Date(), -15)));
		dc.addOrder(Order.desc("queryTime"));
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public String  getTitle(String country,String asin){
		String sql="SELECT title FROM amazoninfo_business_report r WHERE r.`child_asin`=:p2 and r.`country`=:p1 ORDER BY r.`create_date` desc";
		List<String> list=amazonPostsDetailDao.findBySql(sql,new Parameter(country,asin));
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public AmazonPostsDetail getDetailByAsinAndCountry(String accountName,String asin){
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		dc.add(Restrictions.eq("asin",asin));
		dc.add(Restrictions.eq("accountName",accountName));
		Date date=new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
    	dc.add(Restrictions.ge("queryTime",date));
		dc.add(Restrictions.lt("queryTime",DateUtils.addDays(date,1)));
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		if(list.size()>0){
			return list.get(0);
		}else{
			int i=1;
			while(list.size()==0&&i<10){
				dc = amazonPostsDetailDao.createDetachedCriteria();
				dc.add(Restrictions.eq("asin",asin));
				dc.add(Restrictions.eq("accountName",accountName));
				date=DateUtils.addDays(date,-1);
				date.setHours(0);
				date.setMinutes(0);
				date.setSeconds(0);
		    	dc.add(Restrictions.ge("queryTime",date));
				dc.add(Restrictions.lt("queryTime",DateUtils.addDays(date,1)));
				list=amazonPostsDetailDao.find(dc);
				if(list.size()>0){
					return list.get(0);
				}else{
					i++;
				}
			}
			return null;
		}
	}
	
	public Map<String,AmazonPostsDetail> getPostsByCountry(String country){
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		Map<String,AmazonPostsDetail> map=Maps.newHashMap();
		dc.add(Restrictions.eq("country",country));
		Date date=new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
    	dc.add(Restrictions.ge("queryTime",date));
		dc.add(Restrictions.lt("queryTime",DateUtils.addDays(date,1)));
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		if(list.size()>0){
			for (AmazonPostsDetail detail : list) {
				map.put(detail.getAsin(), detail);
			}
			return map;
		}else{
			int i=1;
			while(list.size()==0&&i<10){
				dc = amazonPostsDetailDao.createDetachedCriteria();
				dc.add(Restrictions.eq("country",country));
				date=DateUtils.addDays(date,-1);
				date.setHours(0);
				date.setMinutes(0);
				date.setSeconds(0);
		    	dc.add(Restrictions.ge("queryTime",date));
				dc.add(Restrictions.lt("queryTime",DateUtils.addDays(date,1)));
				list=amazonPostsDetailDao.find(dc);
				if(list.size()>0){
					for (AmazonPostsDetail detail : list) {
						map.put(detail.getAsin(), detail);
					}
				}else{
					i++;
				}
			}
			return map;
		}
	}
	
	public Date findMaxDate(String country){
		String sqlString="select max(query_time) from amazoninfo_posts_detail where  country=:p1 ";
		List<Object> rs=amazonPostsDetailDao.findBySql(sqlString,new Parameter(country));
		if(rs.size()>0){
			return (Timestamp)rs.get(0);
		}
		return new Date();
	}
	
	@Transactional(readOnly = false)
	public void updateSelectItems(AmazonPostsFeed amazonPostsFeed){
		for (AmazonPostsChange item : amazonPostsFeed.getItems()) {
			String sqlString="select max(query_time) from amazoninfo_posts_detail where  account_name=:p1 and asin=:p2 ";
			List<Object> rs=amazonPostsDetailDao.findBySql(sqlString,new Parameter(amazonPostsFeed.getAccountName(),item.getAsin()));
			if(rs.size()>0){
				Date date=(Timestamp)rs.get(0);
				if(date!=null){
					String sql="update amazoninfo_posts_detail set label=label,";
					List<Object> list=Lists.newArrayList();
					int i=4;
					if(StringUtils.isNotBlank(item.getDescription())){
						list.add(item.getDescription().replaceAll("'", "''"));
						sql+=" description=:p"+(i++)+", ";
					}
					if(StringUtils.isNotBlank(item.getTitle())){
						list.add(item.getTitle().replaceAll("'", "''"));
						sql+=" title=:p"+(i++)+", ";
					}
					if(StringUtils.isNotBlank(item.getKeyword1())){
						list.add(item.getKeyword1().replaceAll("'", "''"));
						list.add(item.getKeyword2().replaceAll("'", "''"));
						list.add(item.getKeyword3().replaceAll("'", "''"));
						list.add(item.getKeyword4().replaceAll("'", "''"));
						list.add(item.getKeyword5().replaceAll("'", "''"));
						sql+=" keyword1=:p"+(i++)+",keyword2=:p"+(i++)+",keyword3=:p"+(i++)+",keyword4=:p"+(i++)+",keyword5=:p"+(i++)+", ";
						
					}
					if(StringUtils.isNotBlank(item.getFeature1())){
						list.add(item.getFeature1().replaceAll("'", "''"));
						list.add(item.getFeature2().replaceAll("'", "''"));
						list.add(item.getFeature3().replaceAll("'", "''"));
						list.add(item.getFeature4().replaceAll("'", "''"));
						list.add(item.getFeature5().replaceAll("'", "''"));
						sql+=" feature1=:p"+(i++)+",feature2=:p"+(i++)+",feature3=:p"+(i++)+",feature4=:p"+(i++)+",feature5=:p"+(i++)+", ";
					}
					if(item.getPackageLength()!=null&&item.getPackageLength()>0){
						list.add(item.getPackageLength());
						sql+=" package_length=:p"+(i++)+",";
					}
					if(item.getPackageWidth()!=null&&item.getPackageWidth()>0){
						list.add(item.getPackageWidth());
						sql+=" package_width= :p"+(i++)+",";
					}
					if(item.getPackageHeight()!=null&&item.getPackageHeight()>0){
						list.add(item.getPackageHeight());
						sql+=" package_height= :p"+(i++)+",";
					}
					if(item.getPackageWeight()!=null&&item.getPackageWeight()>0){
						list.add(item.getPackageWeight());
						sql+=" package_weight=:p"+(i++)+",";
					}
					
					if(StringUtils.isNotBlank(item.getBrand())){
						list.add(item.getBrand().replaceAll("'", "''"));
						sql+=" brand=:p"+(i++)+", ";
					}
					
					if(StringUtils.isNotBlank(item.getPartNumber())){
						list.add(item.getPartNumber().replaceAll("'", "''"));
						sql+=" part_number=:p"+(i++)+", ";
					}
					
					if(StringUtils.isNotBlank(item.getManufacturer())){
						list.add(item.getManufacturer().replaceAll("'", "''"));
						sql+=" manufacturer=:p"+(i++)+", ";
					}
					sql+=" label=label where account_name=:p1 and asin=:p2 and DATE_FORMAT(query_time,'%Y-%m-%d')=:p3 ";
					list.add(0,amazonPostsFeed.getAccountName());
					list.add(1,item.getAsin());
					list.add(2,new SimpleDateFormat("yyyy-MM-dd").format(date));
					amazonPostsDetailDao.updateBySql(sql, new Parameter(list.toArray(new Object[list.size()])));
				}
			}
			
		}
		
	}
	
	public  Map<String,List<String>> findCatalogByCountry(){
		Map<String,List<String>> map=Maps.newHashMap();
		String sql="SELECT country,MAX(query_time) FROM amazoninfo_catalog_rank GROUP BY country";
		String sql2="SELECT DISTINCT(r.`catalog`) FROM amazoninfo_catalog_rank r WHERE r.`country`=:p1 AND r.`query_time`=:p2 ";
		List<Object[]> rs=amazonCatalogRankDao.findBySql(sql);
		for (Object[] obj: rs) {
			String country=obj[0].toString();
			Date date=(Timestamp)obj[1];
			List<String> catalogList=amazonCatalogRankDao.findBySql(sql2,new Parameter(country,date));
			map.put(country,catalogList);
		}
	    return map;	
	}
	
	
	@Transactional(readOnly = false)
	public void updatePostsDetail(AmazonPostsFeed amazonPostsFeed){
		String sqlString="select max(query_time) from amazoninfo_posts_detail where account_name=:p1 ";
		List<Object> rs=amazonPostsDetailDao.findBySql(sqlString,new Parameter(amazonPostsFeed.getAccountName()));
		Date date=(Timestamp)rs.get(0);
		List<AmazonPostsDetail> saveList=Lists.newArrayList();
		for (AmazonPostsChange item : amazonPostsFeed.getItems()) {
			String sqlString2="select 1 from amazoninfo_posts_detail where  account_name=:p1 and asin=:p2 and query_time=:p3 ";
			List<Object> rs2=amazonPostsDetailDao.findBySql(sqlString2,new Parameter(amazonPostsFeed.getAccountName(),item.getAsin(),new SimpleDateFormat("yyyy-MM-dd").format(date)));
			if(rs2.size()>0){
				String sql="update amazoninfo_posts_detail set label=label,";
				List<Object> list=Lists.newArrayList();
				int i=4;
				if(StringUtils.isNotBlank(item.getDescription())){
					list.add(item.getDescription().replaceAll("'", "''"));
					sql+=" description=:p"+(i++)+", ";
				}
				if(StringUtils.isNotBlank(item.getTitle())){
					list.add(item.getTitle().replaceAll("'", "''"));
					sql+=" title=:p"+(i++)+", ";
				}
				if(StringUtils.isNotBlank(item.getKeyword1())){
					list.add(item.getKeyword1().replaceAll("'", "''"));
					list.add(item.getKeyword2().replaceAll("'", "''"));
					list.add(item.getKeyword3().replaceAll("'", "''"));
					list.add(item.getKeyword4().replaceAll("'", "''"));
					list.add(item.getKeyword5().replaceAll("'", "''"));
					sql+=" keyword1=:p"+(i++)+",keyword2=:p"+(i++)+",keyword3=:p"+(i++)+",keyword4=:p"+(i++)+",keyword5=:p"+(i++)+", ";
					
				}
				if(StringUtils.isNotBlank(item.getFeature1())){
					list.add(item.getFeature1().replaceAll("'", "''"));
					list.add(item.getFeature2().replaceAll("'", "''"));
					list.add(item.getFeature3().replaceAll("'", "''"));
					list.add(item.getFeature4().replaceAll("'", "''"));
					list.add(item.getFeature5().replaceAll("'", "''"));
					sql+=" feature1=:p"+(i++)+",feature2=:p"+(i++)+",feature3=:p"+(i++)+",feature4=:p"+(i++)+",feature5=:p"+(i++)+", ";
				}
				if(item.getPackageLength()!=null&&item.getPackageLength()>0){
					list.add(item.getPackageLength());
					sql+=" package_length=:p"+(i++)+",";
				}
				if(item.getPackageWidth()!=null&&item.getPackageWidth()>0){
					list.add(item.getPackageWidth());
					sql+=" package_width= :p"+(i++)+",";
				}
				if(item.getPackageHeight()!=null&&item.getPackageHeight()>0){
					list.add(item.getPackageHeight());
					sql+=" package_height= :p"+(i++)+",";
				}
				if(item.getPackageWeight()!=null&&item.getPackageWeight()>0){
					list.add(item.getPackageWeight());
					sql+=" package_weight=:p"+(i++)+",";
				}
				
				if(StringUtils.isNotBlank(item.getBrand())){
					list.add(item.getBrand().replaceAll("'", "''"));
					sql+=" brand=:p"+(i++)+", ";
				}
				
				if(StringUtils.isNotBlank(item.getPartNumber())){
					list.add(item.getPartNumber().replaceAll("'", "''"));
					sql+=" part_number=:p"+(i++)+", ";
				}
				
				if(StringUtils.isNotBlank(item.getManufacturer())){
					list.add(item.getManufacturer().replaceAll("'", "''"));
					sql+=" manufacturer=:p"+(i++)+", ";
				}
				sql+=" label=label where account_name=:p1 and asin=:p2 and DATE_FORMAT(query_time,'%Y-%m-%d')=:p3 ";
				list.add(0,amazonPostsFeed.getAccountName());
				list.add(1,item.getAsin());
				list.add(2,new SimpleDateFormat("yyyy-MM-dd").format(date));
				amazonPostsDetailDao.updateBySql(sql, new Parameter(list.toArray(new Object[list.size()])));
			}else{
				AmazonPostsDetail detail=new AmazonPostsDetail();
				detail.setAsin(item.getAsin());
				detail.setSku(item.getSku());
				detail.setCountry(amazonPostsFeed.getCountry());
				detail.setAccountName(amazonPostsFeed.getAccountName());
				detail.setQueryTime(findMaxDate(amazonPostsFeed.getCountry()));
				detail.setCreateTime(new Date());
				detail.setProductName(amazonProductService.findProductName(item.getAsin(),amazonPostsFeed.getCountry()));
				detail.setBinding(item.getBinding());
				detail.setBrand(item.getBrand());
				detail.setManufacturer(item.getManufacturer());
				detail.setTitle(item.getTitle());
				detail.setPackageLength(item.getPackageLength());
				detail.setPackageWidth(item.getPackageWidth());
				detail.setPackageHeight(item.getPackageHeight());
				detail.setPackageWeight(item.getPackageWeight());
				detail.setFeature1(item.getFeature1());
				detail.setFeature2(item.getFeature2());
				detail.setFeature3(item.getFeature3());
				detail.setFeature4(item.getFeature4());
				detail.setFeature5(item.getFeature5());
				detail.setPartNumber(item.getPartNumber());
				detail.setKeyword1(item.getKeyword1());
				detail.setKeyword2(item.getKeyword2());
				detail.setKeyword3(item.getKeyword3());
				detail.setKeyword4(item.getKeyword4());
				detail.setKeyword5(item.getKeyword5());
				detail.setDescription(item.getDescription());
				detail.setEan(item.getEan());
				saveList.add(detail);
			}
		}
		if(saveList!=null&&saveList.size()>0){
			amazonPostsDetailDao.save(saveList);
		}
	}
	
	public String getDescription(String accountName,String asin){
		String sql="SELECT description FROM amazoninfo_posts_detail WHERE account_name=:p1 and asin=:p2 and  DATE_FORMAT(query_time,'%Y-%m-%d') =:p3  ";
		Date today=DateUtils.addDays(new Date(),-1);
		List<String> list=amazonPostsDetailDao.findBySql(sql, new Parameter(accountName,asin,new SimpleDateFormat("yyyy-MM-dd").format(today)));
		if(list.size()>0){
			   return list.get(0);
		}
		int i = 1;
		while(list.size()==0&&i<10){
		   list=amazonPostsDetailDao.findBySql(sql, new Parameter(accountName,asin,new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(today, 0-i))));
		   if(list.size()>0){
			   return list.get(0);
		   }else{
			   i++;	 
		   }
		}
		return null;
	}
	
	public Map<String,String> getTitleMap(String country){
		Map<String,String> rs = Maps.newHashMap();
		if(StringUtils.isNotBlank(country)){
			String sql="SELECT asin,title FROM amazoninfo_posts_detail WHERE title is not null and  country=:p1  and  DATE_FORMAT(query_time,'%Y-%m-%d') =:p2  ";
			Date today=DateUtils.addDays(new Date(),-1);
			List<Object[]> list=amazonPostsDetailDao.findBySql(sql, new Parameter(country,new SimpleDateFormat("yyyy-MM-dd").format(today)));
			int i = 1;
			while(list.size()==0&&i<10){
			   list=amazonPostsDetailDao.findBySql(sql, new Parameter(country,new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(today, 0-i))));
			   if(list.size()==0){
				   i++;	 
			   }
			}
			if(list.size()>0){
				for (Object[] objects : list) {
					rs.put(objects[0].toString(), objects[1].toString());
				}
			}
		}else{
			String sql="SELECT CONCAT(asin,'_',country),title FROM amazoninfo_posts_detail WHERE title is not null and   DATE_FORMAT(query_time,'%Y-%m-%d') =:p1  ";
			Date today=DateUtils.addDays(new Date(),-1);
			List<Object[]> list=amazonPostsDetailDao.findBySql(sql, new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(today)));
			int i = 1;
			while(list.size()==0&&i<10){
			   list=amazonPostsDetailDao.findBySql(sql, new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(today, 0-i))));
			   if(list.size()==0){
				   i++;	 
			   }
			}
			if(list.size()>0){
				for (Object[] objects : list) {
					rs.put(objects[0].toString(), objects[1].toString());
				}
			}
		}
		return rs;
	}
	
	
	public AmazonPostsDetail getKeyWord(String accountName,String asin){
		String sql="SELECT keyword1,keyword2,keyword3,keyword4,keyword5 FROM amazoninfo_posts_detail WHERE account_name=:p1 and asin=:p2 and DATE_FORMAT(query_time,'%Y-%m-%d') =:p3  ";
		Date today=DateUtils.addDays(new Date(),-1);
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql, new Parameter(accountName,asin,new SimpleDateFormat("yyyy-MM-dd").format(today)));
		AmazonPostsDetail detail=new AmazonPostsDetail();
		if(list.size()>0){
			 for (Object[] obj : list) {
				 detail.setKeyword1(obj[0]==null?"":obj[0].toString());
				 detail.setKeyword2(obj[1]==null?"":obj[1].toString());
				 detail.setKeyword3(obj[2]==null?"":obj[2].toString());
				 detail.setKeyword4(obj[3]==null?"":obj[3].toString());
				 detail.setKeyword5(obj[4]==null?"":obj[4].toString());
				 return detail;
			 }
		}
		int i =1 ;
		while(list.size()==0&&i<10){
		   list=amazonPostsDetailDao.findBySql(sql, new Parameter(accountName,asin,new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(today, 0-i))));
		   if(list.size()>0){
			   for (Object[] obj : list) {
					 detail.setKeyword1(obj[0]==null?"":obj[0].toString());
					 detail.setKeyword2(obj[1]==null?"":obj[1].toString());
					 detail.setKeyword3(obj[2]==null?"":obj[2].toString());
					 detail.setKeyword4(obj[3]==null?"":obj[3].toString());
					 detail.setKeyword5(obj[4]==null?"":obj[4].toString());
					 return detail;
			   }
		   }else{
			   i++;	 
		   }
		}
		return detail;
	}
	
	/*public List<String> getAsinList(String country){
		String sql="SELECT DISTINCT ASIN FROM amazoninfo_posts_detail WHERE country=:p1 and DATE_FORMAT(query_time,'%Y-%m-%d')=DATE_FORMAT(NOW(),'%Y-%m-%d') ";
		return amazonPostsDetailDao.findBySql(sql, new Parameter(country));
	}*/
	
	public List<String> getAsinList(String country){
		String sql="SELECT DISTINCT ASIN FROM amazoninfo_product2 WHERE country=:p1 and active='1' and asin is not null ";
		return amazonPostsDetailDao.findBySql(sql, new Parameter(country));
	}
	
	
	public List<AmazonPostsDetail> getProductNameList1(String accountName){
		String sql="SELECT DISTINCT p.asin,CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN CONCAT ('_',b.`color`) ELSE '' END) NAME,GROUP_CONCAT(p.sku) FROM amazoninfo_product2 p "+
                 " JOIN psi_sku b ON b.`sku`=p.`sku` AND p.`account_name` = b.`account_name` AND  b.`del_flag` = '0' "+
				 " WHERE p.account_name=:p1 AND p.active='1' and  p.asin is not null "+
				 " GROUP BY p.asin,NAME HAVING (NAME!='Inateck other' AND NAME!='Inateck Old')";
		
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql,new Parameter(accountName));
		List<AmazonPostsDetail> posts=Lists.newArrayList();
		if(list!=null&&list.size()>0){
			for (Object[] obj : list) {
					AmazonPostsDetail detail=new AmazonPostsDetail();
					detail.setAsin(obj[0].toString());
					detail.setSku(obj[2].toString());
					detail.setProductName(obj[1]==null?"":obj[1].toString());
					posts.add(detail);
			}
		}
		return posts;
	}
	
	public Map<String,Map<String,AmazonPostsDetail>> findCountryProductName(Set<String> countrySet,String productName){
		 Map<String,Map<String,AmazonPostsDetail>> map=Maps.newHashMap();
		 List<Object[]> list=null;
		 
		 if(StringUtils.isNotBlank(productName)){
			 String sql="SELECT DISTINCT p.account_name,p.asin,CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN CONCAT ('_',b.`color`) ELSE '' END) NAME,GROUP_CONCAT(p.sku) FROM amazoninfo_product2 p "+
						" JOIN psi_sku b ON b.`sku`=p.`sku` AND p.`account_name` = b.`account_name` AND  b.`del_flag` = '0'  "+
						" WHERE p.account_name IN :p1 and CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN CONCAT ('_',b.`color`) ELSE '' END)=:p2 AND p.active='1' AND  p.asin IS NOT NULL and  product_name!='Inateck other' and  product_name!='Inateck Old' "+
						" GROUP BY p.account_name,p.asin,NAME ";
			 list=amazonPostsDetailDao.findBySql(sql,new Parameter(countrySet,productName));
		 }else{
			 String sql="SELECT DISTINCT p.account_name,p.asin,CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN CONCAT ('_',b.`color`) ELSE '' END) NAME,GROUP_CONCAT(p.sku) FROM amazoninfo_product2 p "+
						" JOIN psi_sku b ON b.`sku`=p.`sku` AND p.`account_name` = b.`account_name` AND  b.`del_flag` = '0'  "+
						" WHERE p.account_name IN :p1 AND p.active='1' AND  p.asin IS NOT NULL and  product_name!='Inateck other' and  product_name!='Inateck Old' "+
						" GROUP BY p.account_name,p.asin,NAME ";
			 list=amazonPostsDetailDao.findBySql(sql,new Parameter(countrySet));
		 }
		 if(list!=null&&list.size()>0){
			 for (Object[] obj : list) {
				 String accountName=obj[0].toString();
				 String asin=obj[1].toString();
				 String name=obj[2].toString();
				 String sku=obj[3].toString();
				 
				 Map<String,AmazonPostsDetail> temp=map.get(name);
				 if(temp==null){
					 temp=Maps.newHashMap();
					 map.put(name, temp);
				 }
				AmazonPostsDetail detail=new AmazonPostsDetail();
				detail.setAsin(asin);
				detail.setSku(sku);
				detail.setProductName(name);
				detail.setAccountName(accountName);
				temp.put(accountName, detail);
			 }
		 }
		 return map;
	}
	
	public List<AmazonPostsDetail> getProductNameList(String accountName){
		Date today = DateUtils.getDateStart(new Date());
		String sql="SELECT  ASIN,product_name,sku,part_number FROM amazoninfo_posts_detail WHERE product_name is not null and account_name=:p1 and query_time =:p2 and asin is not null ";
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql, new Parameter(accountName,today));
		List<AmazonPostsDetail> rs=Lists.newArrayList();
		if(list.size()>0){
				for (Object[] obj : list) {
					AmazonPostsDetail detail=new AmazonPostsDetail();
					detail.setAsin(obj[0].toString());
					detail.setProductName(obj[1]==null?"":obj[1].toString());
					detail.setSku(obj[2].toString());
					detail.setPartNumber(obj[3]==null?"":obj[3].toString());
					rs.add(detail);
				}
				return rs;
		   }
		int i = 1 ;
		while(list.size()==0&&i<10){
		   list=amazonPostsDetailDao.findBySql(sql, new Parameter(accountName,DateUtils.addDays(today, 0-i)));
		   if(list.size()>0){
				for (Object[] obj : list) {
					AmazonPostsDetail detail=new AmazonPostsDetail();
					detail.setAsin(obj[0].toString());
					detail.setProductName(obj[1]==null?"":obj[1].toString());
					detail.setSku(obj[2].toString());
					detail.setPartNumber(obj[3]==null?"":obj[3].toString());
					rs.add(detail);
				}
				return rs;
		   }else{
			   i++;	 
		   }
		}
		return rs;
	}
	
	
	public List<AmazonPostsDetail> getAllSkuList(Set<String> country,String suffix){
		
		Date today = DateUtils.getDateStart(new Date());
		String sql="SELECT  DISTINCT ASIN,sku,fulfillable_quantity,country,account_name FROM psi_inventory_fba WHERE country IN :p1 AND  DATE_FORMAT(data_date,'%Y-%m-%d')=DATE_FORMAT(:p2,'%Y-%m-%d') and account_name like :p3";
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql, new Parameter(country,today,suffix+"%"));
		List<AmazonPostsDetail> rs=Lists.newArrayList();
		if(list.size()>0){
				for (Object[] obj : list) {
					String[] skuArr=obj[1].toString().split(",");
					for (String sku : skuArr) {
						AmazonPostsDetail detail=new AmazonPostsDetail();
						detail.setAsin(obj[0].toString());
						//detail.setEan(obj[1]==null?"":obj[1].toString());
						detail.setQuantity(Integer.parseInt(obj[2].toString()));
						detail.setSku(sku);
						detail.setCountry(obj[3].toString());
						detail.setAccountName(obj[4].toString());
						rs.add(detail);
					}
				}
				return rs;
		   }
		int i = 1 ;
		while(list.size()==0&&i<10){
		   list=amazonPostsDetailDao.findBySql(sql, new Parameter(country,DateUtils.addDays(today, 0-i),suffix+"%"));
		   if(list.size()>0){
				for (Object[] obj : list) {
					String[] skuArr=obj[1].toString().split(",");
					for (String sku : skuArr) {
						AmazonPostsDetail detail=new AmazonPostsDetail();
						detail.setAsin(obj[0].toString());
						//detail.setEan(obj[1]==null?"":obj[1].toString());
						detail.setQuantity(Integer.parseInt(obj[2].toString()));
						detail.setSku(sku);
						detail.setCountry(obj[3].toString());
						detail.setAccountName(obj[4].toString());
						rs.add(detail);
					}
				}
				return rs;
		   }else{
			   i++;	 
		   }
		}
		return rs;
	}
	
	public List<AmazonPostsDetail> getAllProductNameList(String accountName){
		String sql="SELECT DISTINCT p.ASIN,CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN CONCAT ('_',b.`color`) ELSE '' END),p.sku FROM amazoninfo_product2 p "+
        " JOIN psi_sku b ON b.`sku`=p.`sku` AND p.`account_name` = b.`account_name` AND  b.`del_flag` = '0' "+
        " WHERE p.account_name=:p1 AND p.active='1'  and p.ASIN is not null ";
		//String sql="SELECT  ASIN,product_name,sku FROM amazoninfo_posts_detail WHERE sku is not null and country=:p1 and query_time =:p2 ";
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql, new Parameter(accountName));
		List<AmazonPostsDetail> rs=Lists.newArrayList();
		if(list!=null&&list.size()>0){
				for (Object[] obj : list) {
						AmazonPostsDetail detail=new AmazonPostsDetail();
						detail.setAsin(obj[0].toString());
						detail.setProductName(obj[1]==null?"":obj[1].toString());
						detail.setSku(obj[2].toString());
						rs.add(detail);
				}
		}
		return rs;
	}
	
	public Map<String,List<AmazonCatalogRank>> getChangeRankMap(){
		String sql="SELECT ASIN,country,product_name,catalog_name,max(rank),min(rank),GROUP_CONCAT(rank ORDER BY query_time DESC) rank,catalog FROM amazoninfo_catalog_rank "+
			" WHERE catalog not like '%on_website' AND product_name is not null and "+
			" (DATE_FORMAT(query_time,'%Y-%m-%d')=DATE_FORMAT((DATE_SUB(NOW(),INTERVAL 1 DAY)),'%Y-%m-%d')  "+
			" OR DATE_FORMAT(query_time,'%Y-%m-%d')=DATE_FORMAT(NOW(),'%Y-%m-%d')  "+
			" ) GROUP BY ASIN,country,product_name,catalog_name,catalog having max(rank)!=min(rank) and MIN(rank)<=100  ORDER BY catalog_name,MAX(rank)-MIN(rank) desc ";
		//List<AmazonCatalogRank> rankList=Lists.newArrayList();
		Map<String,List<AmazonCatalogRank>> map=Maps.newHashMap();
		List<Object[]> list=amazonCatalogRankDao.findBySql(sql);		
		for (Object[] obj : list) {
			AmazonCatalogRank rank=new AmazonCatalogRank();
			rank.setAsin(obj[0].toString());
			rank.setCountry(obj[1].toString());
			rank.setProductName(obj[2].toString());
			rank.setCatalogName(obj[3]==null?"":obj[3].toString());
			rank.setMaxRank(Integer.parseInt(obj[4].toString()));
			rank.setMinRank(Integer.parseInt(obj[5].toString()));
			rank.setRankStr(obj[6].toString());
			rank.setCatalog(obj[7].toString());
			//rankList.add(rank);
			List<AmazonCatalogRank> catalogRankList=map.get(rank.getCountry());
	       	if(catalogRankList==null||catalogRankList.size()==0){
	       		 catalogRankList=Lists.newArrayList();
	       		 map.put(rank.getCountry(),catalogRankList);
	       	}
	       	catalogRankList.add(rank);
		}
		return map;
	}
	
	public Map<String,List<AmazonPostsDetail>> getLowStar(){
		Map<String,List<AmazonPostsDetail>> map=Maps.newHashMap();
		String sql=" select asin,country,product_name,star,star1,star2,star3,star4,star5 FROM amazoninfo_posts_detail where product_name IS NOT NULL and star is not null and star!=0 and star<4 and DATE_FORMAT(query_time,'%Y-%m-%d')=DATE_FORMAT(NOW(),'%Y-%m-%d') order by star ";
		List<Object[]> list=amazonCatalogRankDao.findBySql(sql);
		//产品淘汰分平台、颜色
		Map<String, String> productPosttionMap = psiProductEliminateService.findAllProductPosition();
		for (Object[] obj : list) {
//			PsiProduct product=psiProductService.findProductByProductName(obj[2].toString());
//			if("1".equals(product.getIsSale())){
			if(!"4".equals(productPosttionMap.get(obj[2].toString() + "_" + obj[1].toString()))){
					AmazonPostsDetail detail=new AmazonPostsDetail();
					detail.setAsin(obj[0].toString());
					detail.setCountry(obj[1].toString());
					detail.setProductName(obj[2].toString());
					detail.setStar(Float.parseFloat(obj[3].toString()));
					detail.setStar1(Integer.parseInt(obj[4]==null?"0":obj[4].toString()));
					detail.setStar2(Integer.parseInt(obj[5]==null?"0":obj[5].toString()));
					detail.setStar3(Integer.parseInt(obj[6]==null?"0":obj[6].toString()));
					detail.setStar4(Integer.parseInt(obj[7]==null?"0":obj[7].toString()));
					detail.setStar5(Integer.parseInt(obj[8]==null?"0":obj[8].toString()));
					List<AmazonPostsDetail> data=map.get(detail.getCountry());
					if(data==null){
						data=Lists.newArrayList();
						map.put(detail.getCountry(),data);
					}
					data.add(detail);
			}	
		}
		return map;
	}
	
	public Map<String,Map<String,AmazonPostsDetail>> getChangeStar(){
	  String sql="SELECT ASIN,country,product_name,GROUP_CONCAT(star ORDER BY query_time DESC) star,GROUP_CONCAT((star1+star2+star3+star4+star5) ORDER BY query_time DESC) total "+
			 " FROM amazoninfo_posts_detail WHERE product_name IS NOT NULL and star is not null and star!=0 "+
			 "  AND (DATE_FORMAT(query_time,'%Y-%m-%d')=DATE_FORMAT((DATE_SUB(NOW(),INTERVAL 1 DAY)),'%Y-%m-%d')  "+
			 " OR DATE_FORMAT(query_time,'%Y-%m-%d')=DATE_FORMAT(NOW(),'%Y-%m-%d') ) "+
			"  GROUP BY ASIN,country,product_name HAVING MAX(star)!=MIN(star) order by country,MAX(star)-MIN(star) desc ";
	  Map<String,Map<String,AmazonPostsDetail>> map=Maps.newLinkedHashMap();
	  List<Object[]> list=amazonCatalogRankDao.findBySql(sql);
	  //产品淘汰分平台、颜色
		Map<String, String> productPositionMap = psiProductEliminateService.findAllProductPosition();
		for (Object[] obj : list) {
			if(!"4".equals(productPositionMap.get(obj[2].toString() + "_" + obj[1].toString()))){
			  AmazonPostsDetail detail=new AmazonPostsDetail();
			  detail.setAsin(obj[0].toString());
			  detail.setCountry(obj[1].toString());
			  detail.setProductName(obj[2].toString());
			  detail.setCompareStar(obj[3].toString());
			  if(obj[4]!=null){
				  String[] arr=obj[4].toString().split(",");
				  detail.setStar1(Integer.parseInt(arr[0]));
			  }
			  Map<String,AmazonPostsDetail> data=map.get(detail.getCountry());
			  if(data==null){
				  data=Maps.newLinkedHashMap();
				  map.put(detail.getCountry(), data);
			  }
			  AmazonPostsDetail post=data.get(detail.getAsin());
			  if(post==null){
				  data.put(detail.getAsin(),detail);
			  }
		  }
		 
	  }
	  return map;	
	}
	

	
	public Map<String,Map<String,List<AmazonCatalogRank>>> getChangeRankMap2(){
		String sql="SELECT ASIN,country,product_name,catalog_name,max(rank),min(rank),GROUP_CONCAT(rank ORDER BY query_time DESC) rank,catalog FROM amazoninfo_catalog_rank "+
			" WHERE catalog not like '%on_website' AND product_name is not null and "+
			" (DATE_FORMAT(query_time,'%Y-%m-%d')=DATE_FORMAT((DATE_SUB(NOW(),INTERVAL 1 DAY)),'%Y-%m-%d')  "+
			" OR DATE_FORMAT(query_time,'%Y-%m-%d')=DATE_FORMAT(NOW(),'%Y-%m-%d')  "+
			" ) GROUP BY ASIN,country,product_name,catalog_name,catalog having max(rank)!=min(rank) and MIN(rank)<=20 and MAX(rank)-MIN(rank)>=5  ORDER BY country,MAX(rank)-MIN(rank) desc,catalog_name ";
		Map<String,Map<String,List<AmazonCatalogRank>>> map=Maps.newHashMap();
		List<Object[]> list=amazonCatalogRankDao.findBySql(sql);
		//产品淘汰分平台、颜色
		Map<String, String> productPositionMap = psiProductEliminateService.findAllProductPosition();
		for (Object[] obj : list) {
			if(!"4".equals(productPositionMap.get(obj[2].toString() + "_" + obj[1].toString()))){
				AmazonCatalogRank rank=new AmazonCatalogRank();
				rank.setAsin(obj[0].toString());
				rank.setCountry(obj[1].toString());
				rank.setProductName(obj[2].toString());
				rank.setCatalogName(obj[3]==null?"":obj[3].toString());
				rank.setMaxRank(Integer.parseInt(obj[4].toString()));
				rank.setMinRank(Integer.parseInt(obj[5].toString()));
				rank.setRankStr(obj[6].toString());
				rank.setCatalog(obj[7].toString());
				Map<String,List<AmazonCatalogRank>> catalogRankMap=map.get(rank.getCountry());
		       	if(catalogRankMap==null){
		       		catalogRankMap=Maps.newLinkedHashMap();
		       		map.put(rank.getCountry(), catalogRankMap);
		       	}
		       	List<AmazonCatalogRank> rankList=catalogRankMap.get(rank.getProductName());
		       	if(rankList==null){
		       		rankList=Lists.newArrayList();
		       		catalogRankMap.put(rank.getProductName(), rankList);
		       	}
		        rankList.add(rank);
			}
		}
		return map;
	}
	

	
	
	public AmazonPostsDetail getDetailByProductName(String productName,String country,String asin,Date date){
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		dc.add(Restrictions.eq("productName",productName));
		dc.add(Restrictions.eq("asin",asin));
		dc.add(Restrictions.eq("country",StringUtils.isBlank(country)?"de":country));
		if(date==null){
			date=new Date();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
		}
		dc.add(Restrictions.ge("queryTime",date));
		dc.add(Restrictions.lt("queryTime",DateUtils.addDays(date,1)));
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	
	/*public AmazonPostsDetail getLast2day(Integer id,Date date) {
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		dc.add(Restrictions.eq("id",id));
		dc.createAlias("this.rankItems", "items");
		dc.add(Restrictions.ge("items.queryTime",date));
		dc.add(Restrictions.le("items.queryTime",DateUtils.addDays(date,1)));
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}*/
	
	public Page<AmazonPostsDetail> find(Page<AmazonPostsDetail> page, AmazonPostsDetail amazonPostsDetail) {
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		Date date = amazonPostsDetail.getQueryTime();
		if(date==null){
			date = new Date();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			amazonPostsDetail.setQueryTime(date);
		}
		if (StringUtils.isNotEmpty(amazonPostsDetail.getAsin())){
			dc.add(Restrictions.or(Restrictions.like("productName", "%"+amazonPostsDetail.getAsin()+"%"),Restrictions.like("asin", "%"+amazonPostsDetail.getAsin()+"%")));
		}else{
			dc.add(Restrictions.isNull("parentPortsDetail"));
		}
		if(StringUtils.isNotEmpty(amazonPostsDetail.getCountry())){
			dc.add(Restrictions.eq("country",amazonPostsDetail.getCountry()));
		}
		dc.add(Restrictions.ge("queryTime",amazonPostsDetail.getQueryTime()));
		dc.add(Restrictions.lt("queryTime",DateUtils.addDays(amazonPostsDetail.getQueryTime(),1)));
		dc.addOrder(Order.asc("productName"));
		return amazonPostsDetailDao.find(page, dc);
	}
	
	public AmazonPostsDetail getPortsDetail(AmazonPostsDetail amazonPostsDetail){
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		Date date = amazonPostsDetail.getQueryTime();
		if(date==null){
			date = new Date();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			amazonPostsDetail.setQueryTime(date);
		}
		dc.add(Restrictions.eq("asin",amazonPostsDetail.getAsin()));
		dc.add(Restrictions.eq("country",amazonPostsDetail.getCountry()));
		dc.add(Restrictions.ge("queryTime",amazonPostsDetail.getQueryTime()));
		dc.add(Restrictions.lt("queryTime",DateUtils.addDays(amazonPostsDetail.getQueryTime(),1)));
		/*dc.createAlias("this.rankItems", "items");
		dc.add(Restrictions.ne("items.catalog","ce_display_on_website"));
		dc.add(Restrictions.ne("items.catalog","pc_display_on_website"));
		dc.addOrder(Order.asc("items.rank"));*/
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	/*public Map<String,Map<String,AmazonCatalogRank>> getRank(String productName,Date start,Date end) {
		DetachedCriteria dc = amazonCatalogRankDao.createDetachedCriteria();
		dc.add(Restrictions.ge("queryTime",start));
		dc.add(Restrictions.le("queryTime",end));
		dc.add(Restrictions.eq("productName",productName));
		dc.add(Restrictions.ne("catalog","ce_display_on_website"));
		dc.add(Restrictions.isNotNull("catalogName"));
		Map<String,Map<String,AmazonCatalogRank>> rs = Maps.newHashMap();
		List<AmazonCatalogRank> list = amazonCatalogRankDao.find(dc);
		for (AmazonCatalogRank rank : list) {
			String catalog=rank.getCatalogName();
			String key = rank.getAsin()+"_"+rank.getCountry()+"_"+dateFormat.format(rank.getQueryTime());
			Map<String,AmazonCatalogRank> data = rs.get(key);
			if(data==null){
					data = Maps.newHashMap();
					rs.put(key, data);
			}
			data.put(catalog, rank);
		}
		return rs;
	}*/
	
	public Map<String,Map<String,AmazonCatalogRank>> getRank2(String productName,Date start,Date end,String cty) {
		/*DetachedCriteria dc = amazonCatalogRankDao.createDetachedCriteria();
		dc.add(Restrictions.ge("queryTime",start));
		dc.add(Restrictions.le("queryTime",end));
		dc.add(Restrictions.eq("productName",productName));
		dc.add(Restrictions.ne("catalog","ce_display_on_website"));
		dc.add(Restrictions.ne("catalog","pc_display_on_website"));*/
		
		Map<String,Map<String,AmazonCatalogRank>> rs = Maps.newHashMap();
		String sql="SELECT asin,country,DATE_FORMAT(DATE_ADD(query_time, INTERVAL -1 DAY),'%Y%m%d'),catalog,rank FROM amazoninfo_catalog_rank WHERE  product_name=:p1  and query_time>=:p2 and query_time<=:p3 and country=:p4 and catalog not like '%on_website'  ";
		List<Object[]> rankList=amazonCatalogRankDao.findBySql(sql,new Parameter(productName,start,end,cty));
		for (Object[] obj : rankList) {
			AmazonCatalogRank rank=new AmazonCatalogRank();
			rank.setAsin(obj[0].toString());
			rank.setCountry(obj[1].toString());
			rank.setCatalog(obj[3].toString());
			rank.setRank(Integer.parseInt(obj[4].toString()));
			String key = rank.getAsin()+"_"+rank.getCountry()+"_"+obj[2].toString();
			Map<String,AmazonCatalogRank> data = rs.get(key);
			if(data==null){
				data = Maps.newHashMap();
				rs.put(key, data);
			}
			data.put(rank.getCatalog(), rank);
			
		}
		
		/*List<AmazonCatalogRank> list = amazonCatalogRankDao.find(dc);
		for (AmazonCatalogRank rank : list) {
			String catalog=rank.getCatalog();
			String key = rank.getAsin()+"_"+rank.getCountry()+"_"+dateFormat.format(rank.getQueryTime());
			Map<String,AmazonCatalogRank> data = rs.get(key);
			if(data==null){
					data = Maps.newHashMap();
					rs.put(key, data);
			}
			data.put(catalog, rank);
		}*/
		return rs;
	}
	
	public Map<String,Integer> getProductRank(String productName,Date start,Date end,String country) {
		Map<String,Integer> rs = Maps.newHashMap();
		String sql="SELECT DATE_FORMAT(query_time,'%Y-%m-%d'),rank FROM amazoninfo_catalog_rank WHERE  product_name=:p1  and query_time>=:p2 and query_time<=:p3 and country=:p4 and catalog not like '%on_website' ";
		List<Object[]> rankList=amazonCatalogRankDao.findBySql(sql,new Parameter(productName,start,end,country));
		for (Object[] obj : rankList) {
			 rs.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
		}
		return rs;
	}
	
	public Map<String,List<AmazonCatalogRank>> getCatalogName2(String productName,Date start,Date end,String cty){
		DateFormat formatDay = new SimpleDateFormat("yyyyMMdd");
		String sql="SELECT distinct country,catalog_name,catalog,MIN(query_time),MAX(query_time),asin FROM amazoninfo_catalog_rank WHERE product_name=:p1 and query_time>=:p2 and query_time<=:p3 and country=:p4 and catalog not like '%on_website'  "+
	    " GROUP BY country,catalog_name,catalog,asin HAVING MIN(rank)<=100 ";
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql,new Parameter(productName,start,end,cty));
		Map<String,List<AmazonCatalogRank>> rs=Maps.newHashMap();
		for (Object[] obj : list) {
			String country=obj[0].toString();
			if(obj[1]!=null){
				String catalogName=(obj[1]==null?"":obj[1].toString());
				List<AmazonCatalogRank> nameList=rs.get(country);
				if(nameList==null){
					nameList=Lists.newArrayList();
					rs.put(country, nameList);
				}
				AmazonCatalogRank rank=new AmazonCatalogRank();
				rank.setCatalogName(catalogName);
				rank.setCatalog(obj[2].toString());
				rank.setCountry(country);
				Date startDate=(Timestamp)obj[3];
				Date endDate=(Timestamp)obj[4];
				rank.setAsin(obj[5].toString());
				rank.setStartDate(formatDay.format((Timestamp)obj[3]));
				rank.setEndDate(formatDay.format((Timestamp)obj[4]));
				List<String> rankXAxis  = Lists.newArrayList();
				
				while(endDate.after(startDate)||endDate.equals(startDate)){
					String key = formatDay.format(DateUtils.addDays(startDate,-1));
					if(Integer.parseInt(key) >= 20150902){
						rankXAxis.add(key);
					}
					startDate = DateUtils.addDays(startDate, 1);
				}
				String lastDay=formatDay.format(DateUtils.addDays(endDate,-1));
				if(!rankXAxis.contains(lastDay)){
					rankXAxis.add(lastDay);
				}
				rank.setRankXAxis(rankXAxis);
				nameList.add(rank);
			}
		}
		return rs;
	}
	
	/*public Map<String,List<String>> getCatalogName(String productName,Date start,Date end){
		String sql="SELECT distinct country,catalog_name FROM amazoninfo_catalog_rank WHERE catalog!='ce_display_on_website' and  catalog_name IS NOT null and product_name='"+productName+"' and DATE_FORMAT(query_time,'%Y-%m-%d')>='"+new SimpleDateFormat("yyyy-MM-dd").format(start)+"' and DATE_FORMAT(query_time,'%Y-%m-%d')<='"+new SimpleDateFormat("yyyy-MM-dd").format(end)+"' "+
	    " GROUP BY country,catalog_name HAVING MIN(rank)<=100 ";
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql);
		Map<String,List<String>> rs=Maps.newHashMap();
		for (Object[] obj : list) {
			String country=obj[0].toString();
			if(obj[1]!=null){
				String catalogName=obj[1].toString();
				List<String> nameList=rs.get(country);
				if(nameList==null){
					nameList=Lists.newArrayList();
					rs.put(country, nameList);
				}
				nameList.add(catalogName);
			}
		}
		return rs;
	}*/
	
	public Map<String,Map<String,AmazonCatalogRank>> find(Date start,Date end) {
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		dc.add(Restrictions.ge("queryTime",start));
		dc.add(Restrictions.le("queryTime",end));
		List<AmazonPostsDetail> list = amazonPostsDetailDao.find(dc);
		Map<String,Map<String,AmazonCatalogRank>> rs = Maps.newHashMap();
		for (AmazonPostsDetail postDetail : list) {
			List<AmazonCatalogRank> rankList=postDetail.getRankItems();
			for (AmazonCatalogRank rank : rankList) {
				String catalogName = rank.getCatalogName();
				if(StringUtils.isNotBlank(catalogName)){
					String key = catalogName+"_"+rank.getCountry();
					String date = dateFormat.format(rank.getQueryTime());
					Map<String,AmazonCatalogRank> data = rs.get(key);
					if(data==null){
						data = Maps.newHashMap();
						rs.put(key, data);
					}
					data.put(date, rank);
				}
			}
		}
		return rs;
	}

	
	public Map<String,Integer> getRank(Set<String> catalog,Date date,String productName,String country){
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		Map<String,Integer> rs=Maps.newHashMap();
		if(date==null){
			date = new Date();
		}
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.eq("productName",productName));
		dc.add(Restrictions.ge("queryTime",DateUtils.addDays(date,-1)));
		dc.add(Restrictions.lt("queryTime",date));
		dc.createAlias("this.rankItems", "rankItems");
		dc.add(Restrictions.in("rankItems.catalog", catalog));
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		for (AmazonPostsDetail amazonPostsDetail : list) {
			for (AmazonCatalogRank rank : amazonPostsDetail.getRankItems()) {
	             rs.put(rank.getCatalog(), rank.getRank());
			}	
		}
		return rs;
     }
	
	public List<String> getParentSku(String accountName){
		String sql="SELECT DISTINCT ASIN FROM amazoninfo_product2 WHERE account_name=:p1 and asin is not null";
		List<Object> asinList=amazonPostsDetailDao.findBySql(sql,new Parameter(accountName));
		String sql1="SELECT sku FROM amazoninfo_posts_detail d WHERE (d.`query_time`>=DATE_ADD(CURRENT_DATE(),INTERVAL -5 DAY) and account_name=:p1 and d.`parent_id` IS NULL  "+
		    " and d.asin not in :p2 and sku is not null) or (d.query_time='2015-01-01' and d.account_name=:p1) order by d.id desc ";
		List<String> list=amazonPostsDetailDao.findBySql(sql1,new Parameter(accountName,asinList));
		Set<String> rs=Sets.newHashSet();
		for (String sku: list) {
			rs.add(sku);
		}
		return Lists.newArrayList(rs);
	}
	
	public String getSizeOrColor(String accountName,String sku){
		String sql1="select id from amazoninfo_posts_detail where account_name=:p1 and query_time=:p2 and sku=:p3 ";
		Date date=new Date();
		List<Integer> list1=amazonPostsDetailDao.findBySql(sql1,new Parameter(accountName,new SimpleDateFormat("yyyy-MM-dd").format(date),sku));
		
		String sql="select by_size,by_color from amazoninfo_posts_detail where parent_id =:p1 ";
	
		if(list1.size()>0){
			Integer id=list1.get(0);
			List<Object[]> list=amazonPostsDetailDao.findBySql(sql,new Parameter(id));
			int len=list.size();
			int sizeLen=0;
			int colorLen=0;
			for (Object[] obj : list) {
				if("1".equals(obj[0].toString())){
					++sizeLen;
				}
				if("1".equals(obj[1].toString())){
					++colorLen;
				}
			}
			if(sizeLen==len&&colorLen==len){
				return "1";
			}else if(sizeLen==len&&colorLen<len){
				return "2";
			}else if(sizeLen<len&&colorLen==len){
				return "3";
			}
		}else{
			int i=1;
			while(list1.size()==0&&i<10){
				list1=amazonPostsDetailDao.findBySql(sql1, new Parameter(accountName,new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(date, 0-i)),sku));
				if(list1.size()>0){
					Integer id=list1.get(0);
					List<Object[]> list=amazonPostsDetailDao.findBySql(sql,new Parameter(id));
					int len=list.size();
					int sizeLen=0;
					int colorLen=0;
					for (Object[] obj : list) {
						if("1".equals(obj[0].toString())){
							++sizeLen;
						}
						if("1".equals(obj[1].toString())){
							++colorLen;
						}
					}
					if(sizeLen==len&&colorLen==len){
						return "1";
					}else if(sizeLen==len&&colorLen<len){
						return "2";
					}else if(sizeLen<len&&colorLen==len){
						return "3";
					}
				}else{
					i++;
				}
			}		   
		}
		return "0";
	}
	
	public List<AmazonPostsDetail> getChildSku(String accountName){
		String sql="SELECT DISTINCT p.sku,CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN CONCAT ('_',b.`color`) ELSE '' END) NAME,p.asin FROM amazoninfo_product2 p "+
                 " JOIN psi_sku b ON b.`sku`=p.`sku` AND p.`account_name` = b.`account_name` AND  b.`del_flag` = '0' "+
				 " WHERE p.account_name=:p1 AND p.active='1' and p.ASIN is not null "+
				 " and b.`product_name`!='Inateck other' AND b.`product_name`!='Inateck Old' ";
		
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql,new Parameter(accountName));
		List<AmazonPostsDetail> posts=Lists.newArrayList();
		if(list!=null&&list.size()>0){
			for (Object[] obj : list) {
					AmazonPostsDetail detail=new AmazonPostsDetail();
					detail.setSku(obj[0].toString());
					detail.setProductName(obj[1]==null?"":obj[1].toString());
					detail.setAsin(obj[2].toString());
					posts.add(detail);
			}
		}
		return posts;
	}
	
	/*public List<AmazonPostsDetail> getChildSku(String country){
		String sql="SELECT DISTINCT sku,product_name,size,color,asin FROM amazoninfo_posts_detail d where sku is not null and country=:p1 and DATE_FORMAT(query_time,'%Y-%m-%d')=:p2  ";
		Date today = DateUtils.getDateStart(new Date());
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql,new Parameter(country,new SimpleDateFormat("yyyy-MM-dd").format(today)));
		List<AmazonPostsDetail> posts=Lists.newArrayList();
		if(list!=null&&list.size()>0){
			for (Object[] obj : list) {
				String[] arr=obj[0].toString().split(",");
				for (String skuStr : arr) {
					AmazonPostsDetail detail=new AmazonPostsDetail();
					detail.setSku(skuStr);
					detail.setProductName(obj[1]==null?"":obj[1].toString());
					detail.setSize(obj[2]==null?"":obj[2].toString());
					detail.setColor(obj[3]==null?"":obj[3].toString());
					detail.setAsin(obj[4]==null?"":obj[4].toString());
					posts.add(detail);
				}
			}
			return posts;
		}else{
			int i = 1 ;
			while(list.size()==0&&i<10){
			   list=amazonPostsDetailDao.findBySql(sql, new Parameter(country,new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(today, 0-i))));
			   if(list!=null&&list.size()>0){
				   for (Object[] obj : list) {
					    String[] arr=obj[0].toString().split(",");
						for (String skuStr : arr) {
							AmazonPostsDetail detail=new AmazonPostsDetail();
							detail.setSku(skuStr);
							detail.setProductName(obj[1]==null?"":obj[1].toString());
							detail.setSize(obj[2]==null?"":obj[2].toString());
							detail.setColor(obj[3]==null?"":obj[3].toString());
							detail.setAsin(obj[4]==null?"":obj[4].toString());
							posts.add(detail);
						}
					}
					return posts;
			   }else{
				   i++;	 
			   }
			}
		}
		return null;
	}*/
	
	
	
	public List<AmazonPostsDetail> getChildSku(String accountName,String asin){
		String sql="SELECT DISTINCT sku,product_name,size,color,asin,ean FROM amazoninfo_posts_detail d where sku is not null and account_name=:p1 and DATE_FORMAT(query_time,'%Y-%m-%d')=:p2 and (asin=:p3 or ean=:p3) ";
		Date today = DateUtils.getDateStart(new Date());
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql,new Parameter(accountName,new SimpleDateFormat("yyyy-MM-dd").format(today),asin));
		List<AmazonPostsDetail> posts=Lists.newArrayList();
		if(list.size()>0){
			for (Object[] obj : list) {
				
					AmazonPostsDetail detail=new AmazonPostsDetail();
					detail.setSku(obj[0].toString());
					detail.setProductName(obj[1]==null?"":obj[1].toString());
					detail.setSize(obj[2]==null?"":obj[2].toString());
					detail.setColor(obj[3]==null?"":obj[3].toString());
					detail.setAsin(obj[4]==null?"":obj[4].toString());
					detail.setEan(obj[5]==null?"":obj[5].toString());
					posts.add(detail);
				
			}
			return posts;
		}else{
			int i = 1 ;
			while(list.size()==0&&i<10){
			   list=amazonPostsDetailDao.findBySql(sql, new Parameter(accountName,new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(today, 0-i)),asin));
			   if(list.size()>0){
				   for (Object[] obj : list) {
					   
							AmazonPostsDetail detail=new AmazonPostsDetail();
							detail.setSku(obj[0].toString());
							detail.setProductName(obj[1]==null?"":obj[1].toString());
							detail.setSize(obj[2]==null?"":obj[2].toString());
							detail.setColor(obj[3]==null?"":obj[3].toString());
							detail.setAsin(obj[4]==null?"":obj[4].toString());
							detail.setEan(obj[5]==null?"":obj[5].toString());
							posts.add(detail);
						
					}
					return posts;
			   }else{
				   i++;	 
			   }
			}
		}
		return null;
	}
	
	public Map<String,String> getAllSku(String accountName,Set<String> skuSet){
		Map<String,String> rs = Maps.newHashMap();
		for (String sku : skuSet) {
			rs.put(sku, sku);
			String sql="SELECT DISTINCT sku FROM amazoninfo_posts_detail d where  account_name=:p1 and DATE_FORMAT(query_time,'%Y-%m-%d')=:p2 and sku like :p3 ";
			Date today = DateUtils.getDateStart(new Date());
			List<Object> list=amazonPostsDetailDao.findBySql(sql,new Parameter(accountName,new SimpleDateFormat("yyyy-MM-dd").format(today),"%"+sku+"%"));
			if(list.size()>0){
				rs.put(sku,list.get(0).toString());
			}else{
				int i = 1 ;
				while(list.size()==0&&i<10){
				   list=amazonPostsDetailDao.findBySql(sql, new Parameter(accountName,new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(today, 0-i)),"%"+sku+"%"));
				   if(list.size()>0){
						rs.put(sku,list.get(0).toString());
						break;
				   }else{
					   i++;	 
				   }
				}
			}
		}
		return rs;
	}
	
	public List<AmazonPostsDetail> getChildSku2(String accountName){
		String sql="SELECT DISTINCT GROUP_CONCAT(p.sku),CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN CONCAT ('_',b.`color`) ELSE '' END) NAME FROM amazoninfo_product2 p "+
                 " JOIN psi_sku b ON b.`sku`=p.`sku` AND p.`account_name` = b.`account_name` AND  b.`del_flag` = '0' "+
				 " WHERE p.account_name=:p1 AND p.active='1'  "+
				 " GROUP BY NAME HAVING (NAME!='Inateck other' AND NAME!='Inateck Old')";
		
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql,new Parameter(accountName));
		List<AmazonPostsDetail> posts=Lists.newArrayList();
		if(list!=null&&list.size()>0){
			for (Object[] obj : list) {
					AmazonPostsDetail detail=new AmazonPostsDetail();
					detail.setSku(obj[0].toString());
					detail.setProductName(obj[1]==null?"":obj[1].toString());
					posts.add(detail);
			}
		}
		return posts;
	}
	
	/*public List<AmazonPostsDetail> getChildSku2(String country){
		String sql="SELECT DISTINCT sku,product_name,size,color FROM amazoninfo_posts_detail d where sku is not null and country=:p1 and DATE_FORMAT(query_time,'%Y-%m-%d')=:p2  ";
		Date today = DateUtils.getDateStart(new Date());
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql,new Parameter(country,new SimpleDateFormat("yyyy-MM-dd").format(today)));
		List<AmazonPostsDetail> posts=Lists.newArrayList();
		if(list!=null&&list.size()>0){
			for (Object[] obj : list) {
					AmazonPostsDetail detail=new AmazonPostsDetail();
					detail.setSku(obj[0].toString());
					detail.setProductName(obj[1]==null?"":obj[1].toString());
					detail.setSize(obj[2]==null?"":obj[2].toString());
					detail.setColor(obj[3]==null?"":obj[3].toString());
					posts.add(detail);
			}
			return posts;
		}else{
			int i = 1 ;
			while(list.size()==0&&i<10){
			   list=amazonPostsDetailDao.findBySql(sql, new Parameter(country,new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(today, 0-i))));
			   if(list!=null&&list.size()>0){
				   for (Object[] obj : list) {
							AmazonPostsDetail detail=new AmazonPostsDetail();
							detail.setSku(obj[0].toString());
							detail.setProductName(obj[1]==null?"":obj[1].toString());
							detail.setSize(obj[2]==null?"":obj[2].toString());
							detail.setColor(obj[3]==null?"":obj[3].toString());
							posts.add(detail);
					}
					return posts;
			   }else{
				   i++;	 
			   }
			}
		}
		return null;
	}*/
	
	
	public Set<String> findDescEmpty(String country){
		String sql = "SELECT a.`sku` FROM amazoninfo_posts_detail a WHERE  a.`query_time` = CURDATE() AND a.`description` = '' AND a.`product_name` IS NOT NULL AND a.`country`=:p1";
		List<Object> list = amazonPostsDetailDao.findBySql(sql, new Parameter(country));
		Set<String> rs = Sets.newHashSet();
		String suf = "-"+country;
		if("com".equals(country)){
			suf = "-us";
		}
		for (Object objs : list) {
			String skus = objs.toString();
			String[]temp = skus.split(",");
			String sku = "";
			for (String str : temp) {
				if(str.toLowerCase().contains(suf) && !str.toLowerCase().contains("local")&& !str.toLowerCase().contains("old") ){
					sku = str;
					break;
				}
			}
			rs.add(sku);
		}
		rs.remove("");
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void updateDesc(String country ,String sku,String desc) {
		String p3 = sku+",%";
		String p4 = ","+sku+"%";
		String sql = "UPDATE  amazoninfo_posts_detail a SET a.`description`=:p1 WHERE  a.`query_time` = CURDATE() AND a.`country`=:p2 AND (a.`sku` LIKE :p3 or a.`sku` LIKE :p4 or  a.`sku`=:p5 )  ";
		amazonPostsDetailDao.updateBySql(sql, new Parameter(desc,country,p3,p4,sku));
	}
	
	@Transactional(readOnly = false)
	public void updateAmazonProduct2Sku(String accountName,String sku){
		String sql="update amazoninfo_product2 set active='0' where account_name=:p1 and sku=:p2 ";
		amazonPostsDetailDao.updateBySql(sql, new Parameter(accountName,sku));
	}
	
	@Transactional(readOnly = false)
	public void updateSku(String accountName,String sku){
		String sqlString="select max(query_time) from amazoninfo_posts_detail where  account_name=:p1 and sku like :p2 ";
		List<Object> rs=amazonPostsDetailDao.findBySql(sqlString,new Parameter(accountName,"%"+sku+"%"));
		if(rs!=null&&rs.size()>0){
			Date date=(Timestamp)rs.get(0);
			if(date!=null){
				String sql="SELECT id,sku FROM amazoninfo_posts_detail where account_name=:p1 and sku like :p2 and query_time=:p3";
				List<Object[]> list=amazonPostsDetailDao.findBySql(sql,new Parameter(accountName,"%"+sku+"%",date));
				if(list!=null&&list.size()>0){
					for (Object[] obj : list) {
						String updateSku=obj[1].toString();
						String id=obj[0].toString();
						if(updateSku.contains(",")){
							String[] skuArr=updateSku.split(",");
							String temp="";
							StringBuffer buf= new StringBuffer();
							for (String arr : skuArr) {
								if(!arr.equals(sku)){
									buf.append(arr+",");
								}
							}
							temp=buf.toString();
							String updateSql="update amazoninfo_posts_detail set sku=:p1 where id=:p2";
							amazonPostsDetailDao.updateBySql(updateSql, new Parameter(temp.substring(0, temp.length()-1),id));
						}else{
							String delSql="delete from amazoninfo_posts_detail where id=:p1";
							amazonPostsDetailDao.updateBySql(delSql, new Parameter(id));
						}
					}
				}
			}
		}
	}
	


	public Map<String,List<AmazonPostsDetail>> getExceptionSize(){
		 Map<String,List<AmazonPostsDetail>> map=Maps.newHashMap();
		String sql=" SELECT d.country,d.`product_name`,ROUND(d.`package_length`*2.54,2),ROUND(d.`package_width`*2.54,2),ROUND(d.`package_height`*2.54,2),ROUND(d.`package_weight`*0.4535924,2)  FROM amazoninfo_posts_detail d "+
				" WHERE  d.`query_time`>DATE_SUB(CURDATE(),INTERVAL 1 DAY) AND d.`product_name` IS NOT NULL "+
				" AND ( (d.`country` IN ('de','fr','it','es','uk') AND (d.`package_weight`*0.4535924>12.5 OR d.`package_length`*2.54>45.5 OR d.`package_width`*2.54>34.5 OR d.`package_height`*2.54>26.5)) "+
				"  OR (d.`country` IN ('ca','jp') AND (d.`package_weight`*0.4535924>9.5 OR d.`package_length`*2.54>45.5 OR d.`package_width`*2.54>35.5 OR d.`package_height`*2.54>20.5)) "+
				"  OR (d.`country`='com' AND (d.`package_weight`*0.4535924>9.5 OR d.`package_length`*2.54>45.5 OR d.`package_width`*2.54>35.5 OR d.`package_height`*2.54>20.5))) ";
        List<Object[]> objList=amazonPostsDetailDao.findBySql(sql);
        for (Object[] obj: objList) {
			String country=obj[0].toString();
			String name=obj[1].toString();
			Float length = ((BigDecimal)(obj[2]==null?new BigDecimal(0):obj[2])).floatValue();
			Float width = ((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue();
			Float height = ((BigDecimal)(obj[4]==null?new BigDecimal(0):obj[4])).floatValue();
			Float weight = ((BigDecimal)(obj[5]==null?new BigDecimal(0):obj[5])).floatValue();
			AmazonPostsDetail detail=new AmazonPostsDetail();
			detail.setCountry(country);
			detail.setProductName(name);
			detail.setPackageLength(length);
			detail.setPackageWidth(width);
			detail.setPackageHeight(height);
			detail.setPackageWeight(weight);
			List<AmazonPostsDetail> list=map.get(country);
			if(list==null){
				list=Lists.newArrayList();
				map.put(country, list);
			}
			list.add(detail);
		}
		return map;
	}
	
	/**
	 * 查询最近的组合贴信息
	 */
	public Map<String, Set<String>> findCombination(){
		Map<String, Set<String>> map = Maps.newHashMap();
		String sqlString="select max(query_time) from amazoninfo_posts_detail";
		List<Object> rs = amazonPostsDetailDao.findBySql(sqlString);
		if (rs != null && rs.size() > 0) {
			Date date = (Timestamp) rs.get(0);
			if (date != null) {
				String sql="SELECT DISTINCT d.`parent_id` FROM amazoninfo_posts_detail d WHERE d.`parent_id` IS NOT NULL AND d.`query_time`=:p1 AND (d.`product_name` LIKE 'Inateck%' OR d.`product_name` LIKE 'Tomons%')";
				List<Object[]> list = amazonPostsDetailDao.findBySql(sql, new Parameter(date));
				if (list != null && list.size() > 0) {
					sql="SELECT t.`country`,t.`ASIN` FROM amazoninfo_posts_detail t WHERE t.`id` IN(:p1)";
					List<Object[]> rsList = amazonPostsDetailDao.findBySql(sql, new Parameter(list));
					for (Object[] obj : rsList) {
						String country = obj[0].toString();
						String asin = obj[1].toString();
						Set<String> asinSet = map.get(country);
						if (asinSet == null) {
							asinSet = Sets.newHashSet();
							map.put(country, asinSet);
						}
						asinSet.add(asin);
					}
				}
			}
		}
		return map;
	}
	
	public Map<String,List<AmazonPostsDetail>> findExceptionData(){
		Map<String,List<AmazonPostsDetail>> map=Maps.newHashMap();
		Map<String,PsiInventoryFba> amazonStock=psiInventoryService.getAllProductFbaInfo();//productName+"_"+country
		String sql="SELECT d.`country`,d.`product_name`,d.`sku`,(CASE WHEN d.`keyword1`='' THEN '1' WHEN d.`description`='' THEN '2' ELSE '3' END) TYPE,d.asin FROM amazoninfo_posts_detail d "+
			" JOIN psi_product_eliminate a ON d.`country`=a.`country` AND d.`product_name`=(CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.color) END) "+
			" WHERE d.`query_time`=CURRENT_DATE() AND (d.`keyword1`='' OR (d.country!='jp' and d.`description`='') OR d.`feature1`='') AND d.`product_name` IS NOT NULL "+
			" AND a.`is_sale`!='4' ";
		List<Object[]> rs = amazonPostsDetailDao.findBySql(sql);	
	    if(rs!=null&&rs.size()>0){
	    	for (Object[] obj: rs) {
	    		String country=obj[0].toString();
	    		String name=obj[1].toString();
	    		String sku=obj[2].toString();
	    		String type=obj[3].toString();
	    		String asin=obj[4].toString();
	    		AmazonPostsDetail detail=new AmazonPostsDetail();
	    		detail.setCountry(country);
	    		detail.setProductName(name);
	    		detail.setSku(sku);
	    		detail.setTitle(type);
	    		detail.setAsin(asin);
	    		if(amazonProduct2Service.isShowPosts(country,asin)){
	    			if("de,fr,it,es,uk".contains(country)){
	 	    		   Map<String, String> powerMap=psiProductService.getHasPowerByName();
	 	    		   if("0".equals(powerMap.get(name))&&"de".equals(country)){//0:不带电
	 	    			   if(amazonStock.get(name+"_eu")!=null&&amazonStock.get(name+"_eu").getFulfillableQuantity()>10){
	 	    					List<AmazonPostsDetail> list=map.get(country);
	 	    					if(list==null){
	 	    						list=Lists.newArrayList();
	 	    						map.put(country, list);
	 	    					}
	 	    					list.add(detail);
	 	    				}
	 	    		   }else if("1".equals(powerMap.get(name))&&"de,uk".contains(country)){
	 	    			   if(amazonStock.get(name+"_"+country)!=null&&amazonStock.get(name+"_"+country).getFulfillableQuantity()>10){
	 	    					List<AmazonPostsDetail> list=map.get(country);
	 	    					if(list==null){
	 	    						list=Lists.newArrayList();
	 	    						map.put(country, list);
	 	    					}
	 	    					list.add(detail);
	 	    				}
	 	    		   }
	 	    		}else{
	 	    			if(amazonStock!=null&&amazonStock.size()>0){
	 	    				if(amazonStock.get(name+"_"+country)!=null&&amazonStock.get(name+"_"+country).getFulfillableQuantity()>10){
	 	    					List<AmazonPostsDetail> list=map.get(country);
	 	    					if(list==null){
	 	    						list=Lists.newArrayList();
	 	    						map.put(country, list);
	 	    					}
	 	    					list.add(detail);
	 	    				}
	 	    			}
	 	    		}
	    		}
			}
	    }
	    return map;
	}
	
	public Map<String,List<Object[]>> findRankCatalog(){
		Map<String,List<Object[]>> map=Maps.newHashMap();
		String sql="SELECT  aa.country aacountry,aa.product_name aaproductname,aa.catalogs aacatalogs,bb.country bbcountry,bb.product_name bbproductname,bb.catalogs bbcatalogs,bb.path_name FROM (SELECT a.`country`,a.`product_name`,GROUP_CONCAT(a.`catalog_name` ORDER BY a.`catalog_name`) AS catalogs FROM amazoninfo_catalog_rank a WHERE a.`catalog_name` IS NOT NULL AND a.`query_time` = CURDATE() GROUP BY a.`country`,a.`product_name`) aa "+
		        "   ,(SELECT a.`country`,a.`product_name`,GROUP_CONCAT(a.`catalog_name` ORDER BY a.`catalog_name`) AS catalogs,GROUP_CONCAT(a.`path_name` ORDER BY a.`catalog_name`) path_name  FROM amazoninfo_catalog_rank a WHERE a.`catalog_name` IS NOT NULL AND a.`query_time` = DATE_ADD(CURDATE(),INTERVAL -1 DAY) GROUP BY a.`country`,a.`product_name`)bb "+
		        "   WHERE aa.country = bb.country AND aa.product_name = bb.product_name AND aa.catalogs != bb.catalogs ";
		List<Object[]> rs = amazonPostsDetailDao.findBySql(sql);	
	    if(rs!=null&&rs.size()>0){
	    	for (Object[] obj:rs) {
	    		List<Object[]> temp=map.get(obj[0].toString());
	    		if(temp==null){
	    			temp=Lists.newArrayList();
	    			map.put(obj[0].toString(), temp);
	    		}
	    		temp.add(obj);
	    	}
	    }	
		return map;
	}
	
	public Map<String,List<Object[]>> findAsinChange(){
		Map<String,List<Object[]>> map=Maps.newHashMap();
		String sql="SELECT aa.country,aa.product_name,bb.asins basin,aa.asins aasin FROM  "+
				" (SELECT a.`product_name`,a.`country`,GROUP_CONCAT(CONCAT('<a href=\"https://www.amazon.',(CASE WHEN a.`country`='jp' || a.`country`='uk' THEN CONCAT('co',a.`country`) ELSE a.`country` END),'/dp/',a.`ASIN`,'\" target=\"_blank\">',a.`ASIN`,'</a>') ORDER BY a.`ASIN`) AS asins FROM `amazoninfo_posts_detail` a WHERE a.`query_time` = CURDATE() AND a.`ASIN` IS NOT NULL AND a.`product_name` IS NOT NULL GROUP BY a.`product_name`,a.`country`) aa , "+
				" (SELECT a.`product_name`,a.`country`,GROUP_CONCAT(CONCAT('<a href=\"https://www.amazon.',(CASE WHEN a.`country`='jp' || a.`country`='uk' THEN CONCAT('co',a.`country`) ELSE a.`country` END),'/dp/',a.`ASIN`,'\" target=\"_blank\">',a.`ASIN`,'</a>') ORDER BY a.`ASIN`)  AS asins FROM `amazoninfo_posts_detail` a WHERE a.`query_time` = DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND a.`ASIN` IS NOT NULL AND a.`product_name` IS NOT NULL GROUP BY a.`product_name`,a.`country`) bb "+ 
				" WHERE aa.product_name = bb.product_name AND aa.country = bb.country AND aa.asins != bb.asins ";
		List<Object[]> rs = amazonPostsDetailDao.findBySql(sql);	
	    if(rs!=null&&rs.size()>0){
	    	for (Object[] obj:rs) {
	    		List<Object[]> temp=map.get(obj[0].toString());
	    		if(temp==null){
	    			temp=Lists.newArrayList();
	    			map.put(obj[0].toString(), temp);
	    		}
	    		temp.add(obj);
	    	}
	    }	
		return map;
	} 
	
	
	public Map<String,List<Object[]>> findPriceChange(){
		Map<String,List<Object[]>> map=Maps.newLinkedHashMap();
		String sql=" SELECT aa.country,aa.product_name,aa.sku,ROUND((aa.amz_price-bb.amz_price)/bb.amz_price,2)*100 AS pcent,ROUND((aa.cost-bb.cost)/bb.cost,2)*100,ROUND((aa.fba-bb.fba)/bb.fba ,2)*100,ROUND((aa.commission_pcent-bb.commission_pcent)/bb.commission_pcent,2)*100 "+
				"  FROM (SELECT * FROM amazoninfo_product_price a WHERE a.`date` = DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND a.type='0') aa "+
				" ,(SELECT * FROM amazoninfo_product_price a WHERE a.`date` = DATE_ADD(CURDATE(),INTERVAL -2 DAY)) bb "+
				" WHERE aa.country = bb.country  AND  aa.sku = bb.sku  AND ROUND((ABS(aa.amz_price - bb.amz_price)),2)/bb.amz_price >=0.05 ORDER BY FIELD(aa.country,'com','de','fr','uk','jp','it','es','ca'),pcent DESC";
		List<Object[]> rs = amazonPostsDetailDao.findBySql(sql);	
	    if(rs!=null&&rs.size()>0){
	    	for (Object[] obj:rs) {
	    		List<Object[]> temp=map.get(obj[0].toString());
	    		if(temp==null){
	    			temp=Lists.newArrayList();
	    			map.put(obj[0].toString(), temp);
	    		}
	    		temp.add(obj);
	    	}
	    }	
		return map;
   }
	
	public Map<String,String> findNoCatalog(){
		String countrySql="SELECT r.`country`,COUNT(*) FROM amazoninfo_catalog_rank r WHERE r.`query_time`=CURDATE() GROUP BY r.`country`";
		List<Object[]> list = amazonPostsDetailDao.findBySql(countrySql);
		
		Map<String,String>  map=Maps.newHashMap();
		if(list!=null&&list.size()>0){
			Map<String,Integer> countryMap=Maps.newHashMap();
			for (Object[] obj: list) {
				countryMap.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
			}
			String sql=" SELECT GROUP_CONCAT(distinct CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN '_' ELSE '' END,s.`color`)),s.country FROM amazoninfo_product2 p "+
					" JOIN psi_sku s ON p.`country`=s.`country` AND p.`sku`=s.`sku` AND s.`del_flag`='0' "+
					" join psi_product_eliminate e on s.country=e.country and e.product_name=s.`product_name` and e.color=s.`color` and e.del_flag='0' and added_month is not null "+
					" LEFT JOIN amazoninfo_catalog_rank r ON r.`ASIN`=p.`asin` AND p.`country`=r.`country` AND r.`query_time`=CURDATE() "+
					" WHERE p.`active`='1'  and s.`product_name`!='Inateck other'  AND r.`country` IS NULL GROUP BY s.country ";
				List<Object[]> rs = amazonPostsDetailDao.findBySql(sql);	
				if(rs!=null&&rs.size()>0){
				   for (Object[] obj:rs) {
					   if(countryMap!=null&&countryMap.get(obj[1].toString())!=null&&countryMap.get(obj[1].toString())>0){
						   map.put(obj[1].toString(),obj[0].toString());
					   }
				   }
				}    	
		}
		
		return map;
	}
	
	public List<Object[]> findErrorPosts(){
		String sql="SELECT ddd.pname,COUNT(1) AS num ,GROUP_CONCAT(DISTINCT ddd.asin) AS asins FROM ( "+
		" SELECT  DISTINCT CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN '_' ELSE '' END,s.`color`) AS pname,s.`asin`,CASE WHEN s.`country`='uk' THEN s.`country` ELSE 'de' END FROM psi_sku s ,amazoninfo_product2 p, "+
		" (SELECT CONCAT(a.`brand`,' ',a.model) AS pname FROM psi_product a WHERE a.`del_flag` = '0'  AND (CASE WHEN  a.type='Keyboard'  THEN '1' ELSE a.`has_power` END ) = '1' AND a.`platform` LIKE '%uk%') d "+
		" WHERE s.`product_name` = d.pname AND s.sku = p.sku AND s.country = p.country  AND p.active = '1' AND s.`country` IN ('uk','de','fr','es','it') AND s.`del_flag` = '0' AND s.`asin` IS NOT NULL ) ddd  "+
		" GROUP BY ddd.pname HAVING  num>=2  AND  NOT(LOCATE(',',asins)) ";
		return amazonPostsDetailDao.findBySql(sql);
	}
	
	
	public Map<String,List<AmazonPostsDetail>> findWarnPartNumber(){
		 Map<String,List<AmazonPostsDetail>> map=Maps.newHashMap();
		 String sql="SELECT a.`country`,a.`product_name`,a.`sku`,a.`part_number`,b.`is_sale` FROM amazoninfo_posts_detail a,psi_product_eliminate b WHERE CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END, b.`color`) = a.`product_name` AND a.`country` = b.`country` AND  a.`query_time` = CURDATE() AND NOT(LOCATE(a.`part_number`,a.`product_name`)) AND a.`part_number` IS NOT NULL AND a.`product_name` IS NOT NULL and a.`sku` is not null ORDER BY FIELD(a.`country`,'com','de','uk','fr','jp','it','es','ca'), b.`is_sale` DESC";
		 List<Object[]> rs = amazonPostsDetailDao.findBySql(sql);
		 if(rs!=null&&rs.size()>0){
		    for (Object[] obj:rs) {
		    	String country=obj[0].toString();
		    	String name=obj[1].toString();
		    	String sku=obj[2].toString();
		    	String partNumber=obj[3].toString();
		    	String isSale=obj[4].toString();
		    	String suffix=("com".equals(country)?"us":country).toUpperCase();
		    	
		    	AmazonPostsDetail detail=new AmazonPostsDetail();
		    	detail.setCountry(country);
		    	detail.setProductName(name);
		    	detail.setSku(sku);
		    	detail.setPartNumber(partNumber);
		    	detail.setTitle(isSale);
		    	if(partNumber.toLowerCase().contains("new")){
		    		List<AmazonPostsDetail> temp=map.get(country);
			    	if(temp==null){
			    		temp=Lists.newArrayList();
			    		map.put(country, temp);
			    	}
		    		temp.add(detail);
					continue;
		    	}
		    	String nameNoColor = "";
		    	if(name.indexOf("_")>0){
					nameNoColor = name.substring(0,name.indexOf("_")).replace("Inateck", "").replace("Tomons", "").replace("UNITEK", "").trim();
				}else{
					nameNoColor = name.replace("Inateck", "").replace("Tomons", "").replace("UNITEK", "").trim();
				}
		    	
		    	if(partNumber.equals(nameNoColor)||partNumber.equals(nameNoColor+suffix)||partNumber.equals(nameNoColor+"-"+suffix)||partNumber.equals(nameNoColor+"_"+suffix)){
					continue;
				}
		    	if("ca".equals(country)&&(partNumber.equals(nameNoColor+"US")||partNumber.equals(nameNoColor+"-US")||partNumber.equals(nameNoColor+"_US"))){
		    		continue;
		    	}
		    	String[] skuArr=sku.split(",");
		    	boolean flag=true;
		    	for (String arr : skuArr) {
		    		if(arr.split("-").length>=3){
		    			String tempPartNumber=arr.substring(arr.indexOf("-")+1,arr.lastIndexOf("-"));
			    		if(StringUtils.isNotBlank(tempPartNumber)){
			    			if(tempPartNumber.toLowerCase().endsWith("new")){
				    			tempPartNumber=tempPartNumber.substring(0, tempPartNumber.length()-3);
				    		}
			    			if(tempPartNumber.toLowerCase().startsWith("new")){
				    			tempPartNumber=tempPartNumber.substring(3, tempPartNumber.length());
				    		}
			    			for (int i=0;i<=9;i++) {
			    				if(tempPartNumber.toLowerCase().endsWith("new"+i)){
			    					tempPartNumber=tempPartNumber.substring(0, tempPartNumber.length()-4);
			    					break;
			    				}
							}
			    			if(partNumber.equals(tempPartNumber)||partNumber.equals(tempPartNumber+suffix)||partNumber.equals(tempPartNumber+"-"+suffix)||("ca".equals(country)&&(partNumber.equals(tempPartNumber+"-US")||partNumber.equals(tempPartNumber+"US")))){
			    				flag=false;
			    				break;
			    			}
			    		}
		    		}else if(arr.split("-").length==2){
		    			String tempPartNumber=arr.split("-")[0];
		    			if(partNumber.equals(tempPartNumber)||partNumber.equals(tempPartNumber+suffix)||partNumber.equals(tempPartNumber+"-"+suffix)||("ca".equals(country)&&(partNumber.equals(tempPartNumber+"-US")||partNumber.equals(tempPartNumber+"US")))){
		    				flag=false;
		    				break;
		    			}
		    		}
				}
		    	if(flag){
		    		List<AmazonPostsDetail> temp=map.get(country);
			    	if(temp==null){
			    		temp=Lists.newArrayList();
			    		map.put(country, temp);
			    	}
		    		temp.add(detail);
		    	}
		    }
		 }  	
		 return map;
	}
	
	public Map<String,List<AmazonCatalogRank>>   findCatalogByProduct(String country,List<String> productName){
		 Date today = DateUtils.getDateStart(new Date());
		 Map<String,List<AmazonCatalogRank>> map=Maps.newHashMap();
		 String sql="SELECT distinct product_name,catalog,asin,catalog_name,path,path_name FROM amazoninfo_catalog_rank r WHERE r.`country`=:p1 AND r.`product_name` in :p2 AND r.`query_time`=:p3 ";
		 List<Object[]> rs = amazonPostsDetailDao.findBySql(sql, new Parameter(country,productName,today));
		 if(rs!=null&&rs.size()>0){
		    for (Object[] obj:rs) {
		    	String name=obj[0].toString();
		    	String catalog=obj[1].toString();
		    	String asin=obj[2].toString();
		    	String catalogName=(obj[3]==null?"":obj[3].toString());
		    	String path=(obj[4]==null?"":obj[4].toString());
		    	String pathName=(obj[5]==null?"":obj[5].toString());
		    	List<AmazonCatalogRank> temp=map.get(catalog);
		    	if(temp==null){
		    		temp=Lists.newArrayList();
		    		map.put(catalog, temp);
		    	}
		    	AmazonCatalogRank rank=new AmazonCatalogRank();
		    	rank.setAsin(asin);
		    	rank.setCatalog(catalog);
		    	rank.setCountry(country);
		    	rank.setProductName(name);
		    	rank.setCatalogName(catalogName);
		    	rank.setPath(path);
		    	rank.setPathName(pathName);
		    	temp.add(rank);
		    }
		 }else{
			    int i = 1 ;
				while((rs==null||rs.size()==0)&&i<10){
				   rs=amazonPostsDetailDao.findBySql(sql, new Parameter(country,productName,DateUtils.getDateStart(DateUtils.addDays(today, 0-i))));
				   if(rs!=null&&rs.size()>0){
					   for (Object[] obj:rs) {
						   String name=obj[0].toString();
					    	String catalog=obj[1].toString();
					    	String asin=obj[2].toString();
					    	String catalogName=(obj[3]==null?"":obj[3].toString());
					    	String path=(obj[4]==null?"":obj[4].toString());
					    	String pathName=(obj[5]==null?"":obj[5].toString());
					    	List<AmazonCatalogRank> temp=map.get(catalog);
					    	if(temp==null){
					    		temp=Lists.newArrayList();
					    		map.put(catalog, temp);
					    	}
					    	AmazonCatalogRank rank=new AmazonCatalogRank();
					    	rank.setAsin(asin);
					    	rank.setCatalog(catalog);
					    	rank.setCountry(country);
					    	rank.setProductName(name);
					    	rank.setCatalogName(catalogName);
					    	rank.setPath(path);
					    	rank.setPathName(pathName);
					    	temp.add(rank);
					    }
						return map;
				   }else{
					   i++;	 
				   }
				}
		 }
		 return map;
	}
	
	public Map<String,Map<String,Map<String,AmazonNewReleasesRank>>> findNewReleasesRank(AmazonNewReleasesRank amazonNewReleasesRank){
		Map<String,Map<String,Map<String,AmazonNewReleasesRank>>>  map=Maps.newHashMap();
		String sql="SELECT product_name,catalog,catalog_name,rank,DATE_FORMAT(query_time,'%Y%m%d') dates FROM amazoninfo_new_releases_rank WHERE country=:p1 AND query_time>=:p2 AND query_time<=:p3";
		List<Object[]> list=amazonNewReleasesRankDao.findBySql(sql,new Parameter(amazonNewReleasesRank.getCountry(),amazonNewReleasesRank.getQueryTime(),amazonNewReleasesRank.getEndTime()));
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				String name=obj[0].toString();
				String catalog=obj[1].toString();
				String catalogName=(("".equals(obj[2]))?catalog:obj[2].toString());
				Integer rank=Integer.parseInt(obj[3].toString());
				String date=obj[4].toString();
				Map<String,Map<String,AmazonNewReleasesRank>> temp=map.get(name);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(name, temp);
				}
				Map<String,AmazonNewReleasesRank> catalogMap=temp.get(catalogName);
				if(catalogMap==null){
					catalogMap=Maps.newHashMap();
					temp.put(catalogName, catalogMap);
				}
				AmazonNewReleasesRank enty=new AmazonNewReleasesRank();
				enty.setProductName(name);
				enty.setCatalog(catalog);
				enty.setCatalogName(catalogName);
				enty.setRank(rank);
				catalogMap.put(date, enty);
			}
		}
		return map;
	}
	
	public Map<String,Map<String,Map<String,AmazonNewReleasesRank>>>  findNewReleasesRank2(AmazonNewReleasesRank amazonNewReleasesRank){
		Map<String,Map<String,Map<String,AmazonNewReleasesRank>>>  map=Maps.newHashMap();
		String sql="SELECT product_name,catalog,catalog_name,rank,DATE_FORMAT(query_time,'%Y%m%d') dates,country,asin FROM amazoninfo_new_releases_rank WHERE query_time>=:p1 AND query_time<=:p2 and product_name=:p3 and country=:p4 ";
		List<Object[]> list=amazonNewReleasesRankDao.findBySql(sql,new Parameter(amazonNewReleasesRank.getQueryTime(),amazonNewReleasesRank.getEndTime(),amazonNewReleasesRank.getProductName(),amazonNewReleasesRank.getCountry()));
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				String name=obj[0].toString();
				String catalog=obj[1].toString();
				String catalogName=(("".equals(obj[2]))?catalog:obj[2].toString());
				Integer rank=Integer.parseInt(obj[3].toString());
				String date=obj[4].toString();
				String country=obj[5].toString();
				String asin=obj[6].toString();
				Map<String,Map<String,AmazonNewReleasesRank>> temp=map.get(country);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(country, temp);
				}
				
				Map<String,AmazonNewReleasesRank> catalogMap=temp.get(catalogName+"_"+asin);
				if(catalogMap==null){
					catalogMap=Maps.newHashMap();
					temp.put(catalogName+"_"+asin, catalogMap);
				}
				AmazonNewReleasesRank enty=new AmazonNewReleasesRank();
				enty.setProductName(name);
				enty.setCatalog(catalog);
				enty.setCatalogName(catalogName);
				enty.setRank(rank);
				enty.setCountry(country);
				catalogMap.put(date, enty);
			}
		}
		return map;
	}

	/**
	 * 最新帖子asin和产品(不带颜色)的对应关系
	 * @param country
	 * @return
	 */
	public Map<String, String> findNewAsinNameMap(String country) {
		Map<String, String> rs = Maps.newHashMap();
		String sql = "SELECT t.`ASIN`,SUBSTRING_INDEX(t.`product_name`,'_',1) FROM `amazoninfo_posts_detail` t WHERE t.`country`=:p1 AND"+ 
				" t.`query_time`=(SELECT MAX(a.`query_time`) FROM amazoninfo_posts_detail a) AND t.`product_name` IS NOT NULL";
		List<Object[]> list = amazonPostsDetailDao.findBySql(sql, new Parameter(country));
		for (Object[] obj : list) {
			rs.put(obj[0].toString(), obj[1].toString());
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void saveEan(AmazonEan amazonEan) {
		amazonEanDao.save(amazonEan);
	}
	
	@Transactional(readOnly = false)
	public void saveEan(List<AmazonEan> amazonEan) {
		amazonEanDao.save(amazonEan);
	}
	
	@Transactional(readOnly = false)
	public void updateActive(AmazonEan amazonEan) {
		String sql="update amazoninfo_ean set active=:p1 where id=:p2";
		amazonEanDao.updateBySql(sql, new Parameter(amazonEan.getActive(),amazonEan.getId()));
	}
	
	public Integer isExist(String ean){
		String sql="select id from amazoninfo_ean where ean=:p1 ";
		List<Integer> list=amazonEanDao.findBySql(sql,new Parameter(ean));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public AmazonEan findEan(String ean){
		DetachedCriteria dc = amazonEanDao.createDetachedCriteria();
		dc.add(Restrictions.eq("ean",ean));
		List<AmazonEan> eanList=amazonEanDao.find(dc);
		if(eanList!=null&&eanList.size()>0){
			return eanList.get(0);
		}
		return null;
	}
	
	public Page<AmazonEan> find(Page<AmazonEan> page, AmazonEan amazonEan) {
		DetachedCriteria dc = amazonEanDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(amazonEan.getActive())){
			dc.add(Restrictions.eq("active",amazonEan.getActive()));
		}
		if(StringUtils.isNotBlank(amazonEan.getEan())){
			dc.add(Restrictions.eq("ean",amazonEan.getEan()));
		}
		if(StringUtils.isNotBlank(amazonEan.getProductName())){
			dc.add(Restrictions.like("productName","%"+amazonEan.getProductName()+"%"));
		}
		if(StringUtils.isNotBlank(amazonEan.getCountry())){
			dc.add(Restrictions.eq("country",amazonEan.getCountry()));
		}
		return amazonEanDao.find(page, dc);
	}
	
	public Map<String,AmazonProductTypeCharge> findCharge(){
		DetachedCriteria dc = amazonProductTypeChargeDao.createDetachedCriteria();
		List<AmazonProductTypeCharge> codeList=amazonProductTypeChargeDao.find(dc);
		Map<String,AmazonProductTypeCharge> map=Maps.newHashMap();
		if(codeList!=null&&codeList.size()>0){
			for (AmazonProductTypeCharge amazonProductTypeCharge : codeList) {
				map.put(amazonProductTypeCharge.getProductType()+"_"+amazonProductTypeCharge.getCountry(),amazonProductTypeCharge);
			}
		}
		return map;
	}
	
	public AmazonProductTypeCharge findCommissionPcent(Integer id){
		return amazonProductTypeChargeDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void saveCommissionPcent(AmazonProductTypeCharge charge) {
		amazonProductTypeChargeDao.save(charge);
	}
	
	
	public Map<String,AmazonProductTypeCode> find(){
		DetachedCriteria dc = amazonProductTypeCodeDao.createDetachedCriteria();
		List<AmazonProductTypeCode> codeList=amazonProductTypeCodeDao.find(dc);
		Map<String,AmazonProductTypeCode> map=Maps.newHashMap();
		if(codeList!=null&&codeList.size()>0){
			for (AmazonProductTypeCode amazonProductTypeCode : codeList) {
				map.put(amazonProductTypeCode.getProductType(),amazonProductTypeCode);
			}
		}
		return map;
	}
	
	
	public AmazonProductTypeCode findCode(Integer id){
		return amazonProductTypeCodeDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void saveCode(AmazonProductTypeCode code) {
		amazonProductTypeCodeDao.save(code);
	}
	
	@Transactional(readOnly = false)
	public void updateEan(String ean,Set<String> countrySet) {
		String sql="update amazoninfo_ean set active='1' where ean=:p1 and country in :p2 ";//1失效
		amazonEanDao.updateBySql(sql, new Parameter(ean,countrySet));
	}
	
	@Transactional(readOnly = false)
	public void updateEanIsUse(String ean,String name,String country,String accountName) {
		String sql="update amazoninfo_ean set active='2',product_name=:p1,country=:p2,account_name=:p3 where ean=:p4";//1失效
		amazonEanDao.updateBySql(sql, new Parameter(name,country,accountName,ean));
	}
	
	public String findEanByProductName(String productName,String accountName){
		String sql="select ean from amazoninfo_ean where active='2' and product_name=:p1 and account_name = :p2 ";
		List<String> list=amazonEanDao.findBySql(sql, new Parameter(productName,accountName));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return "";
	}
	
	public String findEuEanByProductName(String productName,String accountName){
		String sql="select ean from amazoninfo_ean where active='2' and product_name=:p1 and account_name like :p2 and country in ('de','fr','it','es','uk') ";
		List<String> list=amazonEanDao.findBySql(sql, new Parameter(productName,accountName.split("_")[0]+"%"));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return "";
	}
	
	public String findActiveEan(){
		String sql="select ean from amazoninfo_ean where active='0' order by create_date asc ";
		List<String> list=amazonEanDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public boolean countEanActive(){
		String sql="SELECT COUNT(*) FROM amazoninfo_ean WHERE active='0'";
		List<Object> list=amazonEanDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			if(Integer.parseInt(list.get(0).toString())>0&&Integer.parseInt(list.get(0).toString())<20){
				return true;
			}
		}
		return false;
	}
	
	public Map<String,Map<String,Integer>> getProductRank(List<String> nameList) {
		Date today=new Date();
		Map<String,Map<String,Integer>> rs = Maps.newHashMap();
		String sql="SELECT product_name,DATE_FORMAT(query_time,'%Y%m%d') dates,min(rank),country FROM amazoninfo_catalog_rank WHERE 1=1 ";
		if(nameList!=null){
			sql+=" and product_name in :p3 ";
		}
		sql+=" and query_time>=:p1 and query_time<=:p2 and catalog not like '%on_website' group by dates,product_name,country ";
		List<Object[]> rankList=null;
		if(nameList!=null){
			rankList=amazonCatalogRankDao.findBySql(sql,new Parameter(DateUtils.addMonths(today,-1),today,nameList));
		}else{
			rankList=amazonCatalogRankDao.findBySql(sql,new Parameter(DateUtils.addMonths(today,-1),today));
		}
		for (Object[] obj : rankList) {
			String name=(obj[0]==null?"":obj[0].toString());
			String date=obj[1].toString();
			Integer rank=Integer.parseInt(obj[2].toString());
			String country=obj[3].toString();
			Map<String,Integer> temp=rs.get(country+"-"+name);
			if(temp==null){
				temp=Maps.newHashMap();
				rs.put(country+"-"+name, temp);
			}
			temp.put(date, rank);
		}
		return rs;
	}
	
	public String  findImage(String productName){
		String sql="SELECT picture1 FROM amazoninfo_posts_detail d WHERE d.`query_time`=CURDATE() AND d.`product_name`=:p1 AND d.`picture1` IS NOT NULL AND  d.`picture1`!='' ORDER BY FIELD(d.country,'de','com','fr','uk','jp','it','es','ca','mx') ";
		List<String> list=amazonPostsDetailDao.findBySql(sql, new Parameter(productName));
		if(list!=null&&list.size()>0&&list.get(0)!=null){
			return list.get(0);
		}
		return null;
	}
	
	public List<Object[]> findChangePic(){
		String imgSql=" SELECT f.`country`,a.`type`,SUBSTRING_INDEX(f.`result`,':',1) FROM amazoninfo_image_feed f  "+
					" JOIN amazoninfo_image a ON f.id=a.`feed_image_feed_id` "+
					" WHERE f.`request_date`>=DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND f.`state`='3' ";
		List<Object[]> imgList=amazonPostsDetailDao.findBySql(imgSql);
		Set<String> typeSet=Sets.newHashSet();
		for (Object[] obj: imgList) {
			typeSet.add(obj[0].toString()+","+obj[2].toString()+","+obj[1].toString());
		}
		
		String sql="SELECT aa.product_name,aa.country,aa.asin, "+
				" (CASE WHEN SUBSTRING_INDEX(aa.picture1, '/', -1)!=SUBSTRING_INDEX(bb.picture1, '/', -1) THEN 'Main' WHEN SUBSTRING_INDEX(aa.picture2, '/', -1)!=SUBSTRING_INDEX(bb.picture2, '/', -1) THEN 'PT1' WHEN SUBSTRING_INDEX(aa.picture3, '/', -1)!=SUBSTRING_INDEX(bb.picture3, '/', -1) THEN 'PT2' "+
				" WHEN SUBSTRING_INDEX(aa.picture4, '/', -1)!=SUBSTRING_INDEX(bb.picture4, '/', -1) THEN 'PT3' WHEN SUBSTRING_INDEX(aa.picture5, '/', -1)!=SUBSTRING_INDEX(bb.picture5, '/', -1) THEN 'PT4' WHEN SUBSTRING_INDEX(aa.picture6, '/', -1)!=SUBSTRING_INDEX(bb.picture6, '/', -1) THEN 'PT5'  "+
				" WHEN SUBSTRING_INDEX(aa.picture7, '/', -1)!=SUBSTRING_INDEX(bb.picture7, '/', -1) THEN 'PT6' WHEN SUBSTRING_INDEX(aa.picture8, '/', -1)!=SUBSTRING_INDEX(bb.picture8, '/', -1) THEN 'PT7' ELSE 'PT8' END) picType,  "+
				
				" (CASE WHEN SUBSTRING_INDEX(aa.picture1, '/', -1)!=SUBSTRING_INDEX(bb.picture1, '/', -1) THEN SUBSTRING_INDEX(aa.picture1, '/', -1) WHEN SUBSTRING_INDEX(aa.picture2, '/', -1)!=SUBSTRING_INDEX(bb.picture2, '/', -1) THEN SUBSTRING_INDEX(aa.picture2, '/', -1) WHEN SUBSTRING_INDEX(aa.picture3, '/', -1)!=SUBSTRING_INDEX(bb.picture3, '/', -1) THEN SUBSTRING_INDEX(aa.picture3, '/', -1) "+
				" WHEN SUBSTRING_INDEX(aa.picture4, '/', -1)!=SUBSTRING_INDEX(bb.picture4, '/', -1) THEN SUBSTRING_INDEX(aa.picture4, '/', -1) WHEN SUBSTRING_INDEX(aa.picture5, '/', -1)!=SUBSTRING_INDEX(bb.picture5, '/', -1) THEN SUBSTRING_INDEX(aa.picture5, '/', -1) WHEN SUBSTRING_INDEX(aa.picture6, '/', -1)!=SUBSTRING_INDEX(bb.picture6, '/', -1) THEN SUBSTRING_INDEX(aa.picture6, '/', -1)  "+
				" WHEN SUBSTRING_INDEX(aa.picture7, '/', -1)!=SUBSTRING_INDEX(bb.picture7, '/', -1) THEN SUBSTRING_INDEX(aa.picture7, '/', -1) WHEN SUBSTRING_INDEX(aa.picture8, '/', -1)!=SUBSTRING_INDEX(bb.picture8, '/', -1) THEN SUBSTRING_INDEX(aa.picture8, '/', -1) ELSE SUBSTRING_INDEX(aa.picture9, '/', -1) END) pic1, "+
								
				" (CASE WHEN SUBSTRING_INDEX(aa.picture1, '/', -1)!=SUBSTRING_INDEX(bb.picture1, '/', -1) THEN SUBSTRING_INDEX(bb.picture1, '/', -1) WHEN SUBSTRING_INDEX(aa.picture2, '/', -1)!=SUBSTRING_INDEX(bb.picture2, '/', -1) THEN SUBSTRING_INDEX(bb.picture2, '/', -1) WHEN SUBSTRING_INDEX(aa.picture3, '/', -1)!=SUBSTRING_INDEX(bb.picture3, '/', -1) THEN SUBSTRING_INDEX(bb.picture3, '/', -1) "+
				" WHEN SUBSTRING_INDEX(aa.picture4, '/', -1)!=SUBSTRING_INDEX(bb.picture4, '/', -1) THEN SUBSTRING_INDEX(bb.picture4, '/', -1) WHEN SUBSTRING_INDEX(aa.picture5, '/', -1)!=SUBSTRING_INDEX(bb.picture5, '/', -1) THEN SUBSTRING_INDEX(bb.picture5, '/', -1) WHEN SUBSTRING_INDEX(aa.picture6, '/', -1)!=SUBSTRING_INDEX(bb.picture6, '/', -1) THEN SUBSTRING_INDEX(bb.picture6, '/', -1)  "+
				" WHEN SUBSTRING_INDEX(aa.picture7, '/', -1)!=SUBSTRING_INDEX(bb.picture7, '/', -1) THEN SUBSTRING_INDEX(bb.picture7, '/', -1) WHEN SUBSTRING_INDEX(aa.picture8, '/', -1)!=SUBSTRING_INDEX(bb.picture8, '/', -1) THEN SUBSTRING_INDEX(bb.picture8, '/', -1) ELSE SUBSTRING_INDEX(bb.picture9, '/', -1) END) pic2 "+
				" FROM   "+
				" (SELECT a.country,a.asin,a.product_name,a.picture1,a.picture2,a.picture3,a.picture4,a.picture5,a.picture6,a.picture7,a.picture8,a.picture9  "+
				" FROM amazoninfo_posts_detail a WHERE a.`query_time` = CURDATE() AND product_name IS NOT NULL) aa  "+
				" ,(SELECT a.country,a.asin,a.product_name,a.picture1,a.picture2,a.picture3,a.picture4,a.picture5,a.picture6,a.picture7,a.picture8,a.picture9  "+
				"  FROM amazoninfo_posts_detail a WHERE a.`query_time` = DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND product_name IS NOT NULL) bb   "+
				"  WHERE aa.country=bb.country AND aa.asin=bb.asin   "+
				"  AND (SUBSTRING_INDEX(aa.picture1, '/', -1)!=SUBSTRING_INDEX(bb.picture1, '/', -1) OR SUBSTRING_INDEX(aa.picture2, '/', -1)!=SUBSTRING_INDEX(bb.picture2, '/', -1) OR SUBSTRING_INDEX(aa.picture3, '/', -1)!=SUBSTRING_INDEX(bb.picture3, '/', -1) OR SUBSTRING_INDEX(aa.picture4, '/', -1)!=SUBSTRING_INDEX(bb.picture4, '/', -1) OR SUBSTRING_INDEX(aa.picture5, '/', -1)!=SUBSTRING_INDEX(bb.picture5, '/', -1)  "+
				"  OR SUBSTRING_INDEX(aa.picture6, '/', -1)!=SUBSTRING_INDEX(bb.picture6, '/', -1) OR SUBSTRING_INDEX(aa.picture7, '/', -1)!=SUBSTRING_INDEX(bb.picture7, '/', -1) OR SUBSTRING_INDEX(aa.picture8, '/', -1)!=SUBSTRING_INDEX(bb.picture8, '/', -1) OR SUBSTRING_INDEX(aa.picture9, '/', -1)!=SUBSTRING_INDEX(bb.picture9, '/', -1)) ORDER BY FIELD(aa.country,'com','de','fr','uk','jp','it','es','ca','mx') ";
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql);
		List<Object[]> tempList=Lists.newArrayList();
		List<String> euList=Lists.newArrayList("de","fr","it","es","uk");
		
		List<String> keyBoardAndHasPowerList=saleReportService.findKeyBoardAndHasPowerAllProduct();
		
		for (Object[] obj: list) {
			
			if("de,fr,it,es,uk".contains(obj[1].toString())){
				boolean flag=true;
				if(!keyBoardAndHasPowerList.contains(obj[0].toString())){//eu
					for (String tempCountry: euList) {
						String key=tempCountry+","+obj[0].toString()+","+obj[3].toString();
						if(typeSet.contains(key)){
							flag=false;
							break;
						}
					}
				}else{
					if("de,fr,it,es".contains(obj[1].toString())){
						for (String tempCountry: euList) {
							if(!"uk".equals(tempCountry)){
								String key=tempCountry+","+obj[0].toString()+","+obj[3].toString();
								if(typeSet.contains(key)){
									flag=false;
									break;
								}
							}
						}
					}else{
						if(typeSet.contains(obj[1].toString()+","+obj[0].toString()+","+obj[3].toString())){
							flag=false;
							break;
						}
					}
				}
				
				if(flag){
					typeSet.add(obj[1].toString()+","+obj[0].toString()+","+obj[3].toString());
					tempList.add(obj);
				}
			}else{
				String key=obj[1].toString()+","+obj[0].toString()+","+obj[3].toString();
				if(!typeSet.contains(key)){
					tempList.add(obj);
				}
			}
		}
		return tempList;
	}
	
	public Map<String,AmazonPostsDetail> findPostsSize(){
		Map<String,AmazonPostsDetail> map=Maps.newHashMap();
		String sql="SELECT d.`product_name`,d.`package_length`,d.`package_width`,d.`package_height`,d.`package_weight`,d.country "+
				" FROM amazoninfo_posts_detail d WHERE d.`query_time`=DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND d.product_name IS NOT NULL AND "+
				" d.`package_length` IS NOT NULL and d.`package_width` IS NOT NULL and  d.`package_height` IS NOT NULL and d.`package_weight` IS NOT NULL "+
				" union SELECT d.`product_name`,d.`package_length`,d.`package_width`,d.`package_height`,d.`package_weight`,d.country "+
				" FROM amazoninfo_posts_detail d WHERE d.`query_time`='2017-10-10 00:00:00' and d.country='com' AND d.product_name IS NOT NULL AND "+
				" d.`package_length` IS NOT NULL and d.`package_width` IS NOT NULL and  d.`package_height` IS NOT NULL and d.`package_weight` IS NOT NULL ";
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql);
		for (Object[] obj: list) {
			String name=obj[0].toString();
			Float length=Float.parseFloat(obj[1].toString());
			Float width=Float.parseFloat(obj[2].toString());
			Float height=Float.parseFloat(obj[3].toString());
			Float weight=Float.parseFloat(obj[4].toString());
			AmazonPostsDetail detail=new AmazonPostsDetail();
			detail.setPackageLength(length);
			detail.setPackageWidth(width);
            detail.setPackageHeight(height);
            detail.setPackageWeight(weight);
            map.put(name+obj[5].toString(),detail);
		}
		return map;
	}
	
	public Map<String,AmazonPostsDetail> findPostsSize(String queryName){
		Map<String,AmazonPostsDetail> map=Maps.newHashMap();
		String sql="SELECT d.`package_length`,d.`package_width`,d.`package_height`,d.`package_weight`,d.country "+
				" FROM amazoninfo_posts_detail d WHERE d.`query_time`=DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND d.product_name=:p1 AND "+
				" d.`package_length` IS NOT NULL and d.`package_width` IS NOT NULL and  d.`package_height` IS NOT NULL and d.`package_weight` IS NOT NULL ";
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql,new Parameter(queryName));
		for (Object[] obj: list) {
			Float length=Float.parseFloat(obj[0].toString());
			Float width=Float.parseFloat(obj[1].toString());
			Float height=Float.parseFloat(obj[2].toString());
			Float weight=Float.parseFloat(obj[3].toString());
			AmazonPostsDetail detail=new AmazonPostsDetail();
			detail.setPackageLength(length);
			detail.setPackageWidth(width);
            detail.setPackageHeight(height);
            detail.setPackageWeight(weight);
            map.put(obj[4].toString(),detail);
		}
		return map;
	}
	
	public Map<String,Integer> find(String type){
		DetachedCriteria dc = amazonProductTypeChargeDao.createDetachedCriteria();
		dc.add(Restrictions.eq("productType",type));
		List<AmazonProductTypeCharge> codeList=amazonProductTypeChargeDao.find(dc);
		Map<String,Integer> map=Maps.newHashMap();
		if(codeList!=null&&codeList.size()>0){
			for (AmazonProductTypeCharge charge : codeList) {
				map.put(charge.getCountry(),charge.getCommissionPcent());
			}
		}
		return map;
	}
	
	
	public Map<Integer,Integer> findQuantityByRank(String country,String catalog){
		Map<Integer,Integer> map=Maps.newLinkedHashMap();
		String sql="SELECT k.rank,ROUND(AVG(t.`sales_volume`)) quantity FROM amazoninfo_catalog_rank k "+
				" JOIN amazoninfo_sale_report t ON t.`country`=k.`country` AND t.`date`=DATE_SUB(k.`query_time`,INTERVAL 1 DAY) "+
				" AND k.`product_name`=CONCAT(t.`product_name`,CASE WHEN t.`color`='' THEN '' ELSE CONCAT('_',t.`color`) END) "+
				" WHERE k.`country`=:p1 AND k.`catalog`=:p2 and k.query_time>=DATE_SUB(CURDATE(),INTERVAL 6 MONTH)    "+
				" AND k.`rank`<=100  GROUP BY k.rank order by k.rank asc ";
		List<Object[]> list=amazonPostsDetailDao.findBySql(sql,new Parameter(country,catalog));
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				map.put(Integer.parseInt(obj[0].toString()),Integer.parseInt(obj[1].toString()));
			}
		}
		return map;
	}
	
	public List<AmazonPostsDetail> findParentPosts(String country){
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		dc.add(Restrictions.gt("queryTime",DateUtils.addDays(new Date(),-1)));
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.isNull("sku"));
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		for (AmazonPostsDetail amazonPostsDetail : list) {
			Hibernate.initialize(amazonPostsDetail);
		}
		return list;
	}
	
	public List<AmazonPostsDetail> findChildsPosts(String country){
		DetachedCriteria dc = amazonPostsDetailDao.createDetachedCriteria();
		dc.add(Restrictions.gt("queryTime",DateUtils.addDays(new Date(),-1)));
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.isNotNull("sku"));
		List<AmazonPostsDetail> list=amazonPostsDetailDao.find(dc);
		for (AmazonPostsDetail amazonPostsDetail : list) {
			Hibernate.initialize(amazonPostsDetail);
		}
		return list;
		
	}
	
}
