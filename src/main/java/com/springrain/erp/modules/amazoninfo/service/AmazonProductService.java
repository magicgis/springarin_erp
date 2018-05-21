package com.springrain.erp.modules.amazoninfo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonProductDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProduct;
import com.springrain.erp.modules.psi.entity.PsiSku;

/**
 * 亚马逊产品Service
 * @author tim
 * @version 2014-06-04
 */
@Component
@Transactional(readOnly = true)
public class AmazonProductService extends BaseService {
	@Autowired
	private AmazonProductDao amazonProductDao;
	public AmazonProduct get(String id) {
		return amazonProductDao.get(id);
	}
	
	public Page<AmazonProduct> find(Page<AmazonProduct> page, AmazonProduct amazonProduct) {
		DetachedCriteria dc = amazonProductDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(amazonProduct.getName())){
			dc.add(Restrictions.or(Restrictions.like("ean", "%"+amazonProduct.getName()+"%"),Restrictions.like("name", "%"+amazonProduct.getName()+"%"),Restrictions.like("sku", "%"+amazonProduct.getName()+"%"),Restrictions.like("asin", "%"+amazonProduct.getName()+"%")));
		}else{
			dc.add(Restrictions.isNull("parentProduct"));
		}
		if(StringUtils.isNotEmpty(amazonProduct.getCountry())){
			dc.add(Restrictions.eq("country",amazonProduct.getCountry()));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.asc("country"));
		dc.addOrder(Order.desc("active"));
		dc.addOrder(Order.asc("name"));
		return amazonProductDao.find(page, dc);
	}
	
	//事件编辑处，提供asin和名字备选
	public List<Map<String, String>> findAsin(String accountName) {
		List<Map<String, String>> rs = Lists.newArrayList();
		List<Object[]>  tempList =null;
		if (StringUtils.isNotEmpty(accountName)){
			String sql="select DISTINCT p.ASIN,p.sku,CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN CONCAT ('_',s.`color`) ELSE '' END) product_name from amazoninfo_product2 p " +
			        " left join (SELECT  product_name,account_name,sku,color FROM psi_sku WHERE `product_name` NOT LIKE '%other%' and `product_name` NOT LIKE '%Old%'  AND del_flag='0' ) s " +
			        " on s.sku=p.sku and p.account_name=s.account_name "+
					" where p.asin is not null and s.product_name IS NOT NULL and p.account_name =  :p1 ";
			tempList=amazonProductDao.findBySql(sql,new Parameter(accountName));
		}else{
			String sql="select DISTINCT  p.ASIN,p.sku,CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN CONCAT ('_',s.`color`) ELSE '' END) product_name from amazoninfo_product2 p " +
			        " left join (SELECT product_name,account_name,sku,color FROM psi_sku WHERE `product_name` NOT LIKE '%other%' and `product_name` NOT LIKE '%Old%' AND del_flag='0' ) s " +
			        " on s.sku=p.sku and p.account_name=s.account_name "+
					" where  p.asin is not null and  s.product_name IS NOT NULL ";
			tempList=amazonProductDao.findBySql(sql);
		}
		for (Object[] obj : tempList) {
				Map<String,String> map = Maps.newHashMap();
				map.put("key", obj[0].toString());
				String value = (obj[1]==null?"maybe obsolete goods":obj[1].toString());
				map.put("value", obj[2].toString()+"["+value+"]");
			    rs.add(map);
		}
		
		return rs;
	}
	
	public List<Map<String, String>> findSku(String country) {
		//排除已经下帖的sku
		String sql = "";
		List<Object[]>  skus =null;
		if (StringUtils.isNotEmpty(country)){
			sql = "SELECT DISTINCT a.`sku` FROM amazoninfo_product2 a WHERE a.`active` = '1' and a.country = :p1 ";
			skus=amazonProductDao.findBySql(sql,new Parameter(country));
		}else{
			sql = "SELECT DISTINCT a.`sku` FROM amazoninfo_product2 a WHERE a.`active` = '1' ";
			skus=amazonProductDao.findBySql(sql);
		}
		List<Map<String, String>> rs = Lists.newArrayList();
		List<Object[]>  tempList =null;
		if (StringUtils.isNotEmpty(country)){
			sql="SELECT distinct sku,CONCAT(product_name,CASE WHEN color!='' THEN CONCAT ('_',color) ELSE '' END) product_name FROM psi_sku WHERE product_name <> 'Inateck other' AND product_name <> 'Inateck Old' AND product_name IS NOT NULL AND sku IS NOT NULL AND del_flag='0' and country=:p1 and sku in :p2";
			tempList=amazonProductDao.findBySql(sql,new Parameter(country,skus));
		}else{
			sql="SELECT distinct sku,CONCAT(product_name,CASE WHEN color!='' THEN CONCAT ('_',color) ELSE '' END) product_name FROM psi_sku WHERE product_name <> 'Inateck other' AND product_name <> 'Inateck Old' AND product_name IS NOT NULL AND sku IS NOT NULL  AND del_flag='0' and sku in :p1 ";
			tempList=amazonProductDao.findBySql(sql,new Parameter(skus));
		}
		for (Object[] obj : tempList) {
				Map<String,String> map = Maps.newHashMap();
				map.put("key", obj[0].toString());
				String value = obj[0].toString();
				map.put("value", obj[1].toString()+"["+value+"]");
			    rs.add(map);
		}
		return rs;
	}
	
	public List<Map<String, String>> findSkuByAccount(String account) {
		//排除已经下帖的sku
		String sql = "";
		List<Object[]>  skus =null;
		if (StringUtils.isNotEmpty(account)){
			sql = "SELECT DISTINCT a.`sku` FROM amazoninfo_product2 a WHERE a.`active` = '1' and a.account_name = :p1 ";
			skus=amazonProductDao.findBySql(sql,new Parameter(account));
		}else{
			sql = "SELECT DISTINCT a.`sku` FROM amazoninfo_product2 a WHERE a.`active` = '1' ";
			skus=amazonProductDao.findBySql(sql);
		}
		List<Map<String, String>> rs = Lists.newArrayList();
		List<Object[]>  tempList =null;
		if (StringUtils.isNotEmpty(account)){
			sql="SELECT distinct sku,CONCAT(product_name,CASE WHEN color!='' THEN CONCAT ('_',color) ELSE '' END) product_name FROM psi_sku WHERE product_name <> 'Inateck other' AND product_name <> 'Inateck Old' AND product_name IS NOT NULL AND sku IS NOT NULL AND del_flag='0' and account_name=:p1 and sku in :p2";
			tempList=amazonProductDao.findBySql(sql,new Parameter(account,skus));
		}else{
			sql="SELECT distinct sku,CONCAT(product_name,CASE WHEN color!='' THEN CONCAT ('_',color) ELSE '' END) product_name FROM psi_sku WHERE product_name <> 'Inateck other' AND product_name <> 'Inateck Old' AND product_name IS NOT NULL AND sku IS NOT NULL  AND del_flag='0' and sku in :p1 ";
			tempList=amazonProductDao.findBySql(sql,new Parameter(skus));
		}
		for (Object[] obj : tempList) {
				Map<String,String> map = Maps.newHashMap();
				map.put("key", obj[0].toString());
				String value = obj[0].toString();
				map.put("value", obj[1].toString()+"["+value+"]");
			    rs.add(map);
		}
		return rs;
	}
	
	//修改图片专用,直接选择产品
	public List<Map<String, String>> findSkuForImage(List<String> countryList) {
		List<Map<String, String>> rs = Lists.newArrayList();
		//排除已经下帖的sku
		String sql = "SELECT DISTINCT a.`sku` FROM amazoninfo_product2 a WHERE a.`active` = '1' and a.`is_fba`='1' and a.country in :p1 ";
		List<Object[]>  skus = amazonProductDao.findBySql(sql, new Parameter(countryList));
		sql = "SELECT distinct CONCAT(product_name,CASE WHEN color!='' THEN CONCAT ('_',color) ELSE '' END) product_name FROM psi_sku WHERE product_name <> 'Inateck other' AND product_name <> 'Inateck Old' AND product_name IS NOT NULL AND sku IS NOT NULL AND del_flag='0' and country=:p1 and sku in :p2 GROUP BY product_name,color";
		List<Object> tempList = amazonProductDao.findBySql(sql, new Parameter(countryList.get(0), skus));
		for (Object obj : tempList) {
			Map<String,String> map = Maps.newHashMap();
			map.put("key", obj.toString());
			map.put("value", obj.toString());
		    rs.add(map);
		}
		return rs;
	}
	
	//修改产品图片专用
	public List<Map<String, String>> findSkuForEditImage(Set<String> accountName,Set<String> typeSet) {
		//排除已经下帖的和本地帖sku
		String sql = "";
		List<String>  skus =null;
		if (accountName!=null&&accountName.size()>0){
			sql = "SELECT DISTINCT CONCAT(account_name,'@',a.`sku`) AS sku FROM amazoninfo_product2 a WHERE a.`active` = '1'  and a.account_name in :p1 ";
			skus=amazonProductDao.findBySql(sql,new Parameter(accountName));
		}else{
			sql = "SELECT DISTINCT CONCAT(account_name,'@',a.`sku`) AS sku FROM amazoninfo_product2 a WHERE a.`active` = '1'  ";
			skus=amazonProductDao.findBySql(sql);
		}
		List<Map<String, String>> rs = Lists.newArrayList();
		List<Map<String, String>> list = Lists.newArrayList();
		List<Object[]>  tempList =null;
		if(typeSet!=null&&typeSet.size()>0){
			sql="SELECT distinct CONCAT(a.account_name,'@',sku) AS countrySku ,CONCAT(product_name,CASE WHEN a.color!='' THEN CONCAT ('_',a.color) ELSE '' END) product_name FROM  psi_sku AS a,psi_product AS b" +
					" WHERE a.`product_id`=b.id AND b.`TYPE` IN :p3 AND product_name <> 'Inateck other' AND product_name <> 'Inateck Old' AND product_name IS NOT NULL" +
					" AND sku IS NOT NULL AND a.del_flag='0' AND b.del_flag='0' and a.account_name in :p1 and CONCAT(a.account_name,'@',sku) in :p2";
			tempList=amazonProductDao.findBySql(sql,new Parameter(accountName,skus,typeSet));
		}else{
			sql="SELECT distinct CONCAT(account_name,'@',sku) AS countrySku,CONCAT(product_name,CASE WHEN color!='' THEN CONCAT ('_',color) ELSE '' END) product_name FROM  psi_sku " +
					" WHERE product_name <> 'Inateck other' AND product_name <> 'Inateck Old' AND product_name IS NOT NULL" +
					" AND sku IS NOT NULL AND del_flag='0' and account_name in :p1 and CONCAT(account_name,'@',sku) in :p2";
			tempList=amazonProductDao.findBySql(sql,new Parameter(accountName,skus));
		}
		Set<String> set = Sets.newHashSet();
		for (Object[] obj : tempList) {
			Map<String,String> map = Maps.newHashMap();
			String sku =obj[0].toString();
			map.put("key",sku );  //sku
			int i= sku.indexOf("@");
			String countryFlag = sku.substring(0, i);
			map.put("value", "<b>"+countryFlag+"</b>@"+obj[1].toString()+"["+sku.substring(i+1)+"]");// 名字[sku]
		    rs.add(map);
		    if (!set.contains(obj[1].toString())) {
		    	set.add(obj[1].toString());
		    	Map<String,String> map1 = Maps.newHashMap();
		    	map1.put("key", obj[1].toString());
		    	map1.put("value", obj[1].toString());
		    	list.add(map1);
			}
		}
		list.addAll(rs);
		return list;
	}
	
	//修改产品价格专用,支持产品快捷修改
	public List<Map<String, String>> findSkuForEditPrice(String accountName,Set<String> typeSet,Set<String> country_types) {
		//排除已经下帖的sku
		String sql = "";
		List<String>  skus =null;
		List<String> accountNames = Lists.newArrayList();
		if (StringUtils.isNotEmpty(accountName)){
			accountNames=Arrays.asList(accountName.split(","));
			sql = "SELECT DISTINCT CONCAT(a.account_name,'@',a.`sku`) AS sku FROM amazoninfo_product2 a WHERE a.`active` = '1' and a.account_name in :p1 ";
			skus=amazonProductDao.findBySql(sql,new Parameter(accountNames));
		}else{
			sql = "SELECT DISTINCT CONCAT(a.account_name,'@',a.`sku`) AS sku FROM amazoninfo_product2 a WHERE a.`active` = '1' ";
			skus=amazonProductDao.findBySql(sql);
		}
		List<Map<String, String>> rs = Lists.newArrayList();
		List<Map<String, String>> list = Lists.newArrayList();
		List<Object[]>  tempList =new ArrayList<Object[]>();
		if (StringUtils.isNotEmpty(accountName)){
			if(country_types!=null&&country_types.size()>0){
				sql="SELECT distinct CONCAT(account_name,'@',sku) AS countrySku ,CONCAT(product_name,CASE WHEN a.color!='' THEN CONCAT ('_',a.color) ELSE '' END) product_name FROM  psi_sku AS a,psi_product AS b" +
						" WHERE a.`product_id`=b.id  AND product_name <> 'Inateck other' AND product_name <> 'Inateck Old' AND product_name IS NOT NULL" +
						" AND sku IS NOT NULL AND a.del_flag='0' AND b.del_flag='0' and CONCAT(country,'_',b.`TYPE`) in :p2 and CONCAT(account_name,'@',sku) in :p1";
				tempList=amazonProductDao.findBySql(sql,new Parameter(skus,country_types));
			}else{
				sql="SELECT distinct CONCAT(account_name,'@',sku) AS countrySku,CONCAT(product_name,CASE WHEN color!='' THEN CONCAT ('_',color) ELSE '' END) product_name FROM  psi_sku " +
						" WHERE product_name <> 'Inateck other' AND product_name <> 'Inateck Old' AND product_name IS NOT NULL" +
						" AND sku IS NOT NULL AND del_flag='0' and account_name in :p1 and CONCAT(account_name ,'@',sku) in :p2";
				tempList=amazonProductDao.findBySql(sql,new Parameter(accountNames,skus));
			}
		}else{
			if(typeSet!=null&&typeSet.size()>0){
				sql="SELECT distinct CONCAT(account_name,'@',sku) AS countrySku,CONCAT(product_name,CASE WHEN a.color!='' THEN CONCAT ('_',a.color) ELSE '' END) product_name FROM  psi_sku AS a,psi_product AS b " +
						"WHERE a.`product_id`=b.id  AND b.`TYPE` IN :p2  AND product_name <> 'Inateck other' AND product_name <> 'Inateck Old' AND product_name IS NOT NULL AND sku IS NOT NULL " +
						" AND a.del_flag='0' AND b.del_flag='0' and CONCAT(account_name,'@',sku) in :p1 ";
				tempList=amazonProductDao.findBySql(sql,new Parameter(skus,typeSet));
			}else{
				sql="SELECT distinct CONCAT(account_name,'@',sku) AS countrySku,CONCAT(product_name,CASE WHEN color!='' THEN CONCAT ('_',color) ELSE '' END) product_name FROM psi_sku WHERE product_name <> 'Inateck other' AND product_name <> 'Inateck Old' AND product_name IS NOT NULL AND sku IS NOT NULL  AND del_flag='0' CONCAT(account_name,'@',sku) sku in :p1 ";
				tempList=amazonProductDao.findBySql(sql,new Parameter(skus));
			}
		
		}
		Set<String> set = Sets.newHashSet();
		for (Object[] obj : tempList) {
			Map<String,String> map = Maps.newHashMap();
			String sku =obj[0].toString();
			map.put("key",sku );  //sku
			int i= sku.indexOf("@");
			String countryFlag = sku.substring(0, i);
			map.put("value", "<b>"+countryFlag+"</b>@"+obj[1].toString()+"["+sku.substring(i+1)+"]");// 名字[sku]
		    rs.add(map);
		    if (!set.contains(obj[1].toString())) {
		    	set.add(obj[1].toString());
		    	Map<String,String> map1 = Maps.newHashMap();
		    	map1.put("key", obj[1].toString());
		    	map1.put("value", obj[1].toString());
		    	list.add(map1);
			}
		}
		list.addAll(rs);
		return list;
	}
	
	public String findProductName(String asin,String country){
		country = country.toLowerCase();
		Set<String> countrys = Sets.newHashSet(country);
		if("fr,uk,es,it,de".contains(country)){
			countrys.add("de");
			countrys.add("fr");
			countrys.add("es");
			countrys.add("it");
			countrys.add("uk");
		}
		String sql="SELECT DISTINCT CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN CONCAT ('_',s.`color`) ELSE '' END) product_name  FROM psi_sku s WHERE s.`del_flag`='0' AND s.`asin`=:p1 and s.`product_name` not like '%Old%' AND s.`country` in :p2";
		List<String> tempList=amazonProductDao.findBySql(sql,new Parameter(asin,countrys));
		if(tempList.size()>0){
			return tempList.get(0);
		}
		return null;
	}
	
	/**
	 *获得产品名和asin关系 
	 *key:country,asin
	 *value:productName
	 */
	public Map<String,String> getProductNameAsin(){
		String sql="SELECT DISTINCT CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN CONCAT ('_',s.`color`) ELSE '' END) product_name ,s.`asin`,s.`country` FROM psi_sku s WHERE s.`del_flag`='0' AND s.`product_name` NOT LIKE '%Old%' ";
		List<Object[]> objs=amazonProductDao.findBySql(sql);
		Map<String,String> rsMap = Maps.newHashMap();
		for(Object[] obj: objs){
			String  key=obj[1]+","+obj[2];
			rsMap.put(key, obj[0].toString());
		}
		return rsMap;	
	}
	
	
	public Map<String, String> findProductNameMap() {
		/*String sql="select distinct p.ASIN,CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN CONCAT ('_',s.`color`) ELSE '' END) product_name from amazoninfo_product2 p " +
        " left join (SELECT DISTINCT product_name,country,ASIN,color FROM psi_sku WHERE `product_name` NOT LIKE '%other%' AND del_flag='0' ) s " +
        " on s.asin=p.asin and p.country=s.country "+
		" where p.active='1' and product_name IS NOT NULL GROUP BY ASIN,product_name ";*/
		String sql="SELECT distinct ASIN,CONCAT(product_name,CASE WHEN color!='' THEN CONCAT ('_',color) ELSE '' END) FROM psi_sku WHERE product_name NOT LIKE '%other%' AND product_name NOT LIKE '%Old%' AND product_name IS NOT NULL AND ASIN IS NOT NULL AND del_flag='0' GROUP BY ASIN,product_name";
		@SuppressWarnings("unchecked")
		List<Object[]> list = amazonProductDao.createSqlQuery(sql, null).list();
		Map<String, String> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(), objects[1].toString());
		}
		return rs;
	}
	
	public Map<String,Map<String,String>> findPostAsin() {
		String sql="SELECT  a.`account_name`,a.`asin`,GROUP_CONCAT(DISTINCT  a.`sku` ORDER BY a.`is_fba` DESC  ) FROM amazoninfo_product2 a WHERE a.`active` = '1' AND a.`asin` IS NOT NULL and account_name is not null GROUP BY a.`account_name`,a.`asin` ORDER BY account_name";
		List<Object[]> list = amazonProductDao.findBySql(sql);
		Map<String,Map<String,String>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString();
			Map<String,String> asins = rs.get(country);
			if(asins==null){
				asins =Maps.newHashMap();
				rs.put(country, asins);
			}
			asins.put(objs[1].toString(),objs[2].toString());
		}
		return rs;
	}
	
	public List<Object> noEanPost(String country){
		String sql = "SELECT DISTINCT a.`asin` FROM amazoninfo_product2 a WHERE (a.`ean` IS NULL OR a.`ean` = '') AND a.`active`='1' AND a.`country` =:p1";
		return amazonProductDao.findBySql(sql,new Parameter(country));
	}
	
	@Transactional(readOnly = false)
	public void updateEan(String country,Map<String,String> asinMap){
		String sql = "UPDATE amazoninfo_product2 a SET a.`ean`=:p1 WHERE a.`asin` = :p2 AND a.`country`=:p3";
		for(Map.Entry<String,String> entry :asinMap.entrySet()){
		     String asin=entry.getKey();
			 amazonProductDao.updateBySql(sql,new Parameter(entry.getValue(),asin,country));
		}
	}
	
	
	//[产品 asins]
	public Map<String,List<String>> getProductsAsinMap(String country){
		String sql = "SELECT DISTINCT a.`asin`,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) AS NAME FROM psi_sku a WHERE  a.`asin` !='' AND a.`country` =:p1 AND a.`del_flag`='0'";
		List<Object> list = amazonProductDao.findBySql(sql,new Parameter(country));
		Map<String,List<String>> rs = Maps.newHashMap();
		for (Object object : list) {
			Object[] objs = (Object[])object;
			String pN = objs[1].toString();
			String sku = objs[0].toString();
			List<String> asins = rs.get(pN);
			if(asins==null){
				asins = Lists.newArrayList();
				rs.put(pN, asins);
			}
			asins.add(sku);
		}
		return rs;
	}
	
	public List<String> findAllProductName(){
		String sql = "SELECT DISTINCT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) AS NAME FROM psi_sku a WHERE a.`del_flag`='0' and a.product_name NOT LIKE '%other%' AND a.product_name NOT LIKE '%Old%' ";
		List<String> list = amazonProductDao.findBySql(sql);
		return  list;
	}
	
	@Transactional(readOnly = false)
	public void save(AmazonProduct amazonProduct) {
		amazonProductDao.save(amazonProduct);
	}
	
	@Transactional(readOnly = false)
	public void save(List<AmazonProduct> amazonProducts) {
		amazonProductDao.update("update  AmazonProduct set parentProduct = null,active = '0' ");
		amazonProductDao.save(amazonProducts);
		amazonProductDao.deleteNullParent();
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		amazonProductDao.deleteById(id);
	}
	
	public PsiSku getProductBySku(String sku,String country){
		String  sql="SELECT a.`product_name`,a.`color` FROM psi_sku a WHERE a.`del_flag`='0' and sku=:p1 and country=:p2 and a.product_name NOT LIKE '%other%' AND a.product_name NOT LIKE '%Old%'";
		List<Object[]> rs = amazonProductDao.findBySql(sql,new Parameter(sku,country));
		if(rs.size()>0){
			Object[] obj=rs.get(0);
			PsiSku psiSku=new PsiSku();
			psiSku.setProductName(obj[0].toString());
			psiSku.setColor(obj[1]==null?"":obj[1].toString());
			return psiSku;
		}else{
			return null;
		}
	}
	
	public static void main(String arr[]){
		String sku="com@skulll";
		
	}
}
