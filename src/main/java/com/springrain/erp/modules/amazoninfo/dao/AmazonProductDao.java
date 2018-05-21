package com.springrain.erp.modules.amazoninfo.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProduct;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.utils.DictUtils;

/**
 * 亚马逊产品DAO接口
 * @author tim
 * @version 2014-06-04
 */
@Repository
public class AmazonProductDao extends BaseDao<AmazonProduct> {
	
	public void save(List<AmazonProduct> entityList){
		Collection<String> countrys = Collections2.transform(DictUtils.getDictList("platform"), new Function<Dict,String>() {
			public String apply(Dict input) {
				return input.getValue();
			};
		});
		for (AmazonProduct entity : entityList){
			String country = entity.getCountry().toLowerCase();
			if(country.equalsIgnoreCase("Unitek_US")){
				country = "com.unitek";
			}else if(country.equalsIgnoreCase("US")){
				country = "com";
			}
			String asin = entity.getAsin();
			entity.setCountry(country);
			if(countrys.contains(country)){
				List<AmazonProduct> product = find(createDetachedCriteria(Restrictions.eq("country", country),Restrictions.eq("asin", asin)));
				if(product.size() == 0){
					if(entity.getParentProduct()!=null){
						List<AmazonProduct> parents = find(createDetachedCriteria(Restrictions.eq("country", country),Restrictions.eq("asin", entity.getParentProduct().getAsin())));
						if(parents.size() ==1){
							entity.setParentProduct(parents.get(0));
						}
					}
					entity.setActive("1");
					save(entity);
				}else{
					AmazonProduct product2 = product.get(0);
					product2.setSku(entity.getSku());
					if(entity.getEan().length()>0){
						product2.setEan(entity.getEan());
					}
					product2.setName(entity.getName());
					if(entity.getParentProduct()!=null){
						List<AmazonProduct> parents = find(createDetachedCriteria(Restrictions.eq("country", country),Restrictions.eq("asin", entity.getParentProduct().getAsin())));
						if(parents.size() ==1){
							product2.setParentProduct(parents.get(0));
						}
					}
					product2.setActive("1");
					save(product2);
				}
			}
		}
	}
	
	public List<String> findAllProductName(){
		List<String> rs = Lists.newArrayList();
		List<Object> objs = findBySql("SELECT DISTINCT NAME FROM amazoninfo_product where country !='com.unitek'");
		for (Object object : objs) {
			if(object.toString().length()>0)
				rs.add(object.toString());
		}
		return rs;
	}
	
	public void deleteNullParent(){
		List<String> rs = Lists.newArrayList();
		List<Object> objs = findBySql("SELECT parent  FROM amazoninfo_product  WHERE NOT(ISNULL(parent))");
		for (Object object : objs) {
			rs.add(object.toString());
		}
		createSqlQuery("delete from amazoninfo_product where name = '' and id not in(:p1)",new Parameter(rs)).executeUpdate();
	}
}
