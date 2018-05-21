package com.springrain.erp.modules.psi.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.dao.PsiProductAttributeDao;
import com.springrain.erp.modules.psi.dao.PsiProductDao;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductAttribute;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;

@Component
@Transactional(readOnly = true)
public class PsiProductAttributeService extends BaseService{

	@Autowired
	private PsiProductAttributeDao        psiProductAttributeDao;
	
	@Autowired
	private PsiProductDao productDao;
	
	@Transactional(readOnly = false)
	public void save(PsiProductAttribute psiProductAttribute) {
		psiProductAttributeDao.save(psiProductAttribute);
	}
	
	@Transactional(readOnly = false)
	public void save(List<PsiProductAttribute> psiProductAttributes) {
		psiProductAttributeDao.save(psiProductAttributes);
	}
	
	@Transactional(readOnly = false)
	public void update(Float price,String type,String productName) {
		String sql="UPDATE `psi_product_attribute` AS a SET moq_price=:p1,currency_type=:p2 WHERE CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END)=:p3 ";
		psiProductAttributeDao.updateBySql(sql,new Parameter(price,type,productName));
	}
	
	@Transactional(readOnly = false)
	public void update(PsiProductAttribute psiProductAttribute, String flag) {
		if (StringUtils.isEmpty(flag) || !"3".equals(flag)) {
			psiProductAttributeDao.save(psiProductAttribute);
		}
	}
	
	
	
	public PsiProductAttribute get(Integer id){
		return psiProductAttributeDao.get(id);
	}
	
	public List<PsiProductAttribute> findAll(){
		DetachedCriteria dc = psiProductAttributeDao.createDetachedCriteria();
		dc.add(Restrictions.ne("productName", "Inateck Old"));
		dc.add(Restrictions.ne("productName", "Inateck other"));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiProductAttributeDao.find(dc);
	}
	
