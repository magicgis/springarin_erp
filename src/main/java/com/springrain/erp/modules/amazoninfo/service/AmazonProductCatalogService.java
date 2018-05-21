/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.amazoninfo.dao.AmazonProductCatalogDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonTiledCatalogDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProductCatalog;
import com.springrain.erp.modules.amazoninfo.entity.AmazonTiledCatalog;

@Component
@Transactional(readOnly = true)
public class AmazonProductCatalogService extends BaseService {

	@Autowired
	private AmazonProductCatalogDao amazonProductCatalogDao;
	
	@Autowired
	private AmazonTiledCatalogDao amazonTiledCatalogDao;

	public AmazonProductCatalog get(Integer id){
		return amazonProductCatalogDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(AmazonProductCatalog amazonProductCatalog) {
		amazonProductCatalogDao.save(amazonProductCatalog);
	}
	
	@Transactional(readOnly = false)
	public void save(List<AmazonProductCatalog> amazonProductCatalog) {
		amazonProductCatalogDao.save(amazonProductCatalog);
	}

	public AmazonProductCatalog find(AmazonProductCatalog amazonProductCatalog) {
		DetachedCriteria dc = amazonProductCatalogDao.createDetachedCriteria();
		dc.add(Restrictions.eq("pathName",amazonProductCatalog.getPathName()));
		List<AmazonProductCatalog> rs = amazonProductCatalogDao.find(dc);
		if(rs!=null&&rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	public AmazonProductCatalog findPathName(String pathName) {
		DetachedCriteria dc = amazonProductCatalogDao.createDetachedCriteria();
		dc.add(Restrictions.eq("pathName",pathName));
		List<AmazonProductCatalog> rs = amazonProductCatalogDao.find(dc);
		if(rs!=null&&rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	public List<AmazonProductCatalog> findAllCatalogByCountry(String country){
		DetachedCriteria dc = amazonProductCatalogDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.eq("isUse","0"));
		//dc.add(Restrictions.eq("delFlag","0"));
		/*if("de".equals(country)){
			dc.add(Restrictions.or(Restrictions.eq("pathName","computers"),Restrictions.eq("pathName","ce"),Restrictions.eq("pathName","appliances"),Restrictions.like("pathName","%computers/%"),Restrictions.like("pathName","%ce/%"),Restrictions.like("pathName","%appliances/%")));//
		}*/
		List<AmazonProductCatalog> rs = amazonProductCatalogDao.find(dc);
		return rs;
	}
	
	public Map<String,List<AmazonProductCatalog>> findAllCatalog(){
		Map<String,List<AmazonProductCatalog>> map=Maps.newHashMap();
		DetachedCriteria dc = amazonProductCatalogDao.createDetachedCriteria();
		//dc.add(Restrictions.eq("delFlag","0"));
		List<AmazonProductCatalog> rs = amazonProductCatalogDao.find(dc);
		for (AmazonProductCatalog catalog : rs) {
			List<AmazonProductCatalog> temp=map.get(catalog.getCountry());
			if(temp==null){
				temp=Lists.newArrayList();
				map.put(catalog.getCountry(), temp);
			}
			temp.add(catalog);
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void delete(String country) {
		String sql="delete from amazoninfo_product_catalogues where country=:p1";
		amazonProductCatalogDao.updateBySql(sql,new Parameter(country));
	}
	
	@Transactional(readOnly = false)
	public void truncateCatalog() {
		String sql="truncate table amazoninfo_product_catalogues";
		amazonProductCatalogDao.updateBySql(sql,null);
	}
	
	@Transactional(readOnly = false)
	public void updateIsUse(String country,List<String> pathList) {
		String sql="update amazoninfo_product_catalogues  set is_use='0' where country=:p1 and path_id in :p2 ";
		amazonProductCatalogDao.updateBySql(sql,new Parameter(country,pathList));
	}
	
	@Transactional(readOnly = false)
	public void updateAnalysisUse(String country,List<String> pathList) {
		String sql="update amazoninfo_catalog  set analysis_use='0' where country=:p1 and path_id in :p2 ";
		amazonProductCatalogDao.updateBySql(sql,new Parameter(country,pathList));
	}
	
	@Transactional(readOnly = false)
	public void updateIsUse(String country,Map<String,Set<String>> catalogMap) {
		String sql="update amazoninfo_product_catalogues  set is_use='0' where country=:p1 and path_name in :p2 and type=:p3 ";
		for(Map.Entry<String,Set<String>> entry :catalogMap.entrySet()){
		    String type=entry.getKey();
			amazonProductCatalogDao.updateBySql(sql,new Parameter(country,entry.getValue(),type));
		}
	}
	
	
	public List<AmazonProductCatalog> findCatalog(String countryStr){
		List<AmazonProductCatalog> catalogList=Lists.newArrayList();
		String temp = "";
		if (StringUtils.isNotEmpty(countryStr)) {
			temp = " and country='"+countryStr+"' ";
		}
		//String sql="SELECT country,path_id,path_name,TYPE FROM amazoninfo_product_catalogues WHERE path_id IS NOT NULL";
		String sql="SELECT country,path_id,GROUP_CONCAT(DISTINCT path_name order by path_name desc),GROUP_CONCAT(DISTINCT TYPE order by type desc),GROUP_CONCAT(DISTINCT parent_id order by parent_id desc) FROM amazoninfo_product_catalogues "+
        " WHERE path_id IS NOT NULL AND TYPE NOT IN ('books','software','videogames','dvd','food','music','musical-instruments','video-games') "+temp+" GROUP BY country,path_id ";
		List<Object[]> list=amazonProductCatalogDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				String country=obj[0].toString();
				String pathId=obj[1].toString();
				String pathName=obj[2].toString();
				String type=(obj[3]==null?"":obj[3].toString());
				
				AmazonProductCatalog catalog=new AmazonProductCatalog();
				catalog.setCountry(country);
				catalog.setPathId(pathId);
				catalog.setPathName(pathName);
				catalog.setType(type);
				if(obj[4]!=null){
					String parentId=obj[4].toString();
					if(StringUtils.isNotBlank(parentId.split(",")[0])){
						AmazonProductCatalog parentCatalog=get(Integer.parseInt(parentId.split(",")[0]));
						if(parentCatalog!=null){
							catalog.setItemType(parentCatalog.getPathId());
						}
					}
				}
				catalogList.add(catalog);
			}
		}
		return catalogList;
	}
	
	public Map<String,Set<String>> findParentPathId(List<String> childPathList,String country){
		Map<String,Set<String>> map=Maps.newHashMap();
		DetachedCriteria dc = amazonProductCatalogDao.createDetachedCriteria();
		dc.add(Restrictions.in("pathId", childPathList));
		dc.add(Restrictions.eq("country", country));
		List<AmazonProductCatalog> rs = amazonProductCatalogDao.find(dc);
		for (AmazonProductCatalog catalog: rs) {
			String type=catalog.getType();
			String pathName=catalog.getPathName();
			if(pathName.contains("/")){
				String[] nameArr=pathName.split("/");
				for (int i=0;i<nameArr.length;i++) {
					String name=nameArr[i];
					String allName="";
					StringBuffer buf= new StringBuffer();
					if(StringUtils.isNotBlank(name)){
						if(i==0){
							allName=name;
				    	}else{
				    		for (int j=0;j<=i-1;j++) {
				    			buf.append(nameArr[j]+"/");
				    		}	
				    		allName =buf.toString();
				    		allName=allName+name;
				    	}
						Set<String> catalogSet=map.get(type);
						if(catalogSet==null){
							catalogSet=Sets.newHashSet();
							map.put(type, catalogSet);
						}
						catalogSet.add(allName);
					}
				}
			}else{
				Set<String> catalogSet=map.get(type);
				if(catalogSet==null){
					catalogSet=Sets.newHashSet();
					map.put(type, catalogSet);
				}
				catalogSet.add(pathName);
			}
		}
		return map;
	}

	@Transactional(readOnly = false)
	public void splitCatalog(String country){
		DetachedCriteria dc = amazonProductCatalogDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country",country));
		List<AmazonProductCatalog> list = amazonProductCatalogDao.find(dc);
		List<AmazonTiledCatalog> tiledList = Lists.newArrayList();
		Set<String> pathIdSet = Sets.newHashSet();
		for (AmazonProductCatalog rs : list) {
			 if(rs.getParent()!=null&&(rs.getChildList()==null||rs.getChildList().size()==0)){
					String pathId = rs.getPathId();
					if(pathIdSet.contains(pathId)){
						continue;
					}
					pathIdSet.add(pathId);
					AmazonTiledCatalog tiled = new AmazonTiledCatalog();
					Integer parentId = rs.getParent().getId();
					int size = rs.getPathName().split("/").length;
					tiled.setCountry(rs.getCountry());
					tiled.setPathId(size,pathId);
					tiled.setIsUse(tiled.getIsUse());
					while(parentId!=null){
						size = size -1;
						parentId=findParent(parentId,size,tiled);
					}
					tiledList.add(tiled);
			 }
		}
		amazonTiledCatalogDao.save(tiledList);
	}
	
	public Integer findParent(Integer parentId,int size,AmazonTiledCatalog tiled){
		AmazonProductCatalog catalog = get(parentId);
		if(catalog!=null){
			if(catalog.getParent()==null){
				tiled.setCatalogName(catalog.getCatalogName());
			}else{
				if("0".equals(catalog.getParent().getIsUse())){
					tiled.setIsUse("0");
				}
				tiled.setPathId(size, catalog.getPathId());
				return catalog.getParent().getId();
			}
		}
		return null;
	}
	
	

	public String findCatalog(String asin,String country){
		String sql="select group_concat(catalogid) from category_info_product_5_4 where asin=:p1 and country=:p2 ";
		List<String> list = amazonProductCatalogDao.findBySql(sql,new Parameter(asin,country));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return "";
	}
	
	public List<String> findParaentCatalog(String catalog,String country){
		String sql = "select path_id1,path_id2,path_id3,path_id4,path_id5,path_id6,path_id7,path_id8 from amazoninfo_tiled_catalog where country=:p1 and (path_id1=:p2 or path_id2=:p2 or path_id3=:p2 or path_id4=:p2 or path_id5=:p2 or path_id6=:p2 or path_id7=:p2 or path_id8=:p2)";
		List<Object[]> list = amazonProductCatalogDao.findBySql(sql,new Parameter(country,catalog));
		List<String> rs = Lists.newArrayList();
		if(list!=null&&list.size()>0){
		   for (Object[] obj: list) {
			   if(obj[0]!=null&&obj[0].toString().equals(catalog)){
				   rs.add(obj[0].toString());    
			   }else if(obj[1]!=null&&obj[1].toString().equals(catalog)){
				   if(obj[0]!=null){
					   rs.add(obj[0].toString()); 
				   }
				   rs.add(obj[1].toString());    
			   }else if(obj[2]!=null&&obj[2].toString().equals(catalog)){
				   if(obj[0]!=null){
					   rs.add(obj[0].toString()); 
				   }
				   if(obj[1]!=null){
					   rs.add(obj[1].toString()); 
				   }
				   rs.add(obj[2].toString());  
			   }else if(obj[3]!=null&&obj[3].toString().equals(catalog)){
				   if(obj[0]!=null){
					   rs.add(obj[0].toString()); 
				   }
				   if(obj[1]!=null){
					   rs.add(obj[1].toString()); 
				   }
				   if(obj[2]!=null){
					   rs.add(obj[2].toString()); 
				   }
				   rs.add(obj[3].toString());  
			   }else if(obj[4]!=null&&obj[4].toString().equals(catalog)){
				   if(obj[0]!=null){
					   rs.add(obj[0].toString()); 
				   }
				   if(obj[1]!=null){
					   rs.add(obj[1].toString()); 
				   }
				   if(obj[2]!=null){
					   rs.add(obj[2].toString()); 
				   }
				   if(obj[3]!=null){
					   rs.add(obj[3].toString()); 
				   }
				   rs.add(obj[4].toString());  
			   }else if(obj[5]!=null&&obj[5].toString().equals(catalog)){
				   if(obj[0]!=null){
					   rs.add(obj[0].toString()); 
				   }
				   if(obj[1]!=null){
					   rs.add(obj[1].toString()); 
				   }
				   if(obj[2]!=null){
					   rs.add(obj[2].toString()); 
				   }
				   if(obj[3]!=null){
					   rs.add(obj[3].toString()); 
				   }
				   if(obj[4]!=null){
					   rs.add(obj[4].toString()); 
				   }
				   rs.add(obj[5].toString());  
			   }else if(obj[6]!=null&&obj[6].toString().equals(catalog)){
				   if(obj[0]!=null){
					   rs.add(obj[0].toString()); 
				   }
				   if(obj[1]!=null){
					   rs.add(obj[1].toString()); 
				   }
				   if(obj[2]!=null){
					   rs.add(obj[2].toString()); 
				   }
				   if(obj[3]!=null){
					   rs.add(obj[3].toString()); 
				   }
				   if(obj[4]!=null){
					   rs.add(obj[4].toString()); 
				   }
				   if(obj[5]!=null){
					   rs.add(obj[5].toString()); 
				   }
				   rs.add(obj[6].toString());  
			   }else if(obj[7]!=null&&obj[7].toString().equals(catalog)){
				   if(obj[0]!=null){
					   rs.add(obj[0].toString()); 
				   }
				   if(obj[1]!=null){
					   rs.add(obj[1].toString()); 
				   }
				   if(obj[2]!=null){
					   rs.add(obj[2].toString()); 
				   }
				   if(obj[3]!=null){
					   rs.add(obj[3].toString()); 
				   }
				   if(obj[4]!=null){
					   rs.add(obj[4].toString()); 
				   }
				   if(obj[5]!=null){
					   rs.add(obj[5].toString()); 
				   }
				   if(obj[6]!=null){
					   rs.add(obj[6].toString()); 
				   }
				   rs.add(obj[7].toString());  
			   }else if(obj[8]!=null&&obj[8].toString().equals(catalog)){
				   if(obj[0]!=null){
					   rs.add(obj[0].toString()); 
				   }
				   if(obj[1]!=null){
					   rs.add(obj[1].toString()); 
				   }
				   if(obj[2]!=null){
					   rs.add(obj[2].toString()); 
				   }
				   if(obj[3]!=null){
					   rs.add(obj[3].toString()); 
				   }
				   if(obj[4]!=null){
					   rs.add(obj[4].toString()); 
				   }
				   if(obj[5]!=null){
					   rs.add(obj[5].toString()); 
				   }
				   if(obj[6]!=null){
					   rs.add(obj[6].toString()); 
				   }
				   if(obj[7]!=null){
					   rs.add(obj[7].toString()); 
				   }
				   rs.add(obj[8].toString());  
			   }
		   }
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void updateCatalog(String catalog,Integer id){
		String updateSql = "update category_product_info_report set catalog=:p1 where id=:p2 ";
		 amazonProductCatalogDao.updateBySql(updateSql, new Parameter(catalog,id));
	}
	
	@Transactional(readOnly = false)
	public void updateCatalog(){
		String sql="select country,asin,id from category_product_info_report where asin is not null and catalog is null";
		List<Object[]> list = amazonProductCatalogDao.findBySql(sql);
		for (Object[] obj: list) {
			 String country = ("us".equals(obj[0].toString())?"com":obj[0].toString());
			 String asin = obj[1].toString();
			 Integer id = Integer.parseInt(obj[2].toString());
			 String catalog = findCatalog(asin,country);
			 if(StringUtils.isNotBlank(catalog)){
				 updateCatalog(catalog,id);
				 String[] arr = catalog.split(",");
				 for (String temp : arr) {
					 List<String> cagalogList = findParaentCatalog(temp,country);
					 if(cagalogList!=null&&cagalogList.size()>0){
						 updateAnalysisUse(country,cagalogList);
					 }
				 }
			 }
		}
	}
	
	
	public List<Object[]> findAll(){
		String sql="select country,asin,id from category_product_info_report where asin is not null and catalog is null";
		return amazonProductCatalogDao.findBySql(sql);
	}
	
	
	@Transactional(readOnly = false)
	public void updateCatalog(Object[] obj){
		
			 String country = ("us".equals(obj[0].toString())?"com":obj[0].toString());
			 String asin = obj[1].toString();
			 Integer id = Integer.parseInt(obj[2].toString());
			 String catalog = findCatalog(asin,country);
			 if(StringUtils.isNotBlank(catalog)){
				 updateCatalog(catalog,id);
				 String[] arr = catalog.split(",");
				 for (String temp : arr) {
					 List<String> cagalogList = findParaentCatalog(temp,country);
					 if(cagalogList!=null&&cagalogList.size()>0){
						 updateAnalysisUse(country,cagalogList);
					 }
				 }
			 }else{
				 updateCatalog("",id);
			 }
		
	}
	
	public List<AmazonProductCatalog> findCatalogByCountry(String country){
		List<AmazonProductCatalog> catalogList=Lists.newArrayList();
		String sql="select id,parent_id,catalog_name,path_id from  amazoninfo_catalog where country=:p1 and analysis_use='0' ";
		List<Object[]> orders= amazonProductCatalogDao.findBySql(sql,new Parameter(country));
		for (Object[] obj : orders) {
			AmazonProductCatalog catalog = new AmazonProductCatalog();
			catalog.setId(Integer.parseInt(obj[0].toString()));
			if(obj[1]!=null){
				AmazonProductCatalog temp = new AmazonProductCatalog();
				temp.setId(Integer.parseInt(obj[1].toString()));
				catalog.setParent(temp);
			}
			catalog.setCatalogName(obj[2]==null?"":obj[2].toString());
			catalog.setPathId(obj[3]==null?"":obj[3].toString());
			catalogList.add(catalog);
		}
		return catalogList;
		
		/*DetachedCriteria dc = amazonProductCatalogDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.eq("analysisUse","0"));
		List<AmazonProductCatalog> rs = amazonProductCatalogDao.find(dc);
		return rs;*/
	}
	
	public Map<String,String> findNum(){
		 Map<String,String> map = Maps.newHashMap();
		 String sql="select category_linkid,num,avg_price,lower_price,higher_price,sales,quantity from category_report_num where num>=60";
		 List<Object[]> orders= amazonProductCatalogDao.findBySql(sql);
		 for (Object[] obj : orders) {
			String num = obj[1].toString();
			String avgPrice = obj[2].toString();
			String lowerPrice = obj[3].toString();
			String higherPrice = obj[4].toString();
			String info= "取样数:"+num+" 销售额:"+obj[5].toString()+" 销量:"+obj[6].toString()+" 均价:"+avgPrice +" 最低:"+lowerPrice+" 最高:"+higherPrice;
			map.put(obj[0].toString(), info);
		 }
		 return map;
	}
	
	public Object[] findPrice(String catalog){
		String sql="select avg_price,lower_price,higher_price,num from category_report_num where category_linkid=:p1 ";
		List<Object[]> rs= amazonProductCatalogDao.findBySql(sql,new Parameter(catalog));
	    return rs.get(0);
	}
	
	@Transactional(readOnly = false)
	public void save(String country,String asin,String catalog,String brand,Float sales,Integer quantity){
		String sql="INSERT INTO category_product_report(country,ASIN,`catalog`,`data_date`,brand,sales,quantity) values(:p1,:p2,:p3,'2018-03-31',:p4,:p5,:p6)";
		amazonProductCatalogDao.updateBySql(sql, new Parameter(country,asin,catalog,brand,sales,quantity));
	}
}
