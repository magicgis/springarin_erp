package com.springrain.erp.modules.psi.service;

import java.util.Collection;
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
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.PsiProductPartsDao;
import com.springrain.erp.modules.psi.entity.ProductParts;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.parts.PsiParts;

@Component
@Transactional(readOnly = true)
public class PsiProductPartsService extends BaseService{
	@Autowired
	private PsiProductPartsDao productPartsDao;
	
	public Map<String,List<ProductParts>> getProPartsMap(ProductParts pParts){
		Map<String,List<ProductParts>> proPartsMap = Maps.newHashMap(); //key:产品_颜色
		DetachedCriteria dc = productPartsDao.createDetachedCriteria();
		List<ProductParts> productParts= this.productPartsDao.find(dc);
		
		for(ProductParts proParts:productParts){
			String nameColor =proParts.getProduct().getModel();
			if(StringUtils.isNotEmpty(proParts.getColor())){
				nameColor+="_"+proParts.getColor();
			}
			List<ProductParts> list=null;
			if(proPartsMap.get(nameColor)==null){
				list = Lists.newArrayList();
			}else{
				list=proPartsMap.get(nameColor);
			}
			list.add(proParts);
			proPartsMap.put(nameColor, list);
		}
		return proPartsMap;
	}
	
	
	public List<ProductParts> getProductParts(Integer productId,String color){
		DetachedCriteria dc = productPartsDao.createDetachedCriteria();
		dc.add(Restrictions.eq("product.id", productId));
		dc.add(Restrictions.eq("color",color));
		return this.productPartsDao.find(dc);
	}
	
	public List<Integer> getPartsIdsBySql(Integer productId,String color){
		String sql ="SELECT a.`parts_id` FROM psi_product_parts AS a WHERE a.`product_id`=:p1 AND a.`color`=:p2";
		return this.productPartsDao.findBySql(sql,new Parameter(productId,color));
	}
	
	/***
	 * 及时查询产品+颜色，是否有配件（这个方法很重要）
	 * 
	 */
	public Set<String> getPartsColors(Set<String> proColor){
		String sql ="";
		List<String> list=null;
		if(proColor!=null&&proColor.size()>0){
			sql="SELECT CONCAT_WS(',',a.`product_id`,a.`color`) FROM psi_product_parts AS a WHERE CONCAT_WS(',',a.`product_id`,a.`color`) in :p1 GROUP BY a.`product_id`,a.`color`";
			list =this.productPartsDao.findBySql(sql,new Parameter(proColor));
		}else{
			sql="SELECT CONCAT_WS(',',a.`product_id`,a.`color`) FROM psi_product_parts AS a  GROUP BY a.`product_id`,a.`color`";
			list =this.productPartsDao.findBySql(sql);
		}
		Set<String> set = Sets.newHashSet();
		if(list!=null&&list.size()>0){
			set.addAll(list);
		}
		return set;
	}
	 
	
	public String getSelectedPartsData(Integer productId,String color){
		DetachedCriteria dc = productPartsDao.createDetachedCriteria();
		dc.add(Restrictions.eq("product.id", productId));
		dc.add(Restrictions.eq("color",color));
		List<ProductParts> productParts= this.productPartsDao.find(dc);
		StringBuilder res=new StringBuilder("[");
		if(productParts.size()>0){
			for(ProductParts proParts:productParts){
				res.append("{\"partsId\":\"").append(proParts.getParts().getId()).append("\"},");
			}
			res=new StringBuilder(res.substring(0, res.length()-1)).append("]");
		}else{
			res.append("]");
		}
		return res.toString();
	}
	
	/***
	 *根据产品id   颜色     partsIds配置关系
	 * 
	 */
	@Transactional(readOnly = false)
	public  void  editProductPartsRelivate(Integer productId,String color ,String parts[]){
		//根据产品id  颜色    查出目前的关系，
		List<ProductParts> productParts= this.getProductParts(productId, color);
		Map<Integer,Integer>  partsMap =Maps.newHashMap();    //key：partsId,value:productPartsId
		if(productParts!=null&&productParts.size()>0){
			for(ProductParts pparts:productParts){
				if(pparts!=null){
					partsMap.put(pparts.getParts().getId(), pparts.getId());
				}
			}
		}
		Set<Integer> curPartsIdSet = Sets.newHashSet();
		if(parts.length==0){
			this.deleteComInfos(partsMap.values());
		}else{  
			if(productParts!=null&&productParts.size()>0){
				//遍历现在的，老的里面不存在就是新增的
				for(String partsId:parts){
					if(!partsMap.containsKey(Integer.parseInt(partsId))){
						//原来不存在就新增
						this.productPartsDao.save(new ProductParts(new PsiProduct(productId), new PsiParts(Integer.parseInt(partsId)), color,null));
					}
					curPartsIdSet.add(Integer.parseInt(partsId));
				}
				//遍历原来的，现在里面不存在就要删除
				for(Map.Entry<Integer,Integer> entry:partsMap.entrySet()){
					Integer partsId = entry.getKey();
					if(!curPartsIdSet.contains(partsId)){
						this.deleteComInfo(entry.getValue());
					}
				}
			}else{
				//全部是新增的
				for(String partsId:parts){
					this.productPartsDao.save(new ProductParts(new PsiProduct(productId), new PsiParts(Integer.parseInt(partsId)), color,null));
				}
			}
		}
	}
	

	public void deleteComInfo(Integer id){
		String sql ="DELETE FROM psi_product_parts  WHERE id=:p1";
		this.productPartsDao.updateBySql(sql, new Parameter(id));
	}
	
	
	public void deleteComInfos(Collection<Integer> ids){
		String sql ="DELETE FROM psi_product_parts  WHERE id in :p1";
		this.productPartsDao.updateBySql(sql, new Parameter(ids));
	}
	
	public void deleteByPartsId(Integer partsId){
		String sql ="DELETE FROM psi_product_parts  WHERE parts_id=:p1";
		this.productPartsDao.updateBySql(sql, new Parameter(partsId));
	}

	@Transactional(readOnly = false)
	public String updateMixtrueRatio(Integer proPartsId,Integer ratio){
		String sql ="update  psi_product_parts set mixture_ratio=:p1 where id=:p2";
		int i =this.productPartsDao.updateBySql(sql, new Parameter(ratio,proPartsId));
		if(i>0){
			return "true";
		}else{
			return "false";
		}
	}
	
	public void deleteByProductId(Integer productId){
		String sql ="DELETE FROM psi_product_parts  WHERE product_id=:p1";
		this.productPartsDao.updateBySql(sql, new Parameter(productId));
	}
}