	public Integer getMaxInventoryByName(String nameWithColor){
		String sql="select a.quantity from psi_product_attribute a where CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END)=:p1 AND a.del_flag='0' ";
		List<Integer> list=psiProductAttributeDao.findBySql(sql,new Parameter(nameWithColor));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	
	public Map<String,Integer> getAllMaxInventory(){
		String sql="select CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END),a.quantity from psi_product_attribute a WHERE a.`del_flag`='0' AND a.`quantity` IS NOT NULL";
		List<Object[]> list=psiProductAttributeDao.findBySql(sql);
		Map<String,Integer> map=Maps.newHashMap();
		for (Object[] obj : list) {
			map.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
		} 
		return map;
	}
	
	public Map<String,Map<String,Object[]>> getMaxInventory(){
		String sql="select product_name,(case when color='' then 'noColor' else color end ),quantity,id from psi_product_attribute a WHERE a.`del_flag`='0' AND a.`quantity` IS NOT NULL";
		List<Object[]> list=psiProductAttributeDao.findBySql(sql);
		Map<String,Map<String,Object[]>> map=Maps.newHashMap();
		for (Object[] obj : list) {
			Map<String,Object[]> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(),temp);
			}
			temp.put(obj[1].toString(),obj);
		}
		return map;
	}
	
	public List<String> getColorByName(String productName){
		List<String> list=Lists.newArrayList();
		String sql=" select color from  psi_product_attribute where product_name=:p1 AND del_flag='0' ";
		list=psiProductAttributeDao.findBySql(sql,new Parameter(productName));
		return list;
	}
	
	@Transactional(readOnly = false)
	public void deleteColor(String productName){
		String sql="DELETE  FROM  psi_product_attribute WHERE product_name=:p1 AND color!='' ";
		psiProductAttributeDao.updateBySql(sql, new Parameter(productName));
	}
	
	@Transactional(readOnly = false)
	public void deleteNoColor(String productName,String color){
		String sql="DELETE  FROM  psi_product_attribute WHERE product_name=:p1 AND color=:p2 ";
		psiProductAttributeDao.updateBySql(sql, new Parameter(productName,color));
	}
	
	public PsiProductAttribute get(String name,String color) {
		DetachedCriteria dc = this.psiProductAttributeDao.createDetachedCriteria();
		dc.add(Restrictions.eq("productName", name));
		dc.add(Restrictions.eq("color", color));
		//dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiProductAttribute> rs = psiProductAttributeDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	public PsiProductAttribute get(String nameWithColor) {
		DetachedCriteria dc = this.psiProductAttributeDao.createDetachedCriteria();
		if(StringUtils.isBlank(nameWithColor)){
			return null;
		}
		if(nameWithColor.contains("_")){
			dc.add(Restrictions.eq("productName",nameWithColor.substring(0,nameWithColor.lastIndexOf("_"))));
			dc.add(Restrictions.eq("color", nameWithColor.substring(nameWithColor.lastIndexOf("_")+1)));
		}else{
			dc.add(Restrictions.eq("productName",nameWithColor));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiProductAttribute> rs = psiProductAttributeDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	

	//更新产品分颜色属性 
	@Transactional(readOnly = false)
	public void updateProductAttr(PsiProduct product) {
		String productName = product.getName();
		
		//先把该产品的记录更新为删除状态
		String delSql = "UPDATE `psi_product_attribute` t SET t.`del_flag`='1' WHERE t.`product_name`=:p1";
		psiProductAttributeDao.updateBySql(delSql, new Parameter(product.getName()));
		
		//按照修改后的产品参数（平台和颜色）更新记录（新插入或更新删除状态为未删除）
		String[] newColors = product.getColor().split(",");
		for (String color : newColors) {
			String sql = "INSERT INTO `psi_product_attribute`(product_id,product_name,color,del_flag)"+
					" VALUES('"+product.getId()+"','"+productName+"','"+color+"','0') "+
					" ON DUPLICATE KEY UPDATE del_flag=VALUES(del_flag)";
			psiProductAttributeDao.updateBySql(sql, null);
		}
	}

	/*
	 * map[产品名称_颜色 [国家  运输方式]] 欧洲以德国为准
	 */
	public Map<String, Map<String, Integer>> findtransportType() {
		Map<String, Map<String, Integer>> rs = Maps.newHashMap();
		String sql ="SELECT t.`product_name`,t.`color`,t.`country`,t.`transport_type` FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' ";
		List<Object[]> list = psiProductAttributeDao.findBySql(sql);
		for (Object[] obj : list) {
			String name = obj[0].toString();
			String color = obj[1]==null?"":obj[1].toString();
			if (StringUtils.isNotEmpty(color)) {
				name = name + "_" + color;
			}
			String country = obj[2].toString();
			//默认没填的为空运
			Integer transportType = obj[3]==null?2:Integer.parseInt(obj[3].toString());
			Map<String, Integer> productMap = rs.get(name);
			if (productMap == null) {
				productMap = Maps.newHashMap();
				rs.put(name, productMap);
			}
			productMap.put(country, transportType);
			//欧洲
			if ("de,fr,uk,it,es".contains(country)) {
				//部分特殊产品只有UK
				if (productMap.get("eu")==null && "uk".equals(country)) {
					productMap.put("eu", transportType);
				}
				if ("de".equals(country)) {
					productMap.put("eu", transportType);
				}
			}
			if (productMap.get("total") == null) {
				productMap.put("total", transportType);
			} else if (productMap.get("total")!=transportType && productMap.get("total")<3) {
				productMap.put("total", productMap.get("total") + transportType);
			}
		}
		return rs;
	}
	
	//更新产品采购周属性
	@Transactional(readOnly = false)
	public int updatePurchaseWeek(){
		String sql ="UPDATE `psi_product_attribute` t SET t.`purchase_week`=CASE WHEN t.`purchase_week`-1=-1 THEN 3 ELSE t.`purchase_week`-1 END WHERE t.`purchase_week` IS NOT NULL";
		return psiProductAttributeDao.updateBySql(sql, null);
	}
	
	//查询当前周需要下单的产品
	public List<String> findOrderProducts(){
		String sql ="SELECT CONCAT(t.`product_name`,CASE WHEN t.`color`='' THEN '' ELSE CONCAT('_', t.`color`) END) FROM `psi_product_attribute` t WHERE t.`purchase_week`=0 AND t.`del_flag`='0'";
		return psiProductAttributeDao.findBySql(sql);
	}
	
	//查询采购周
	public Map<String, Integer> findPurchaseWeekMap(){
		Map<String, Integer> rs = Maps.newHashMap();
		String sql ="SELECT CONCAT(t.`product_name`,CASE WHEN t.`color`='' THEN '' ELSE CONCAT('_', t.`color`) END),t.`purchase_week` "+
				" FROM `psi_product_attribute` t WHERE t.`purchase_week` IS NOT NULL AND t.`del_flag`='0'";
		List<Object[]> list = psiProductAttributeDao.findBySql(sql);
		for (Object[] obj : list) {
			rs.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
		}
		return rs;
	}

	//不分颜色组装产品运输方式,map[产品名称, transportType] transportType： 1 海运  2空运 3 空、海运
	public Map<String, Integer> findProductTransportType(){
		Map<String, Integer> rs = Maps.newHashMap();
		String sql ="SELECT t.`product_name`,t.`transport_type` FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' AND t.`transport_type` IS NOT NULL GROUP BY t.`product_name`,t.`transport_type`";
		List<Object[]> list = psiProductAttributeDao.findBySql(sql);
		for (Object[] obj : list) {
			String name = obj[0].toString();
			Integer transportType = Integer.parseInt(obj[1].toString());
			if (rs.get(name) != null) {
				transportType = transportType + rs.get(name);
			}
			rs.put(name, transportType);
		}
		return rs;
	}
	
	public Map<String,Map<String,Object>> findBeforePrice(){
		Map<String,Map<String,Object>> map=Maps.newHashMap();
		String sql="SELECT CONCAT(a.product_name,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) NAME, "+
                   " a.`currency_type`,TRUNCATE( CASE WHEN a.`currency_type`='CNY' THEN  a.moq_price/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE  a.moq_price END ,2)  AS realprice FROM psi_product_attribute a WHERE a.`del_flag`='0' AND currency_type IS NOT NULL AND moq_price IS NOT NULL  ";
		List<Object[]> list =psiProductAttributeDao.findBySql(sql);
		for (Object[] obj: list) {
			Map<String,Object> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(),temp);
			}
			temp.put("type",obj[1].toString());
			temp.put("price",Float.parseFloat(obj[2].toString()));
		}
		return map;
	}
	
	public Map<String,Map<String,Object>> findProductPrice(){
		Map<String,Map<String,Object>> map=Maps.newHashMap();
		String sql="SELECT CONCAT(a.product_name,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) NAME, "+
                   " a.`currency_type`,ROUND( CASE WHEN a.`currency_type`='CNY' THEN  a.moq_price/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE  a.moq_price END ,2)  AS realprice FROM psi_product_attribute a WHERE a.`del_flag`='0' ";
		List<Object[]> list =psiProductAttributeDao.findBySql(sql);
		for (Object[] obj: list) {
			Map<String,Object> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(),temp);
			}
			temp.put("type",obj[1]==null?null:obj[1].toString());
			temp.put("price",obj[2]==null?null:Float.parseFloat(obj[2].toString()));
		}
		return map;
	}
	
	
	public Map<String,Map<String,Object>> findProductPrice2(){
		Map<String,Map<String,Object>> map=Maps.newHashMap();
		String sql="SELECT CONCAT(a.product_name,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) NAME, "+
                   " a.`currency_type`,a.moq_price FROM psi_product_attribute a WHERE a.`del_flag`='0' ";
		List<Object[]> list =psiProductAttributeDao.findBySql(sql);
		for (Object[] obj: list) {
			Map<String,Object> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(),temp);
			}
			temp.put("type",obj[1]==null?null:obj[1].toString());
			temp.put("price",obj[2]==null?null:Float.parseFloat(obj[2].toString()));
		}
		return map;
	}
	
	public Map<String,Float> findTaxPrice(String country,Set<String> nameSet){
		Map<String,Float> map=Maps.newHashMap();
		if (nameSet == null || nameSet.size() == 0) {
			return map;
		}
		String sql="SELECT CONCAT(a.product_name,CASE WHEN a.`color`!='' THEN CONCAT('_',a.`color`) ELSE '' END) NAME, "+
                " TRUNCATE( CASE WHEN a.`currency_type`='CNY' THEN  a.moq_price/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE  a.moq_price END ,2)  AS realprice FROM psi_product_attribute a "+
				" WHERE a.`del_flag`='0' AND currency_type IS NOT NULL AND moq_price IS NOT NULL HAVING NAME IN :p1 ";
		List<Object[]> list =psiProductAttributeDao.findBySql(sql,new Parameter(nameSet));
		for (Object[] obj: list) {
			Float price=Float.parseFloat(obj[1].toString());
			if(StringUtils.isNotBlank(country)){
				if("de,fr,it,es".contains(country)){
					map.put(obj[0].toString(),price/AmazonProduct2Service.getRateConfig().get("EUR/USD"));
				}else if("ca".equals(country)){
					map.put(obj[0].toString(),price/AmazonProduct2Service.getRateConfig().get("CAD/USD"));
				}else if("uk".equals(country)){
					map.put(obj[0].toString(),price/AmazonProduct2Service.getRateConfig().get("GBP/USD"));
				}else if("jp".equals(country)){
					map.put(obj[0].toString(),price*AmazonProduct2Service.getRateConfig().get("USD/JPY"));
				}else if("mx".equals(country)){
					map.put(obj[0].toString(),price/AmazonProduct2Service.getRateConfig().get("MXN/USD"));
				}else{
					map.put(obj[0].toString(),price);
				}
			}else{
				map.put(obj[0].toString(),price);
			}
			
		}
		return map;
	}
	
	
	public Map<String,Map<String,Integer>> findPurchaseWeek(){
		Map<String,Map<String,Integer>> map=Maps.newHashMap();
		Date date=new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMM");
		String startMonth=dateFormat.format(DateUtils.addMonths(date,-1));//0
		String endMonth=dateFormat.format(date);//0
		String sql="SELECT t.name,t.`purchase_date`,a.`purchase_week`,t.order_no FROM "+
				" (SELECT DISTINCT o.`order_no`,o.`purchase_date`, CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT('_',t.`color_code`) ELSE '' END) NAME "+
				"  FROM lc_psi_purchase_order o  "+
				"  JOIN lc_psi_purchase_order_item t ON o.`id`=t.`purchase_order_id` "+
				"  WHERE o.`order_sta` NOT IN ('0','6') GROUP BY o.`order_no`) t "+
				"  JOIN  "+
				"  ( "+
				"  SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT('_',a.`color`) ELSE '' END) NAME,a.`purchase_week`  "+
				"  FROM psi_product_attribute a "+
				"  WHERE a.`del_flag`='0' AND a.`product_name` NOT LIKE 'other' AND a.`product_name` NOT LIKE 'old') a ON t.name=a.name "+
				" WHERE t.`purchase_date`>=:p1 ";
		List<Object[]> list =psiProductAttributeDao.findBySql(sql,new Parameter(DateUtils.addMonths(date,-3)));
		for (Object[] obj: list) {
			String name=obj[0].toString();
			Date purchaseDate=(Date)obj[1];
			String orderId=obj[3].toString();
			Map<String,Integer> temp=map.get(name);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(name, temp);
			}
			if(obj[2]==null){
				if(dateFormat.format(purchaseDate).equals(startMonth)){
					Integer count=(temp.get("0")==null?0:temp.get("0"));
					temp.put("0", count+1);
				}else if(dateFormat.format(purchaseDate).equals(endMonth)){
					Integer count=(temp.get("1")==null?0:temp.get("1"));
					temp.put("1", count+1);
				}
			}else{
				String purchaseWeek=obj[2].toString();
				Date afterDate=null;
				Date beforeDate=null;
				if("0".equals(purchaseWeek)){
					afterDate=DateUtils.addDays(date,-28);
					beforeDate=DateUtils.addDays(date,-56);
				}else if("1".equals(purchaseWeek)){
					afterDate=DateUtils.addDays(date,-21);
					beforeDate=DateUtils.addDays(afterDate,-28);
				}else if("2".equals(purchaseWeek)){
					afterDate=DateUtils.addDays(date,-14);
					beforeDate=DateUtils.addDays(afterDate,-28);
				}else if("3".equals(purchaseWeek)){
					afterDate=DateUtils.addDays(date,-7);
					beforeDate=DateUtils.addDays(afterDate,-28);
				}
				if(beforeDate!=null&&afterDate!=null&&(purchaseDate.after(beforeDate)||purchaseDate.equals(beforeDate))){
					if(purchaseDate.before(afterDate)){
						Integer count=(temp.get("0")==null?0:temp.get("0"));
						temp.put("0", count+1);
					}else{
						Integer count=(temp.get("1")==null?0:temp.get("1"));
						temp.put("1", count+1);
					}
				}
			}
		}
		return map;
	}
	
	public Map<String,Map<String,String>> findPurchaseWeek2(){
		Map<String,Map<String,String>> map=Maps.newHashMap();
		Date date=new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMM");
		String startMonth=dateFormat.format(DateUtils.addMonths(date,-1));//0
		String endMonth=dateFormat.format(date);//0
		String sql="SELECT t.name,t.`purchase_date`,a.`purchase_week`,t.order_no FROM "+
				" (SELECT DISTINCT o.`order_no`,o.`purchase_date`, CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT('_',t.`color_code`) ELSE '' END) NAME "+
				"  FROM lc_psi_purchase_order o  "+
				"  JOIN lc_psi_purchase_order_item t ON o.`id`=t.`purchase_order_id` and t.del_flag='0' "+
				"  WHERE o.`order_sta` NOT IN ('0','6') GROUP BY o.`order_no`) t "+
				"  JOIN  "+
				"  ( "+
				"  SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT('_',a.`color`) ELSE '' END) NAME,a.`purchase_week`  "+
				"  FROM psi_product_attribute a "+
				"  WHERE a.`del_flag`='0' AND a.`product_name` NOT LIKE 'other' AND a.`product_name` NOT LIKE 'old') a ON t.name=a.name "+
				" WHERE t.`purchase_date`>=:p1 ";
		List<Object[]> list =psiProductAttributeDao.findBySql(sql,new Parameter(DateUtils.addMonths(date,-3)));
		for (Object[] obj: list) {
			String name=obj[0].toString();
			Date purchaseDate=(Date)obj[1];
			String orderId=obj[3].toString();
			Map<String,String> temp=map.get(name);
			String link=("<a target='_blank' href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPurchaseOrder/view?orderNo="+orderId+"'>"+orderId+"</a>");
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(name, temp);
			}
			if(obj[2]==null){
				if(dateFormat.format(purchaseDate).equals(startMonth)){
					temp.put("0",(temp.get("0")==null?"":temp.get("0")+",")+link);
				}else if(dateFormat.format(purchaseDate).equals(endMonth)){
					temp.put("1",(temp.get("1")==null?"":temp.get("1")+",")+link);
				}
			}else{
				String purchaseWeek=obj[2].toString();
				Date afterDate=null;
				Date beforeDate=null;
				if("0".equals(purchaseWeek)){
					afterDate=DateUtils.addDays(date,-28);
					beforeDate=DateUtils.addDays(date,-56);
				}else if("1".equals(purchaseWeek)){
					afterDate=DateUtils.addDays(date,-21);
					beforeDate=DateUtils.addDays(afterDate,-28);
				}else if("2".equals(purchaseWeek)){
					afterDate=DateUtils.addDays(date,-14);
					beforeDate=DateUtils.addDays(afterDate,-28);
				}else if("3".equals(purchaseWeek)){
					afterDate=DateUtils.addDays(date,-7);
					beforeDate=DateUtils.addDays(afterDate,-28);
				}
				if(beforeDate!=null&&afterDate!=null&&(purchaseDate.after(beforeDate)||purchaseDate.equals(beforeDate))){
					if(purchaseDate.before(afterDate)){
						temp.put("0",(temp.get("0")==null?"":temp.get("0")+",")+link);
					}else{
						temp.put("1",(temp.get("1")==null?"":temp.get("1")+",")+link);
					}
				}
			}
		}
		return map;
	}
	
	
	//country 采购周   产品名   采购单号
	public Map<String,Map<String,Map<String,String>>> findPurchaseWeek3(int week){
		Map<String,Map<String,Map<String,String>>> map=Maps.newHashMap();
		Date date=new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMM");
	    Map<String,String> monthMap=Maps.newHashMap();
		for (int i=0;i<=week;i++) {
			Date tempDate=DateUtils.addMonths(date, -i);
			monthMap.put(dateFormat.format(tempDate),i+"");
		}
		String sql="SELECT t.name,t.`purchase_date`,a.`purchase_week`,t.order_no,t.country_code  FROM "+
				" (SELECT DISTINCT o.`order_no`,o.`purchase_date`, CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT('_',t.`color_code`) ELSE '' END) NAME,t.country_code "+
				"  FROM lc_psi_purchase_order o  "+
				"  JOIN lc_psi_purchase_order_item t ON o.`id`=t.`purchase_order_id` and t.del_flag='0' "+
				"  WHERE o.`order_sta` NOT IN ('0','6') GROUP BY o.`order_no`) t "+
				"  JOIN  "+
				"  ( "+
				"  SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT('_',a.`color`) ELSE '' END) NAME,a.`purchase_week`  "+
				"  FROM psi_product_attribute a "+
				"  WHERE a.`del_flag`='0' AND a.`product_name` NOT LIKE 'other' AND a.`product_name` NOT LIKE 'old') a ON t.name=a.name "+
				" WHERE t.`purchase_date`>=:p1 ";
		List<Object[]> list =psiProductAttributeDao.findBySql(sql,new Parameter(DateUtils.addMonths(date,-week)));
		for (Object[] obj: list) {
			String name=obj[0].toString();
			Date purchaseDate=(Date)obj[1];
			String orderId=obj[3].toString();
			String country=obj[4].toString();
			Map<String,Map<String,String>> temp=map.get(country);
			String link=("<a target='_blank' href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPurchaseOrder/view?orderNo="+orderId+"'>"+orderId+"</a>");
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(country, temp);
			}
			if(obj[2]==null){
				String w=monthMap.get(dateFormat.format(purchaseDate));
				Map<String,String> weekMap=temp.get(w);
				if(weekMap==null){
					weekMap=Maps.newHashMap();
					temp.put(w,weekMap);
				}
				weekMap.put(name,(weekMap.get(name)==null?"":weekMap.get(name)+",")+link);
			}else{
				String purchaseWeek=obj[2].toString();
				Map<String,Date[]> tempWeek=Maps.newHashMap();
				for (int i=0;i<=week;i++) {
					Date afterDate=null;
					Date beforeDate=null;
					Date[] arr= new Date[2];
					if("0".equals(purchaseWeek)){
						afterDate=DateUtils.addDays(date,-28*i);
						beforeDate=DateUtils.addDays(afterDate,-28);
					}else if("1".equals(purchaseWeek)){
						afterDate=DateUtils.addDays(DateUtils.addDays(date,7),-28*i);
						beforeDate=DateUtils.addDays(afterDate,-28);
					}else if("2".equals(purchaseWeek)){
						afterDate=DateUtils.addDays(DateUtils.addDays(date,14),-28*i);
						beforeDate=DateUtils.addDays(afterDate,-28);
					}else if("3".equals(purchaseWeek)){
						afterDate=DateUtils.addDays(DateUtils.addDays(date,21),-28*i);
						beforeDate=DateUtils.addDays(afterDate,-28);
					}
					arr[0]=beforeDate;
					arr[1]=afterDate;
					tempWeek.put(i+"", arr);
				}
				for (int i=0;i<=week;i++) {
					Date[] dateArr=tempWeek.get(i+"");
					Date start=dateArr[0];
					Date end=dateArr[1];
					if((purchaseDate.after(start)||purchaseDate.equals(start))&&purchaseDate.before(end)){
						Map<String,String> weekMap=temp.get(i+"");
						if(weekMap==null){
							weekMap=Maps.newHashMap();
							temp.put(i+"",weekMap);
						}
						weekMap.put(name,(weekMap.get(name)==null?"":weekMap.get(name)+",")+link);
						break;
					}
				}
			}
		}
		return map;
	}
	
	
	@Transactional(readOnly = false)
	public void updateCameraman(String cameraman,String productName,String color){
		String sql="update psi_product_attribute set cameraman=:p1 WHERE product_name=:p2 AND color=:p3 ";
		psiProductAttributeDao.updateBySql(sql, new Parameter(cameraman,productName,color));
	}
	
	public Map<String,String> getCameraman(){
		String sql="select CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END),a.cameraman from psi_product_attribute a WHERE a.`del_flag`='0' ";
		List<Object[]> list=psiProductAttributeDao.findBySql(sql);
		Map<String,String> map=Maps.newHashMap();
		for (Object[] obj : list) {
			map.put(obj[0].toString(),obj[1]==null?"":obj[1].toString());
		} 
		return map;
	}
	
	public Map<String,Object[]> findQuantity(Set<String> nameSet){
		Map<String, Object[]> rs = Maps.newHashMap();
		String sql ="SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`='' OR a.`color` IS NULL THEN '' ELSE CONCAT('_', a.`color`)END),a.quantity,inventory_sale_month "+ 
				" FROM psi_product_attribute a WHERE a.del_flag='0' ";
		List<Object[]> list = psiProductAttributeDao.findBySql(sql);
		for (Object[] obj : list) {
			rs.put(obj[0].toString(),obj);
		}
		return rs;
	}

}
