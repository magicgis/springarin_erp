/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.SaleReportDao;
import com.springrain.erp.modules.amazoninfo.entity.ProductPrice;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductTieredPriceService;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 产品销量销售额Service
 * @author Tim
 * @version 2015-06-01
 */
@SuppressWarnings("deprecation")
@Component
@Transactional(readOnly = true)
public class SaleReportService extends BaseService {

	@Autowired
	private SaleReportDao saleReportDao;
	@Autowired
	private PsiProductTieredPriceService tieredPriceService;
	@Autowired
	private AmazonProductService amazonProductService;
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	
	@Autowired
	private PsiProductAttributeService psiProductAttributeService;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SaleReportService.class);
	
	public Map<String,Object[]> findSales(Date start,Date end,String country,String currencyType, Map<String, Float> rateRs,String lineType){
		String temp = "";
		if(StringUtils.isNotEmpty(country) && !"eu".equals(country)&&!"en".equals(country)&&!"unEn".equals(country)){
			temp = "  and a.`country` ='"+country+"'";
		} else if("eu".equals(country)){
			temp = " and a.`country` in ('de','uk','es','fr','it') ";
		} else if("unEn".equals(country)){
			temp = " and a.`country` in ('de','jp','es','fr','it') ";
		}else if("en".equals(country)){
			temp = " and (a.`country` in ('uk','ca') or a.country like 'com%' ) ";
		}
//		String sql = "SELECT ifnull(CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END),'') AS product,TRUNCATE(Sum(CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN a.`sales`*"
//					+AmazonProduct2Service.getRateConfig().get("EUR/USD")+" WHEN a.`country`='uk' THEN a.`sales`*"+AmazonProduct2Service.getRateConfig().get("GBP/USD")+" WHEN a.`country`='ca' THEN a.`sales`*"
//					+AmazonProduct2Service.getRateConfig().get("CAD/USD")+" WHEN a.`country`='jp' THEN a.`sales`*"+
//					AmazonProduct2Service.getRateConfig().get("JPY/USD")+" WHEN a.`country`='mx' THEN a.`sales`*"+AmazonProduct2Service.getRateConfig().get("MXN/USD")+" ELSE a.`sales` END ),2),SUM(a.`sales_volume`) as volume FROM amazoninfo_sale_report a  WHERE a.`date`>=:p1 AND a.`date`<=:p2  "+temp+" GROUP BY product order by volume desc";
		String sql = "SELECT ifnull(CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END),'') AS product,TRUNCATE(Sum(CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN a.`sales`*"
				+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN a.`sales`*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN a.`sales`*"
				+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN a.`sales`*"+
				MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN a.`sales`*"+MathUtils.getRate("MXN", currencyType, rateRs)+" ELSE a.`sales`*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) as volume FROM amazoninfo_sale_report a ";
		if(!"total".equals(lineType)){
				sql+=" left join psi_product c on  a.`product_name` = CONCAT(c.`brand`,' ',c.`model`) and c.del_flag='0' left JOIN sys_dict d ON d.`value`=c.`type` AND d.del_flag='0' AND d.type='product_type' LEFT JOIN psi_product_type_group g ON g.`dict_id`=d.id  ";
		}	
		sql+=" WHERE a.order_type='1' and a.`date`>=:p1 AND a.`date`<=:p2  "+temp+" ";
		if(!"total".equals(lineType)){
	        	if(!"unGrouped".equals(lineType)){
	        		sql+=" and g.id='"+lineType+"' ";
	        	}else{
	        		sql+=" and g.id is null";
	        	}
	    }
		sql+=" GROUP BY product order by volume desc ";
		List<Object[]> list = saleReportDao.findBySql(sql, new Parameter(start,end));
		Map<String,Object[]> rs = Maps.newLinkedHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(), objects);
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void setOrderItemNullName(){
		String sql="SELECT DISTINCT aa.`sellersku` FROM amazoninfo_orderitem aa WHERE aa.`product_name` IS NULL";
		List<String> sellerSkus = saleReportDao.findBySql(sql);
		sql = "SELECT DISTINCT b.sku,b.`product_name`,b.`color` FROM psi_sku b WHERE  b.`del_flag` = '0' AND b.sku in :p1 ";
		List<Object[]> list = saleReportDao.findBySql(sql,new Parameter(sellerSkus));
		if(list.size()>0){
			sql = "UPDATE  amazoninfo_orderitem a  SET a.`product_name`= :p1 ,a.`color` = :p2  WHERE a.`sellersku` = :p3";
			for (Object[] objects : list) {
				saleReportDao.updateBySql(sql, new Parameter(objects[1],objects[2],objects[0]));
			}
		}
		sql ="SELECT sellersku FROM  (SELECT DISTINCT b.`sellersku`,CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) AS productName FROM amazoninfo_orderitem b ) ss GROUP BY ss.`sellersku` HAVING COUNT(1)>1";
		List<Object> skus = saleReportDao.findBySql(sql);
		if(skus.size()>0){
			sql = "SELECT DISTINCT a.`sku`,a.`product_name`,a.`color` FROM psi_sku a WHERE  a.`del_flag`='0' and a.sku in :p1";
			List<Object[]> list1 = saleReportDao.findBySql(sql,new Parameter(skus));
			Map<String, Object[]> rs = Maps.newHashMap();
			for (Object[] objects : list1) {
				rs.put(objects[0].toString(),objects);
			}
			for (Object obj : skus) {
				String sku = obj.toString();
				Object[] objs = rs.get(sku);
				if(objs!=null){
					sql = "UPDATE  amazoninfo_orderitem a  SET a.`product_name`= :p1 ,a.`color` = :p2  WHERE a.`sellersku` = :p3";
					saleReportDao.updateBySql(sql, new Parameter(objs[1],objs[2],sku));
				}
			}
		}
	}
	
	
	@Transactional(readOnly = false)
	public void updateAndSaveSales() {
		 String sql="INSERT INTO `amazoninfo_sale_report` (country,DATE,product_name,color,sales_volume,sure_sales_volume,sales,sure_sales,real_sales_volume,real_sales,sku,order_type,business_order,account_name)  "+ 
					" SELECT REPLACE(REPLACE(REPLACE(a.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') AS country,   "+
					" STR_TO_DATE(DATE_FORMAT(a.`purchase_date`,'%Y%m%d'),'%Y%m%d') AS DATE,b.`product_name`,b.`color`, "+
					" SUM(CASE WHEN a.`order_status`='Canceled'  THEN 0 ELSE b.`quantity_ordered` END) AS sales_volume, "+
					" SUM(CASE WHEN a.`order_status`='Pending'||a.`order_status`='Canceled' THEN 0 ELSE b.`quantity_ordered` END) AS sure_sales_volume, "+
					" SUM(CASE WHEN a.`order_status`='Canceled'  THEN  0 ELSE (IFNULL(b.`item_price`,0)-IFNULL(b.`promotion_discount`,0)) END ) AS sales , "+
					" SUM(CASE WHEN a.`order_status`='Pending'||a.`order_status`='Canceled' THEN 0 ELSE (IFNULL(b.`item_price`,0)-IFNULL(b.`promotion_discount`,0)) END) AS sure_sales ,   "+
					" SUM(CASE WHEN b.`promotion_ids` LIKE '%F-%' || a.`order_status` = 'Pending'||a.`order_status`='Canceled'  THEN 0  ELSE b.`quantity_ordered` END) AS real_sales_volume,   "+
					" SUM(CASE WHEN b.`promotion_ids` LIKE '%F-%' || a.`order_status` = 'Pending'||a.`order_status`='Canceled' THEN 0 ELSE (IFNULL(b.`item_price`,0)-IFNULL(b.`promotion_discount`,0)) END) AS real_sales,   "+
					" b.`sellersku` AS sku,'1', "+
					" SUM(CASE WHEN (a.is_business_order='1' and (a.`order_status`='Shipped'||a.`order_status`='Unshipped'||a.`order_status`='Pending') ) THEN b.`quantity_ordered` ELSE 0 END) AS business_order,a.account_name "+
					" FROM amazoninfo_order a ,amazoninfo_orderitem b WHERE  a.`id` = b.`order_id`   "+ 
					" AND a.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -2 MONTH) AND a.`order_channel` IS NULL and b.`sellersku` is not null and b.`sellersku`!='' "+
					" GROUP BY DATE,b.`product_name`,b.`color`,a.`sales_channel`,b.`sellersku`,a.account_name   "+ 
					" ON DUPLICATE KEY UPDATE `product_name` = VALUES(product_name),`color` = VALUES(color),`sales_volume` = VALUES(sales_volume),`sure_sales_volume` =VALUES(sure_sales_volume),`sales` = VALUES(sales),`sure_sales` = VALUES(sure_sales),`real_sales_volume` = VALUES(real_sales_volume),`real_sales` = VALUES(real_sales),`business_order` = VALUES(business_order) ";
							
		saleReportDao.updateBySql(sql, null);
		
		//计算sku对应产品打包数(为了兼容business项目)
		sql = "UPDATE amazoninfo_sale_report t SET t.`pack_num`=CASE WHEN t.`sku` LIKE '%\\_pack%' THEN SUBSTRING_INDEX(t.`sku`,'_pack',-1) ELSE 1 END WHERE t.`pack_num` IS NULL";
		saleReportDao.updateBySql(sql, null);
		
		Date time = new Date();
		if(!(time.getHours()==0||time.getHours()==23||time.getHours()==4)){
			updateProductProfit();
		}
		
	}
	
	@Transactional(readOnly = false)
	public void updateSalesAttr() {
		Date time = new Date();
		if(time.getHours()==8||time.getHours()==16||time.getHours()==1){
			Map<String,Map<String,PsiProductEliminate>> isNewMap=psiProductEliminateService.findAllByNameAndCountry();
			String attrSql="SELECT r.id,country,CONCAT(r.`product_name`,CASE WHEN r.`color`!='' THEN CONCAT('_',r.`color`) ELSE '' END) NAME "+
                       " FROM amazoninfo_sale_report r WHERE r.`date`>=DATE_ADD(CURRENT_DATE(),INTERVAL -2 DAY) AND r.`product_attr` IS NULL "; 
			List<Object[]> list=saleReportDao.findBySql(attrSql);
			if(list!=null&&list.size()>0){//淘汰>新品>主力>普通
				String attrUpdateSql="update amazoninfo_sale_report set product_attr=:p1 where id=:p2 ";
				for (Object[] obj: list) {
					if(obj[2]!=null){
						Integer id=Integer.parseInt(obj[0].toString());
						String country=obj[1].toString();
						String productName=obj[2].toString();
						String attr="";
						if(isNewMap.get(productName)!=null&&isNewMap.get(productName).get(country)!=null){
							PsiProductEliminate eliminate=isNewMap.get(productName).get(country);
							if("4".equals(eliminate.getIsSale())){
								attr="淘汰";
							}else if("1".equals(eliminate.getIsNew())){
								attr="新品";
							}else {
								attr=DictUtils.getDictLabel(eliminate.getIsSale(), "product_position", "");
							}
						}else {
							attr="普通";
						}
						if(StringUtils.isNotBlank(attr)){
							saleReportDao.updateBySql(attrUpdateSql,new Parameter(attr,id));
						}
					}
				}
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void updateAndSaveOtherSales() {
        
		//统计到销售报表里供显示提示
		try{
			String tempSql=" INSERT INTO `amazoninfo_sale_report` (country,DATE,sku,order_type,account_name,real_order)   "+
				"	SELECT r.country,r.date,r.sku,'1',account_name,(CASE WHEN IFNULL(r.sales_volume,0)-(CASE WHEN (IFNULL(r.ads_order,0)+IFNULL(r.ams_order,0))>(IFNULL(r.flash_sales_order,0)+IFNULL(r.outside_order,0)) THEN (IFNULL(r.ads_order,0)+IFNULL(r.ams_order,0))   "+
				"	ELSE (IFNULL(r.flash_sales_order,0)+IFNULL(r.outside_order,0)) END)>0 THEN       "+
				"	IFNULL(r.sales_volume,0)-(CASE WHEN (IFNULL(r.ads_order,0)+IFNULL(r.ams_order,0))>(IFNULL(r.flash_sales_order,0)+IFNULL(r.outside_order,0)) THEN (IFNULL(r.ads_order,0)+IFNULL(r.ams_order,0))    "+
				"	ELSE (IFNULL(r.flash_sales_order,0)+IFNULL(r.outside_order,0)) END)     "+
				"	ELSE 0 END ) real_order         "+
				"	FROM amazoninfo_sale_report r     "+
				"	WHERE r.`order_type`='1' AND r.`date`>=DATE_ADD(CURRENT_DATE(),INTERVAL -2 MONTH) and r.sku is not null    and r.sku!=''      "+    
				"	ON DUPLICATE KEY UPDATE `real_order` = VALUES(real_order) ";

			saleReportDao.updateBySql(tempSql, null);
		}catch(Exception e){
			LOGGER.error("统计AMS广告异常", e);
		}
	}
	
	@Transactional(readOnly = false)
	public void updateProductProfit(){
		String sql = "INSERT INTO `amazoninfo_sale_profit` (product_name,account_name,country,day,sales,sales_no_tax,sales_volume,buy_cost,amazon_fee,tariff)  "+ 
				"SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) AS pname,a.`account_name`,a.`country`,DATE_FORMAT(a.`date`,'%Y%m%d')," +
				" round((CASE WHEN a.`country` IN ('de','fr','it','es') THEN SUM(a.`sales`)  WHEN a.`country` ='uk' THEN SUM(a.`sales`)*:p1 WHEN a.`country` = 'ca' THEN SUM(a.`sales`)*:p2  WHEN locate('com',a.`country`)>0 THEN SUM(a.`sales`)*:p3 WHEN a.`country` = 'jp' THEN SUM(a.`sales`)*:p4  WHEN a.`country` = 'mx' THEN SUM(a.`sales`)*:p5  ELSE SUM(a.`sales`) END ),2)," +
				" round((CASE WHEN a.`country` IN ('de','fr','it','es') THEN SUM(a.`sales`)  WHEN a.`country` ='uk' THEN SUM(a.`sales`)*:p1 WHEN a.`country` = 'ca' THEN SUM(a.`sales`)*:p2  WHEN locate('com',a.`country`)>0 THEN SUM(a.`sales`)*:p3 WHEN a.`country` = 'jp' THEN SUM(a.`sales`)*:p4   WHEN a.`country` = 'mx' THEN SUM(a.`sales`)*:p5 ELSE SUM(a.`sales`) END )/(CASE WHEN a.`country` ='de' THEN 1.19 WHEN a.`country` ='fr' THEN 1.2 WHEN a.`country` ='it' THEN 1.22 WHEN a.`country` ='es' THEN 1.21 WHEN a.`country` ='uk' THEN 1.2*:p1 WHEN a.`country` = 'ca' THEN 1.15  WHEN a.`country` = 'com' THEN 1.0647 WHEN a.`country` = 'mx' THEN 1.16 WHEN a.`country` = 'jp' THEN 1.08 ELSE 1 END ),2)" +
				" ,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),round((b.cost/(CASE WHEN p.`tax_refund` IS NULL THEN 1.17 ELSE (100+p.`tax_refund`)/100 END)*:p3),2),-1*(round((SUM(b.fba*a.`sales_volume`*IFNULL(a.`pack_num`,1))*:p3),2)+round(((CASE WHEN a.`country` IN ('de','fr','it','es') THEN SUM(a.`sales`)  WHEN a.`country` ='uk' THEN SUM(a.`sales`)*:p1 WHEN a.`country` = 'ca' THEN SUM(a.`sales`)*:p2  WHEN locate('com',a.`country`)>0 THEN SUM(a.`sales`)*:p3  WHEN a.`country` = 'mx' THEN SUM(a.`sales`)*:p5  WHEN a.`country` = 'jp' THEN SUM(a.`sales`)*:p4  ELSE SUM(a.`sales`) END )*b.`commission_pcent`/100),2)),round((SUM(b.tariff)*:p3),2) FROM amazoninfo_sale_report a   JOIN psi_product p  ON a.`product_name` = CONCAT(p.`brand`,' ',p.`model`) AND p.`del_flag` = '0'  LEFT JOIN " +
				"			(SELECT a.`sku`,a.`country`,a.`cost`,a.`fba`,(a.`cost`*a.`tariff_pcent`/100) AS tariff ,a.`commission_pcent` FROM amazoninfo_product_price a WHERE a.`date` = DATE_ADD(CURDATE(),INTERVAL -1 DAY)) b " +
				"			 on  a.`country` = b.country AND  a.`sku` = b.sku  where a.`order_type` = '1' and a.`date`>=DATE_ADD(CURDATE(),INTERVAL -25 DAY) AND a.`product_name` != '' "+
				"            GROUP BY a.`product_name`,a.`color`,a.`country`,a.`date`"+
				" ON DUPLICATE KEY UPDATE `sales` = VALUES(sales),`sales_no_tax` = VALUES(sales_no_tax),`sales_volume` = VALUES(sales_volume),`buy_cost` = VALUES(buy_cost),`amazon_fee` = VALUES(amazon_fee),`tariff` = VALUES(tariff)";
		saleReportDao.updateBySql(sql, new Parameter(MathUtils.getRate("GBP","EUR", null),MathUtils.getRate("CAD","EUR", null),MathUtils.getRate("USD","EUR", null),MathUtils.getRate("JPY","EUR", null),MathUtils.getRate("MXN","EUR", null)));
		//修正新品成本
		sql = "INSERT INTO `amazoninfo_sale_profit` (day,account_name,country,product_name,buy_cost,amazon_fee,tariff)  "+ 
			  "SELECT    "+
			  "	  a.`day`,    "+
				"	    a.`account_name`,a.`country`,    "+
				"	  a.`product_name`,    "+
				"	  ROUND(((CASE WHEN b.currency_type='CNY' THEN b.`moq_price`*:p1 ELSE b.`moq_price`*:p2 END)/  (CASE WHEN c.`tax_refund` IS NULL THEN 1.17 ELSE  (100+c.`tax_refund`)/100 END)),2)  ,  "+
				"	  (ROUND(((CASE WHEN a.`country`='uk' THEN  a.`sales`* :p3 WHEN locate('com',a.`country`)>0 THEN  a.`sales`* :p2 WHEN a.`country`='ca' THEN  a.`sales`* :p4  WHEN a.`country`='jp' THEN  a.`sales`* :p5  WHEN a.`country`='mx' THEN  a.`sales`* :p6   ELSE a.`sales` END) * 0.15),2)+ a.`sales_volume` * 2.5)*-1 as fbafee,   "+
				
				"	   ROUND(((CASE WHEN b.currency_type='CNY' THEN b.`moq_price`/(CASE WHEN c.`tax_refund` IS NULL THEN 1.17 ELSE  (100+c.`tax_refund`)/100 END)*:p1 ELSE b.`moq_price`/  (CASE WHEN c.`tax_refund` IS NULL THEN 1.17 ELSE  (100+c.`tax_refund`)/100 END)*:p2 END)*(CASE WHEN a.`country` ='jp' THEN IFNULL(c.`jp_custom_duty`, 0) WHEN locate('com',a.`country`)>0 THEN IFNULL(c.`us_custom_duty`, 0) WHEN a.`country` ='mx' THEN IFNULL(c.`mx_custom_duty`, 0) WHEN a.`country` ='ca' THEN IFNULL(c.`ca_custom_duty`, 0) WHEN a.`country` IN('de','fr','es','it','uk') THEN IFNULL(c.`eu_custom_duty`, 0) ELSE 0 END )/100),2) tariff    "+
				"	  FROM    "+
				  "	  amazoninfo_sale_profit a,    "+
				  "	  psi_product_attribute b,    "+
				  "	  psi_product c     "+
				"	  WHERE c.`id` = b.`product_id`     "+
				  "	  AND a.`product_name` = CONCAT(    "+
				   "	   b.`product_name`,    "+
				   "	   CASE    "+
				     "	   WHEN b.`color` != ''     "+
				     "	   THEN '_'     "+
				     "	   ELSE ''     "+
				    "	  END,    "+
				    "	  b.`color`    "+
				 "	   )     "+
				 "	   AND c.`del_flag` = '0'     "+
				 "	   AND (a.`fee_quantity` IS NULL or a.`fee_quantity` =0 or (a.`sales_volume`-a.`fee_quantity`)>4 ) "+
				 "	   AND a.`buy_cost` IS NULL     "+
				 "	   AND a.`day` >= DATE_ADD(CURDATE(), INTERVAL - 15 DAY)     "+
				 "	   AND b.`del_flag` = '0'     "+
				 "	   AND b.`moq_price` > 0     "+
				 "	   AND a.`sales` > 0 "+
				 " ON DUPLICATE KEY UPDATE `buy_cost` = VALUES(buy_cost),`amazon_fee` = VALUES(amazon_fee),`tariff` = VALUES(tariff)";
		saleReportDao.updateBySql(sql, new Parameter(MathUtils.getRate("CNY","EUR", null),MathUtils.getRate("USD","EUR", null),MathUtils.getRate("GBP","EUR", null),MathUtils.getRate("CAD","EUR", null),MathUtils.getRate("JPY","EUR", null),MathUtils.getRate("MXN","EUR", null)));
		//运费按照加权平均运费单独统计
		sql = "UPDATE amazoninfo_sale_profit a,(SELECT a.`product_name`,(CASE WHEN a.`country`='eu' THEN 'de,uk,es,it,fr' WHEN a.`country`='us' THEN 'com,mx,ca,com1,com2' ELSE a.`country`  END) AS country1,SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT  a.`avg_price` ORDER BY a.`update_date` DESC),',',1) AS tran  FROM psi_product_avg_price a  GROUP BY a.`product_name`,a.`country`) b  set a.`transport_fee`= ROUND(((a.`sales_volume`*b.tran)*:p1),2)  WHERE a.`product_name` = b.product_name AND FIND_IN_SET( a.country,b.country1) AND ( (a.`fee_quantity` IS NULL  OR a.`fee_quantity`=0) OR (a.`transport_fee` = NULL OR a.`transport_fee` = 0) OR (a.`sales_volume` - a.`fee_quantity`) > 4)AND a.`sales_volume`>0 AND a.`day`>DATE_ADD(CURDATE(),INTERVAL -90 DAY)";
		saleReportDao.updateBySql(sql, new Parameter(MathUtils.getRate("CNY","EUR", null)));
		
		//统计利润
		sql = "UPDATE  amazoninfo_sale_profit a  SET a.profits= (a.`sales_no_tax`+IFNULL(a.`amazon_fee`,0)-IFNULL(a.`transport_fee`,0)-IFNULL(a.`buy_cost`,0)*a.`sales_volume`) WHERE ((a.profits is null or a.profits=0) or (a.`fee_quantity` IS NULL or a.`fee_quantity` = 0 ) or (a.`sales_volume`-a.`fee_quantity`)>4 ) AND a.`sales_no_tax`>0";
		saleReportDao.updateBySql(sql,null);
		
		

		Map<String,Map<String,PsiProductEliminate>> isNewMap=psiProductEliminateService.findAllByNameAndCountry();
		String attrSql="SELECT r.id,country,r.`product_name` FROM amazoninfo_sale_profit r "+
                   " WHERE r.`day`>=DATE_ADD(CURRENT_DATE(),INTERVAL -2 DAY) AND r.`product_attr` IS NULL "; 
		List<Object[]> list=saleReportDao.findBySql(attrSql);
		if(list!=null&&list.size()>0){//淘汰>新品>主力>普通
			String attrUpdateSql="update amazoninfo_sale_profit set product_attr=:p1 where id=:p2 ";
			for (Object[] obj: list) {
				if(obj[2]!=null){
					Integer id=Integer.parseInt(obj[0].toString());
					String country=obj[1].toString();
					String productName=obj[2].toString();
					String attr="";
					if(isNewMap.get(productName)!=null&&isNewMap.get(productName).get(country)!=null){
						PsiProductEliminate eliminate=isNewMap.get(productName).get(country);
						if("4".equals(eliminate.getIsSale())){
							attr="淘汰";
						}else if("1".equals(eliminate.getIsNew())){
							attr="新品";
						}else {
							attr=DictUtils.getDictLabel(eliminate.getIsSale(), "product_position", "");
						}
					}else {
						attr="普通";
					}
					if(StringUtils.isNotBlank(attr)){
						saleReportDao.updateBySql(attrUpdateSql,new Parameter(attr,id));
					}
				}
			}
		}
		
	}
	
	
	
	@Transactional(readOnly = false)
	public void updateMaxOrderAndPromotionsOrder() {
		
		String sql="INSERT INTO `amazoninfo_sale_report` (country,DATE,product_name,color,sku,order_type,max_order,promotions_order,flash_sales_order,free_order,outside_order,account_name)   "+
				" SELECT a.country,a.dates,a.product_name,a.color,a.sku,'1',MAX(CASE WHEN (a.type='3' AND a.order_status!='Canceled') THEN a.quantity ELSE 0 END) max_order,  "+
				"  SUM(CASE WHEN (a.type='1' AND a.order_status!='Canceled') THEN a.quantity ELSE 0 END) promotions_order,SUM(CASE WHEN (a.type='2' AND a.order_status!='Canceled') THEN a.quantity ELSE 0 END) flash_sales_order,  "+
				"  SUM(CASE WHEN (a.type='5' AND a.order_status!='Canceled') THEN a.quantity ELSE 0 END) free_order,  "+
				"  SUM(a.outQuantity) outside_order,a.account_name  "+
				"  FROM (SELECT STR_TO_DATE(DATE_FORMAT(a.`purchase_date`,'%Y%m%d'),'%Y%m%d') dates,b.`sellersku` sku,REPLACE(REPLACE(REPLACE(a.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') AS country,b.`product_name`,b.`color`,   "+
				"  a.order_status,(CASE WHEN ( ( (b.`promotion_ids` LIKE 'F-%' OR b.`promotion_ids` LIKE '%,F-%' or b.`promotion_ids` LIKE 'S-%'  OR b.`promotion_ids` LIKE '%,S-%' ) AND (a.order_status='Shipped' OR a.order_status='Unshipped') AND  b.`promotion_discount` >0 AND b.`promotion_ids` NOT LIKE 'F-%AMZ%' AND b.`promotion_ids` NOT LIKE '%,F-%AMZ%') OR ( (b.`promotion_ids` LIKE 'F-%' OR b.`promotion_ids` LIKE '%,F-%'  or b.`promotion_ids` LIKE 'S-%'  OR b.`promotion_ids` LIKE '%,S-%' ) AND (a.order_status='Canceled' OR a.order_status='Pending') AND b.`promotion_ids` NOT LIKE 'F-%AMZ%' AND b.`promotion_ids` NOT LIKE '%,F-%AMZ%' )   ) THEN '1'  "+
				"  WHEN ( ( (b.`promotion_ids` LIKE 'F-%' OR b.`promotion_ids` LIKE '%,F-%') AND (a.order_status='Shipped' OR a.order_status='Unshipped') AND  b.`promotion_discount` >0  AND b.`promotion_ids` NOT LIKE 'F-%A-Page%' AND b.`promotion_ids` NOT LIKE '%,F-%A-Page%' AND b.`promotion_ids` NOT LIKE 'F-%AMZ%' AND b.`promotion_ids` NOT LIKE '%,F-%AMZ%') OR ( (b.`promotion_ids` LIKE 'F-%' OR b.`promotion_ids` LIKE '%,F-%') AND (a.order_status='Canceled' OR a.order_status='Pending') AND b.`promotion_ids` NOT LIKE 'F-%A-Page%' AND b.`promotion_ids` NOT LIKE '%,F-%A-Page%' AND b.`promotion_ids` NOT LIKE 'F-%AMZ%' AND b.`promotion_ids` NOT LIKE '%,F-%AMZ%' )   ) THEN '6' "+
				"  WHEN ((b.`promotion_ids` IS NULL OR (b.`promotion_ids` IS NOT NULL AND b.`promotion_ids` NOT LIKE '%,%' AND (b.`promotion_ids` LIKE '%Free Shipping%' or b.`promotion_ids` LIKE 'Free Delivery%') )) AND b.`promotion_discount` >0) THEN '2' WHEN (SUM(b.quantity_ordered)>=10) THEN '3' WHEN (b.`promotion_ids` LIKE 'Free-%' OR b.`promotion_ids` LIKE '%,Free-%' OR b.`promotion_ids` LIKE 'R-%' OR b.`promotion_ids` LIKE '%,R-%' OR b.`promotion_ids` LIKE 'F-%AMZ%' OR b.`promotion_ids` LIKE '%,F-%AMZ%') THEN '5' ELSE '4' END) TYPE,SUM(b.quantity_ordered) quantity, "+
				 "  SUM(CASE WHEN ( ( (b.`promotion_ids` LIKE 'F-%' OR b.`promotion_ids` LIKE '%,F-%') AND (a.order_status='Shipped' OR a.order_status='Unshipped') AND  b.`promotion_discount` >0  AND b.`promotion_ids` NOT LIKE 'F-%A-Page%' AND b.`promotion_ids` NOT LIKE '%,F-%A-Page%' AND b.`promotion_ids` NOT LIKE 'F-%AMZ%' AND b.`promotion_ids` NOT LIKE '%,F-%AMZ%') OR ( (b.`promotion_ids` LIKE 'F-%' OR b.`promotion_ids` LIKE '%,F-%') AND (a.order_status='Canceled' OR a.order_status='Pending') AND b.`promotion_ids` NOT LIKE 'F-%A-Page%' AND b.`promotion_ids` NOT LIKE '%,F-%A-Page%' AND b.`promotion_ids` NOT LIKE 'F-%AMZ%' AND b.`promotion_ids` NOT LIKE '%,F-%AMZ%' )   ) THEN b.quantity_ordered ELSE 0 END)  outQuantity,a.account_name "+
				"  FROM  amazoninfo_order a JOIN amazoninfo_orderitem b ON a.id=b.order_id 	 	 "+		
				"  WHERE a.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -1 MONTH)  "+
				"  AND a.order_status IN ('Shipped','Pending','Unshipped','Canceled') and b.`sellersku` is not null and b.`sellersku`!=''   "+
				"  GROUP BY a.id,b.`sellersku`,b.`product_name`,b.`color`,country,dates,b.`promotion_ids`,b.`promotion_discount`,a.order_status,a.account_name) a   "+
				" GROUP BY a.dates,a.country,a.product_name,a.color,a.sku,a.account_name "+
				" ON DUPLICATE KEY UPDATE `product_name` = VALUES(product_name),`color` = VALUES(color),`max_order` = VALUES(max_order),`promotions_order` = VALUES(promotions_order),`flash_sales_order` = VALUES(flash_sales_order),`free_order` = VALUES(free_order),`outside_order` = VALUES(outside_order) ";
		saleReportDao.updateBySql(sql, null);
	}
	
	
	@Transactional(readOnly = false)
	public void updateProductCycle() {
		String sql="SELECT  aa.id,TO_DAYS(bb.saleDate)-TO_DAYS(aa.open_date) FROM "+
				    " (SELECT a.id,a.`country`,a.`sku`,a.`open_date` FROM amazoninfo_product2 a WHERE a.`is_fba` = '1' AND a.`open_date` IS NOT NULL  AND  a.open_cycle IS NULL) aa, "+
				    " (SELECT a.`country`,a.`sku`,MIN(a.`date`) AS saleDate FROM amazoninfo_sale_report a WHERE  a.`sure_sales`>0 AND a.`sales_volume`>0 AND a.order_type = '1'  AND a.`product_name`!= 'inateck old' AND a.`product_name`!= 'inateck other'  GROUP BY a.`country`,a.`sku`) bb WHERE BINARY(aa.sku) = bb.sku AND aa.country = bb.country AND bb.saleDate > aa.open_date  ";
		List<Object[]> list = saleReportDao.findBySql(sql);
		if(list.size()>0){
			sql = "UPDATE amazoninfo_product2 a SET a.open_cycle = :p1 WHERE a.`id` = :p2";
			for (Object[] objects : list) {
				saleReportDao.updateBySql(sql, new Parameter(objects[1],objects[0]));
			}
		}
	}

	@Transactional(readOnly = false)
	public void updateAdsOrder() {
		
		String sql="INSERT INTO `amazoninfo_sale_report` (country,DATE,sku,order_type,account_name,ads_order)  "+ 
				" SELECT a.country,a.data_date,a.sku,'1',IFNULL(a.account_name,CASE WHEN a.country='com' THEN CONCAT('Inateck','_','US') ELSE CONCAT('Inateck','_',UPPER(a.country)) END),SUM(a.`same_sku_orders_placed`) ads_order FROM amazoninfo_advertising a where a.data_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -1 MONTH) and a.sku!=''  and a.sku is not null  "+
				" GROUP BY a.`country`,a.sku,a.`data_date`,a.account_name HAVING SUM(a.`same_sku_orders_placed`)>0 "+
				" ON DUPLICATE KEY UPDATE `ads_order` = VALUES(ads_order) ";
		saleReportDao.updateBySql(sql, null);
		
		String delSql="DELETE FROM amazoninfo_sale_report WHERE date>=DATE_ADD(CURRENT_DATE(),INTERVAL -2 MONTH) and sales_volume=0 and review_volume is null and support_volume is null ";
		saleReportDao.updateBySql(delSql, null);
	}
	
	
	
	@Transactional(readOnly = false)
	public void findAmsOrder(){
		String sql="SELECT a.`country`,a.`product_name`,a.`data_date`,SUM(units_sold) FROM amazoninfo_aws_adversting a "+
                   " WHERE a.`data_date`>:p1 AND a.`units_sold`>0 GROUP BY a.`country`,a.`product_name`,a.`data_date` ";
		List<Object[]> list=saleReportDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-60)));
		for (Object[] obj: list) {
			if(obj[1]!=null){
				String country=obj[0].toString();
				String name=obj[1].toString();
				Date date=(Date)obj[2];
				Integer ams=Integer.parseInt(obj[3].toString());
				if(StringUtils.isNotBlank(name)){
					SaleReport report=findByProductId(country,name,date);
					if(report!=null){
						report.setAmsOrder(ams);
						saleReportDao.save(report);
					}
				}
			}
		}
	}
	
	
	public SaleReport findByProductId(String country,String productName,Date date) {
		DetachedCriteria dc = saleReportDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country", country));
		dc.add(Restrictions.eq("date",date));
		String color = "";
		String name=productName;
		if(productName.indexOf("_")>0){
			name = productName.substring(0,productName.indexOf("_"));
			color = productName.substring(productName.indexOf("_")+1);
		}
		dc.add(Restrictions.eq("productName",name));
		if(StringUtils.isNotBlank(color)){
			dc.add(Restrictions.eq("color",color));
		}
		dc.addOrder(Order.asc("id"));
		List<SaleReport> reportList=saleReportDao.find(dc);
		if(reportList!=null&&reportList.size()>0){
			return reportList.get(0);
		}
		return null;
   }
	
	@Transactional(readOnly = false)
	public void updateReviewsOrSupportOrder() {
		String sql="INSERT INTO `amazoninfo_sale_report` (country,DATE,product_name,color,sku,order_type,review_volume,support_volume,account_name)  "+
				" SELECT r.`country`,STR_TO_DATE(DATE_FORMAT(r.`create_date`,'%Y%m%d'),'%Y%m%d') AS DATE,t.product_name,t.color,t.`sellersku`,'1', "+
				" 	SUM(CASE WHEN (r.`order_type`='Review' or r.`order_type`='Paypal_Refund' or r.order_type='Marketing')  THEN t.`quantity_ordered` ELSE 0 END) AS review_volume, "+
				"	SUM(CASE WHEN (r.`order_type`='Support' )  THEN t.`quantity_ordered` ELSE 0 END) AS support_volume,IFNULL(r.account_name,CASE WHEN r.`country` in ('com','com1') THEN 'Inateck_US' WHEN r.`country`='com2' THEN 'TDKRFSEB_US' WHEN r.`country`='com3' THEN 'Tomons_US' ELSE CONCAT('Inateck','_',UPPER(r.`country`)) END) "+
				"	FROM amazoninfo_outbound_order r "+
				"	JOIN amazoninfo_outbound_orderitem t ON r.id=t.`order_id` "+
				"	WHERE r.`order_status`='COMPLETE' AND r.`order_type`!='Ebay' and r.`create_date`>=DATE_ADD(CURRENT_DATE(),INTERVAL -1 MONTH) and t.sellersku is not null and t.sellersku!=''  GROUP BY r.`country`,DATE,t.sellersku,t.product_name,t.color,r.account_name "+
				"	ON DUPLICATE KEY UPDATE `product_name` = VALUES(product_name),`color` = VALUES(color),`review_volume` = VALUES(review_volume),`support_volume` = VALUES(support_volume) ";
		saleReportDao.updateBySql(sql, null);
		
		String sql2="SELECT id,country,sku FROM amazoninfo_sale_report t WHERE t.`order_type`='1' AND t.`date`>=:p1 AND t.product_name IS NULL and sku is not null and sku!=''";
		List<Object[]> list=saleReportDao.findBySql(sql2,new Parameter(DateUtils.addDays(new Date(),-100)));
		
		//
		if(list!=null&&list.size()>0){
			Map<String,Map<String,Set<Integer>>> map=Maps.newHashMap();
			for(Object[] obj:list){
				Integer id=Integer.parseInt(obj[0].toString());
				String country=obj[1].toString();
				String sku=obj[2].toString();
				Map<String,Set<Integer>> temp=map.get(country);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(country, temp);
				}
				Set<Integer> idSet=temp.get(sku);
				if(idSet==null){
					idSet=Sets.newHashSet();
					temp.put(sku, idSet);
				}
				idSet.add(id);
			}
			
			if(map!=null&&map.size()>0){
				 String updateSql="UPDATE amazoninfo_sale_report SET product_name=:p1,color=:p2 WHERE id IN :p3 ";
				 for (Map.Entry<String, Map<String, Set<Integer>>>  entry : map.entrySet()) { 
				     String country=entry.getKey();
					 Map<String,Set<Integer>> countryMap=entry.getValue();
					 for (Map.Entry<String, Set<Integer>>  entryRs : countryMap.entrySet()) { 
					     String sku=entryRs.getKey();
						 Set<Integer> idSet=entryRs.getValue();
						 PsiSku psiSku=amazonProductService.getProductBySku(sku,country);
						 if(psiSku!=null){
							 saleReportDao.updateBySql(updateSql, new Parameter(psiSku.getProductName(),psiSku.getColor()==null?"":psiSku.getColor(),idSet));
						 }
					 }
				 }
			}
		}
		
		String tempSql=" INSERT INTO `amazoninfo_sale_report` (country,DATE,sku,order_type,account_name,real_order)   "+
				"	SELECT r.country,r.date,r.sku,'1',account_name,(CASE WHEN IFNULL(r.sales_volume,0)-(CASE WHEN (IFNULL(r.ads_order,0)+IFNULL(r.ams_order,0))>(IFNULL(r.flash_sales_order,0)+IFNULL(r.outside_order,0)) THEN (IFNULL(r.ads_order,0)+IFNULL(r.ams_order,0))   "+
				"	ELSE (IFNULL(r.flash_sales_order,0)+IFNULL(r.outside_order,0)) END)>0 THEN       "+
				"	IFNULL(r.sales_volume,0)-(CASE WHEN (IFNULL(r.ads_order,0)+IFNULL(r.ams_order,0))>(IFNULL(r.flash_sales_order,0)+IFNULL(r.outside_order,0)) THEN (IFNULL(r.ads_order,0)+IFNULL(r.ams_order,0))    "+
				"	ELSE (IFNULL(r.flash_sales_order,0)+IFNULL(r.outside_order,0)) END)     "+
				"	ELSE 0 END ) real_order         "+
				"	FROM amazoninfo_sale_report r     "+
				"	WHERE r.`order_type`='1' AND r.`date`>=DATE_ADD(CURRENT_DATE(),INTERVAL -2 MONTH) and r.sku is not null    and r.sku!=''      "+    
				"	ON DUPLICATE KEY UPDATE `real_order` = VALUES(real_order) ";

			saleReportDao.updateBySql(tempSql, null);
	}
	
	@Transactional(readOnly = false)
	public void updateAndSaveSalesByType() {
		try{
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMM");
			Date date=new Date();
		    String changeSql="SELECT GROUP_CONCAT(DISTINCT TYPE) ptype FROM amazoninfo_report_month_type t "+
	                 " WHERE t.`month`=:p1 OR t.`month`=:p2 GROUP BY t.`product_name` HAVING LOCATE(',',ptype)!=0 ";
		    List<String> changeList=saleReportDao.findBySql(changeSql,new Parameter(dateFormat.format(date),dateFormat.format(DateUtils.addMonths(date, -1))));
		    if(changeList!=null&&changeList.size()>0){
		    	Set<String> typeSet=Sets.newHashSet();
		    	for (String type : changeList) {
					String[] typeArr=type.split(",");
					typeSet.add(typeArr[0]);
					typeSet.add(typeArr[1]);
				}
		    	if(typeSet!=null&&typeSet.size()>0){
		    		String sql="INSERT INTO amazoninfo_sale_report_type(TYPE,country,sales,sure_sales,real_sales,sales_volume,sure_sales_volume,real_sales_volume,DATE,order_type,account_name) " +
		    				" SELECT b.`TYPE`,a.`country`,SUM(IFNULL(a.`sales`,0)) sales,SUM(IFNULL(a.`sure_sales`,0)) sure_sales,SUM(IFNULL(a.`real_sales`,0)) real_sales, " +
		    				" SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) sales_volume,SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)) sure_sales_volume,SUM(a.`real_sales_volume`*IFNULL(a.`pack_num`,1)) real_sales_volume,a.`date`,a.order_type,a.account_name FROM amazoninfo_sale_report a ,psi_product b  " +
		    				" WHERE b.`del_flag`='0' AND a.`product_name` = CONCAT(b.`brand`,' ',b.`model`) AND b.type in :p1 " +
		    				" GROUP BY b.`TYPE`,a.`country`,a.`date`,a.order_type,a.account_name ORDER BY a.`country`,a.`date` " +
		    				" ON DUPLICATE KEY UPDATE `sales_volume` = VALUES(sales_volume),`sure_sales_volume` =VALUES(sure_sales_volume),`sales` = VALUES(sales),`sure_sales` = VALUES(sure_sales),`real_sales_volume` = VALUES(real_sales_volume),`real_sales` = VALUES(real_sales) ";
		    	    saleReportDao.updateBySql(sql,new Parameter(typeSet));
		    	}
		    }
		}catch(Exception e){
			LOGGER.error("产品类型变更销售按类型统计异常", e);
		}
	    
		String sql="INSERT INTO amazoninfo_sale_report_type(TYPE,country,sales,sure_sales,real_sales,sales_volume,sure_sales_volume,real_sales_volume,DATE,order_type,account_name) " +
				" SELECT b.`TYPE`,a.`country`,SUM(IFNULL(a.`sales`,0)) sales,SUM(IFNULL(a.`sure_sales`,0)) sure_sales,SUM(IFNULL(a.`real_sales`,0)) real_sales, " +
				" SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) sales_volume,SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)) sure_sales_volume,SUM(a.`real_sales_volume`*IFNULL(a.`pack_num`,1)) real_sales_volume,a.`date`,a.order_type,a.account_name FROM amazoninfo_sale_report a ,psi_product b  " +
				" WHERE b.`del_flag`='0' AND a.`product_name` = CONCAT(b.`brand`,' ',b.`model`) AND a.`date`>=DATE_ADD(CURRENT_DATE(),INTERVAL -3 MONTH) " +
				" GROUP BY b.`TYPE`,a.`country`,a.`date`,a.order_type,a.account_name ORDER BY a.`country`,a.`date` " +
				" ON DUPLICATE KEY UPDATE `sales_volume` = VALUES(sales_volume),`sure_sales_volume` =VALUES(sure_sales_volume),`sales` = VALUES(sales),`sure_sales` = VALUES(sure_sales),`real_sales_volume` = VALUES(real_sales_volume),`real_sales` = VALUES(real_sales) ";
	    saleReportDao.updateBySql(sql, null);
	    
	    String typeSql="SELECT DISTINCT TYPE FROM psi_product WHERE del_flag='0' ";
	    List<String> typeList=saleReportDao.findBySql(typeSql);
	    
	    String delSql="DELETE FROM amazoninfo_sale_report_type WHERE TYPE NOT IN :p1 ";
	    saleReportDao.updateBySql(delSql, new Parameter(typeList));
	}
	
	@Transactional(readOnly = false)
	public void updateAndSaveMaxOrder() {
		String sql="INSERT INTO amazoninfo_max_order(country,date,product_name,quantity) " +
				" SELECT REPLACE(REPLACE(REPLACE(r.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') country,STR_TO_DATE(DATE_FORMAT(r.`purchase_date`,'%Y%m%d'),'%Y%m%d') AS DATE, " +
				" CONCAT(t.`product_name`,CASE  WHEN t.`color`='' THEN '' ELSE CONCAT('_',t.`color`) END) NAME,SUM(t.`quantity_ordered`) quantity " +
				" FROM amazoninfo_order r JOIN amazoninfo_orderitem t ON r.id=t.order_id WHERE r.order_status!='Canceled' " +
				" AND r.`purchase_date`>=DATE_ADD(CURRENT_DATE(),INTERVAL -1 MONTH) and t.`product_name` is not null "+
				" GROUP BY r.`amazon_order_id`,country,DATE,name " +
				" HAVING SUM(t.`quantity_ordered`)>=10 " +
				" ON DUPLICATE KEY UPDATE `quantity` = VALUES(quantity) ";
	    saleReportDao.updateBySql(sql, null);
	}
	
	@Transactional(readOnly = false)
	public void ebayAndVendorAndUnline() {
		String vendorSql="INSERT INTO `amazoninfo_sale_report` (country,DATE,product_name,color,sales_volume,sure_sales_volume,sales,sure_sales,real_sales_volume,real_sales,sku,order_type,account_name) "+
				" SELECT a.country,STR_TO_DATE(DATE_FORMAT(a.`ordered_date`,'%Y%m%d'),'%Y%m%d') AS DATE,SUBSTRING_INDEX(b.`product_name`,'_',1) product_name, "+
				" (CASE WHEN SUBSTRING_INDEX(b.`product_name`,'_',1)!=SUBSTRING_INDEX(b.`product_name`,'_',-1) THEN SUBSTRING_INDEX(b.`product_name`,'_',-1) ELSE '' END) color, "+
				" SUM(b.`accepted_quantity`) sales_volume,SUM(b.`received_quantity`) sure_sales_volume,SUM(b.`accepted_quantity`*b.unit_price) sales,SUM(b.`received_quantity`*b.unit_price) sure_sales, "+
				" SUM(b.`received_quantity`) real_sales_volume,SUM(b.`received_quantity`*b.unit_price) real_sales,b.sku,'2','Vendor' "+
				" FROM amazoninfo_vendor_order a  "+
				" JOIN amazoninfo_vendor_orderitem b ON a.`id`=b.`order_id` "+
				" where a.ordered_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -3 MONTH) and b.sku is not null and b.sku!='' "+
				" GROUP BY DATE,product_name,color,SUBSTRING_INDEX(b.`product_name`,'_',-1),a.country,b.sku  "+
				" ON DUPLICATE KEY UPDATE `product_name` = VALUES(product_name),`color` = VALUES(color),`sales_volume` = VALUES(sales_volume),`sure_sales_volume` =VALUES(sure_sales_volume),`sales` = VALUES(sales),`sure_sales` = VALUES(sure_sales),`real_sales_volume` = VALUES(real_sales_volume),`real_sales` = VALUES(real_sales) ";
		saleReportDao.updateBySql(vendorSql, null);
		
		String ebaySql="INSERT INTO `amazoninfo_sale_report` (country,DATE,product_name,color,sales_volume,sure_sales_volume,sales,sure_sales,real_sales_volume,real_sales,sku,order_type,account_name) "+
    		  " SELECT a.country country,STR_TO_DATE(DATE_FORMAT(a.`created_time`,'%Y%m%d'),'%Y%m%d') AS DATE,s.product_name,s.color, "+
    		  " SUM(CASE WHEN a.`order_status`='Cancelled'  THEN 0 ELSE b.`quantity_purchased`*(CASE WHEN b.`sku` LIKE '%\\_q%' THEN SUBSTRING_INDEX(b.`sku`,'_q',-1) ELSE 1 END) END) AS sales_volume, "+
    		  " SUM(CASE WHEN a.`order_status`='Inactive'||a.`order_status`='Cancelled' THEN 0 ELSE b.`quantity_purchased`*(CASE WHEN b.`sku` LIKE '%\\_q%' THEN SUBSTRING_INDEX(b.`sku`,'_q',-1) ELSE 1 END) END) AS sure_sales_volume,  "+
    		  " SUM(CASE WHEN a.`order_status`='Cancelled'  THEN 0 ELSE b.`quantity_purchased`*transaction_price END) AS sales, "+
    		  " SUM(CASE WHEN a.`order_status`='Inactive'||a.`order_status`='Cancelled' THEN 0 ELSE b.`quantity_purchased`*transaction_price END) AS sure_sales, "+
    		  " SUM(CASE WHEN a.`order_status`='Cancelled'  THEN 0 ELSE b.`quantity_purchased`*(CASE WHEN b.`sku` LIKE '%\\_q%' THEN SUBSTRING_INDEX(b.`sku`,'_q',-1) ELSE 1 END) END) AS real_sales_volume, "+
    		  " SUM(CASE WHEN a.`order_status`='Inactive'||a.`order_status`='Cancelled' THEN 0 ELSE b.`quantity_purchased`*transaction_price END) AS real_sales,b.sku,'3','Ebay' "+
    		  " FROM ebay_order a JOIN ebay_orderitem  b ON a.id=b.order_id left JOIN "+
    		  " (SELECT DISTINCT sku,product_name,color FROM psi_sku s WHERE s.del_flag='0' AND country in ('ebay','ebay_com')) s ON b.sku=s.sku "+
    		  " where  a.created_time>=DATE_ADD(CURRENT_DATE(),INTERVAL -1 MONTH) and b.sku is not null and b.sku!='' "+
    		  " GROUP BY DATE,s.product_name,s.color,country,b.sku  "+
    		  " ON DUPLICATE KEY UPDATE `product_name` = VALUES(product_name),`color` = VALUES(color),`sales_volume` = VALUES(sales_volume),`sure_sales_volume` =VALUES(sure_sales_volume),`sales` = VALUES(sales),`sure_sales` = VALUES(sure_sales),`real_sales_volume` = VALUES(real_sales_volume),`real_sales` = VALUES(real_sales) ";
		saleReportDao.updateBySql(ebaySql, null);
		
       String unlineSql="INSERT INTO `amazoninfo_sale_report` (country,DATE,product_name,color,sales_volume,sure_sales_volume,sales,sure_sales,real_sales_volume,real_sales,sku,order_type,account_name) "+
    		  " SELECT (CASE WHEN a.sales_channel=19 THEN 'de' WHEN a.sales_channel=120 THEN 'com' ELSE 'cn' END) country,STR_TO_DATE(DATE_FORMAT(a.`purchase_date`,'%Y%m%d'),'%Y%m%d') AS DATE,b.product_name,b.color, "+
    		 "  SUM(CASE WHEN a.`order_status`='Canceled'||a.`order_status`='PaymentPending'  THEN 0 ELSE b.`quantity_ordered` END) AS sales_volume, "+
    		 "  SUM(CASE WHEN a.`order_status`='Shipped' THEN b.`quantity_ordered` ELSE 0 END) AS sure_sales_volume,  "+
    		 " SUM(CASE WHEN a.`order_status`='Canceled'||a.`order_status`='PaymentPending'  THEN 0 ELSE b.`quantity_ordered`*item_price*  "+ 
    		 " (CASE WHEN a.sales_channel=19 THEN (case when a.marketplace_id='GBP' then "+MathUtils.getRate("GBP","EUR",null)+" when a.marketplace_id='USD' then "+MathUtils.getRate("USD","EUR",null)+" when a.marketplace_id='CAD' then "+MathUtils.getRate("CAD","EUR",null)+" when a.marketplace_id='JPY' then "+MathUtils.getRate("JPY","EUR",null)+" when a.marketplace_id='MXN' then "+MathUtils.getRate("MXN","EUR",null)+" when a.marketplace_id='CNY' then "+MathUtils.getRate("CNY","EUR",null)+"  else 1 end )"+
    		 " WHEN a.sales_channel=120 THEN (case when a.marketplace_id='GBP' then "+MathUtils.getRate("GBP","USD",null)+" when a.marketplace_id='EUR' then "+MathUtils.getRate("EUR","USD",null)+" when a.marketplace_id='CAD' then "+MathUtils.getRate("CAD","USD",null)+" when a.marketplace_id='JPY' then "+MathUtils.getRate("JPY","USD",null)+" when a.marketplace_id='MXN' then "+MathUtils.getRate("MXN","USD",null)+" when a.marketplace_id='CNY' then "+MathUtils.getRate("CNY","USD",null)+"  else 1 end ) "+
    		 " ELSE (case when a.marketplace_id='GBP' then "+MathUtils.getRate("GBP","USD",null)+" when a.marketplace_id='EUR' then "+MathUtils.getRate("EUR","USD",null)+" when a.marketplace_id='CAD' then "+MathUtils.getRate("CAD","USD",null)+" when a.marketplace_id='JPY' then "+MathUtils.getRate("JPY","USD",null)+" when a.marketplace_id='MXN' then "+MathUtils.getRate("MXN","USD",null)+" when a.marketplace_id='CNY' then "+MathUtils.getRate("CNY","USD",null)+"  else 1 end ) END )"+
    		 " END) AS sales, "+
    		 "  SUM(CASE WHEN a.`order_status`='Shipped' THEN b.`quantity_ordered`*item_price*"+
    		 " (CASE WHEN a.sales_channel=19 THEN (case when a.marketplace_id='GBP' then "+MathUtils.getRate("GBP","EUR",null)+" when a.marketplace_id='USD' then "+MathUtils.getRate("USD","EUR",null)+" when a.marketplace_id='CAD' then "+MathUtils.getRate("CAD","EUR",null)+" when a.marketplace_id='JPY' then "+MathUtils.getRate("JPY","EUR",null)+" when a.marketplace_id='MXN' then "+MathUtils.getRate("MXN","EUR",null)+" when a.marketplace_id='CNY' then "+MathUtils.getRate("CNY","EUR",null)+"  else 1 end )"+
    		 " WHEN a.sales_channel=120 THEN (case when a.marketplace_id='GBP' then "+MathUtils.getRate("GBP","USD",null)+" when a.marketplace_id='EUR' then "+MathUtils.getRate("EUR","USD",null)+" when a.marketplace_id='CAD' then "+MathUtils.getRate("CAD","USD",null)+" when a.marketplace_id='JPY' then "+MathUtils.getRate("JPY","USD",null)+" when a.marketplace_id='MXN' then "+MathUtils.getRate("MXN","USD",null)+" when a.marketplace_id='CNY' then "+MathUtils.getRate("CNY","USD",null)+"  else 1 end ) "+
    		 " ELSE (case when a.marketplace_id='GBP' then "+MathUtils.getRate("GBP","USD",null)+" when a.marketplace_id='EUR' then "+MathUtils.getRate("EUR","USD",null)+" when a.marketplace_id='CAD' then "+MathUtils.getRate("CAD","USD",null)+" when a.marketplace_id='JPY' then "+MathUtils.getRate("JPY","USD",null)+" when a.marketplace_id='MXN' then "+MathUtils.getRate("MXN","USD",null)+" when a.marketplace_id='CNY' then "+MathUtils.getRate("CNY","USD",null)+"  else 1 end ) END )"+
    		 " ELSE  0 END) AS sure_sales, "+
    		 "  SUM(CASE WHEN a.`order_status`='Shipped'  THEN b.`quantity_ordered` ELSE 0 END) AS real_sales_volume, "+
    		 "  SUM(CASE WHEN a.`order_status`='Shipped' THEN b.`quantity_ordered`*item_price ELSE  0 END) AS real_sales,b.sellersku, "+
    		 "  (CASE WHEN a.order_channel='管理员' THEN '5' WHEN a.order_channel='check24' THEN '6' when a.order_channel like '%-OTHER-%' then '7'  ELSE '4' END) order_type, "+
    		 "  (CASE WHEN a.order_channel='管理员' THEN 'Website' WHEN a.order_channel='check24' THEN 'Check24' when a.order_channel like '%-OTHER-%' then 'Other'  ELSE 'Offline' END) "+
    		 "  FROM amazoninfo_unline_order a JOIN amazoninfo_unline_orderitem b ON a.id=b.order_id  WHERE cancel_date IS NULL "+
    		 "  and a.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -2 MONTH) and b.sellersku is not null and b.sellersku!='' "+
    		 "  GROUP BY DATE,b.product_name,b.color,country,b.sellersku,order_type "+
    		 "  ON DUPLICATE KEY UPDATE `product_name` = VALUES(product_name),`color` = VALUES(color),`sales_volume` = VALUES(sales_volume),`sure_sales_volume` =VALUES(sure_sales_volume),`sales` = VALUES(sales),`sure_sales` = VALUES(sure_sales),`real_sales_volume` = VALUES(real_sales_volume),`real_sales` = VALUES(real_sales) ";
       saleReportDao.updateBySql(unlineSql, null);

	}
	
	
	public Map<String,Map<String, SaleReport>> getSalesByCountry(SaleReport saleReport){
		String typeSql="'%Y%m'";
		String sql ="SELECT a.`country`,SUM(a.`sales`),DATE_FORMAT(a.`date`,"+typeSql+") dates FROM amazoninfo_sale_report a  WHERE a.order_type='1' and a.`date`>=:p1 AND a.`date`<=:p2  GROUP BY a.`country`,dates ";
		List<Object[]> list = saleReportDao.findBySql(sql, new Parameter(saleReport.getStart(),saleReport.getEnd()));
		
		Map<String,Map<String, SaleReport>> rs = Maps.newLinkedHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
	     	String temp = country.toUpperCase();
	     	if("UK".equals(temp)){
					temp = "GB";
			}
			if(temp.startsWith("COM")){
					temp = "US";
			}
			CountryCode vatCode = CountryCode.valueOf(temp);
			Float vat=0.19f;
			if(vatCode!=null){
				vat=vatCode.getVat()/100f;
			}
			Float sales = ((BigDecimal)objs[1]).floatValue()*vat;
			String date=objs[2].toString();
			
			
			Map<String, SaleReport> datas = rs.get(date);
			if(datas==null){
				datas = Maps.newLinkedHashMap();
				rs.put(date, datas);
			}
			SaleReport newReport=new SaleReport();
			newReport.setSales(sales);
			datas.put(country, newReport);
			
			
			Map<String, SaleReport> datas1 = rs.get("total");
			if(datas1==null){
				datas1 = Maps.newLinkedHashMap();
				rs.put("total", datas1);
			}
			SaleReport totalReport=datas1.get(country);
			if(totalReport==null){
				SaleReport noReport=new SaleReport();
				noReport.setSales(sales);
				datas1.put(country, noReport);
			}else{
				totalReport.setSales(sales+totalReport.getSales());
				datas1.put(country, totalReport);
			}
			
		
			if ("de,fr,it,es".contains(country)) {
				
				SaleReport newReport2=datas.get("eu");
				if(newReport2==null){
					SaleReport noReport=new SaleReport();
					noReport.setSales(sales);
					datas.put("eu", noReport);
				}else{
					newReport2.setSales(sales+newReport2.getSales());
					datas.put("eu", newReport2);
				}
				
				SaleReport newReport1=datas1.get("eu");
				if(newReport1==null){
					SaleReport noReport=new SaleReport();
					noReport.setSales(sales);
					datas1.put("eu", noReport);
				}else{
					newReport1.setSales(sales+newReport1.getSales());
					datas1.put("eu", newReport1);
				}
				
			}
		}
		return rs;
	}
	
	
	public Map<String,Map<String, SaleReport>> getSalesByCountry2(SaleReport saleReport){
		if(saleReport.getStart()==null){
			Date today = new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			today=DateUtils.addMonths(today,-2);
			saleReport.setStart(DateUtils.getFirstDayOfMonth(today));
			saleReport.setEnd(DateUtils.getLastDayOfMonth(today));
		}
		String date=new SimpleDateFormat("yyyyMM").format(DateUtils.addMonths(saleReport.getStart(),2));
		String sql ="SELECT a.`country`,SUM(a.`sales`) FROM amazoninfo_sale_report a  WHERE a.order_type='1' and a.`date`>=:p1 AND a.`date`<=:p2  GROUP BY a.`country` ";
		List<Object[]> list = saleReportDao.findBySql(sql, new Parameter(saleReport.getStart(),saleReport.getEnd()));
		
		Map<String,Map<String, SaleReport>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
	     	String temp = country.toUpperCase();
	     	if("UK".equals(temp)){
					temp = "GB";
			}
			if(temp.startsWith("COM")){
					temp = "US";
			}
			CountryCode vatCode = CountryCode.valueOf(temp);
			Float vat=0.19f;
			if(vatCode!=null){
				vat=vatCode.getVat()/100f;
			}
			Float sales = ((BigDecimal)objs[1]).floatValue()*vat;
			Map<String, SaleReport> datas = rs.get(country);
			if(datas==null){
				datas = Maps.newLinkedHashMap();
				rs.put(country, datas);
			}
			SaleReport newReport=new SaleReport();
			newReport.setSales(sales);
			datas.put(date, newReport);
		
			if ("de,fr,it,es".contains(country)) {
				Map<String, SaleReport> euData = rs.get("eu");
				if(euData==null){
					euData = Maps.newLinkedHashMap();
					rs.put("eu", euData);
				}
				SaleReport newReport2=euData.get(date);
				if(newReport2==null){
					SaleReport noReport=new SaleReport();
					noReport.setSales(sales);
					euData.put(date, noReport);
				}else{
					newReport2.setSales(sales+newReport2.getSales());
					euData.put(date, newReport2);
				}
			}
		}
		return rs;
	}
	
	
	//国家[日期 /数据]
	public Map<String,Map<String, SaleReport>> getSales(SaleReport saleReport, Map<String, Float> rateRs){
		String currencyType = saleReport.getCurrencyType(); //统计的货币类型（EUR/USD）
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		}
		List<Object[]> list = null;
		if(StringUtils.isEmpty(saleReport.getProductType())){
			String sql ="SELECT a.`country`,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN SUM(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+" ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE sum(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),DATE_FORMAT(a.`date`,"+typeSql+") dates ,SUM(a.`real_sales_volume`*IFNULL(a.`pack_num`,1)), TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) FROM amazoninfo_sale_report a  WHERE a.order_type='1' and a.`date`>=:p1 AND a.`date`<=:p2  GROUP BY a.`country`,dates ORDER BY a.`country`,dates DESC ";
			list = saleReportDao.findBySql(sql, new Parameter(start,end));
		}else{
			String sql ="SELECT a.`country`,SUM(a.`sales_volume`),SUM(a.`sure_sales_volume`),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sure_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sure_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sure_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),DATE_FORMAT(a.`date`,"+typeSql+") dates ,SUM(a.`real_sales_volume`), TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) FROM amazoninfo_sale_report_type a  WHERE a.order_type='1' and  a.`date`>=:p1 AND a.`date`<=:p2 and a.type in ("+saleReport.getProductType()+")  GROUP BY a.`country`,dates ORDER BY a.`country`,dates DESC ";
			list = saleReportDao.findBySql(sql, new Parameter(start,end));
		}
		Map<String,Map<String, SaleReport>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
			Integer salesVolume = ((BigDecimal)objs[1]).intValue();
			Integer sureSalesVolume = ((BigDecimal)objs[2]).intValue();
			Float sales = ((BigDecimal)objs[3]).floatValue();
			Float sureSales = ((BigDecimal)objs[4]).floatValue();
			String date = objs[5].toString(); 
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Integer realSalesVolume = ((BigDecimal)objs[6]).intValue();
			Float realSales = ((BigDecimal)objs[7]).floatValue();
			
			Map<String, SaleReport> datas = rs.get(country);
			if(datas==null){
				datas = Maps.newLinkedHashMap();
				rs.put(country, datas);
			}
			datas.put(date, new SaleReport(sales,sureSales,realSales,salesVolume,sureSalesVolume,realSalesVolume));
			Map<String, SaleReport> datas1 = rs.get("total");
			if(datas1==null){
				datas1 = Maps.newLinkedHashMap();
				rs.put("total", datas1);
			}
			SaleReport saleEntry = datas1.get(date);
			if(saleEntry==null){
				saleEntry = new SaleReport(0f,0f,0f,0,0,0);
				datas1.put(date, saleEntry);
			}
			saleEntry.setSales(sales+saleEntry.getSales());
			saleEntry.setSalesVolume(salesVolume+saleEntry.getSalesVolume());
			saleEntry.setSureSales(sureSales+saleEntry.getSureSales());
			saleEntry.setSureSalesVolume(sureSalesVolume+saleEntry.getSureSalesVolume());
			saleEntry.setRealSales(realSales+saleEntry.getRealSales());
			saleEntry.setRealSalesVolume(realSalesVolume+saleEntry.getRealSalesVolume());	
			
			//欧洲汇总
			if ("de,fr,it,es,uk".contains(country)) {
				Map<String, SaleReport> datas2 = rs.get("eu");
				if(datas2==null){
					datas2 = Maps.newLinkedHashMap();
					rs.put("eu", datas2);
				}
				SaleReport saleEntry1 = datas2.get(date);
				if(saleEntry1==null){
					saleEntry1 = new SaleReport(0f,0f,0f,0,0,0);
					datas2.put(date, saleEntry1);
				}
				saleEntry1.setSales(sales+saleEntry1.getSales());
				saleEntry1.setSalesVolume(salesVolume+saleEntry1.getSalesVolume());
				saleEntry1.setSureSales(sureSales+saleEntry1.getSureSales());
				saleEntry1.setSureSalesVolume(sureSalesVolume+saleEntry1.getSureSalesVolume());
				saleEntry1.setRealSales(realSales+saleEntry1.getRealSales());
				saleEntry1.setRealSalesVolume(realSalesVolume+saleEntry1.getRealSalesVolume());
			}
			
			//英语国家汇总
			if ("com,ca,uk,com1".contains(country)||country.startsWith("com")) {
				Map<String, SaleReport> enData = rs.get("en");
				if(enData==null){
					enData = Maps.newLinkedHashMap();
					rs.put("en", enData);
				}
				SaleReport saleEntryEn = enData.get(date);
				if(saleEntryEn==null){
					saleEntryEn = new SaleReport(0f,0f,0f,0,0,0);
					enData.put(date, saleEntryEn);
				}
				saleEntryEn.setSales(sales+saleEntryEn.getSales());
				saleEntryEn.setSalesVolume(salesVolume+saleEntryEn.getSalesVolume());
				saleEntryEn.setSureSales(sureSales+saleEntryEn.getSureSales());
				saleEntryEn.setSureSalesVolume(sureSalesVolume+saleEntryEn.getSureSalesVolume());
				saleEntryEn.setRealSales(realSales+saleEntryEn.getRealSales());
				saleEntryEn.setRealSalesVolume(realSalesVolume+saleEntryEn.getRealSalesVolume());
			}
			//非英语国家汇总
			if ("de,fr,it,es,jp,".contains(country+",")) {
				Map<String, SaleReport> unEnData = rs.get("unEn");
				if(unEnData==null){
					unEnData = Maps.newLinkedHashMap();
					rs.put("unEn", unEnData);
				}
				SaleReport saleEntryUnEn = unEnData.get(date);
				if(saleEntryUnEn==null){
					saleEntryUnEn = new SaleReport(0f,0f,0f,0,0,0);
					unEnData.put(date, saleEntryUnEn);
				}
				saleEntryUnEn.setSales(sales+saleEntryUnEn.getSales());
				saleEntryUnEn.setSalesVolume(salesVolume+saleEntryUnEn.getSalesVolume());
				saleEntryUnEn.setSureSales(sureSales+saleEntryUnEn.getSureSales());
				saleEntryUnEn.setSureSalesVolume(sureSalesVolume+saleEntryUnEn.getSureSalesVolume());
				saleEntryUnEn.setRealSales(realSales+saleEntryUnEn.getRealSales());
				saleEntryUnEn.setRealSalesVolume(realSalesVolume+saleEntryUnEn.getRealSalesVolume());
			}
		}
		return rs;
	}
	
	/**
	 * 国家/本月销售额     查询当前产品线、当前月，销售总额   
	 */
	public Map<String, Float> getSalesByLine(String curMonth,String productTpye,String currencyType, Map<String, Float> rateRs){
		String typeSql = "'%Y%m'";
		String sql ="SELECT a.`country`,TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
				+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
				+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
				MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2)" 
				+"FROM amazoninfo_sale_report_type a  WHERE a.order_type='1' and DATE_FORMAT(a.`date`,"+typeSql+")=:p1 and a.type in ("+productTpye+")  GROUP BY a.`country` ";
		List<Object[]> 	list = saleReportDao.findBySql(sql,new Parameter(curMonth));
		Map<String,Float> rs = Maps.newHashMap();
		Float total =0f;
		Float euTotal =0f;
		Float enTotal =0f;
		Float unEnTotal=0f;
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
			Float saleAmount = ((BigDecimal)objs[1]).floatValue();
			//欧洲汇总
			if ("de,fr,it,es,uk".contains(country)) {
				euTotal+=saleAmount;
			}
			//英语国家汇总
			if ("com,ca,uk,com1".contains(country)||country.startsWith("com")) {
				enTotal+=saleAmount;
			}
			//非英语国家汇总
			if ("de,fr,it,es,jp,".contains(country+",")) {
				unEnTotal+=saleAmount;
			}
			total+=saleAmount;
			rs.put(country, saleAmount);
		}
		rs.put("total", total);
		rs.put("eu", euTotal);
		rs.put("en", enTotal);
		rs.put("unEn", unEnTotal);
		return rs;
	}
	
	//type-country[日期 /数据]
	public Map<String,Map<String, SaleReport>> getOtherSales(SaleReport saleReport, Map<String, Float> rateRs){
		String currencyType = saleReport.getCurrencyType(); //统计的货币类型（EUR/USD）
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		}
		List<Object[]> list = null;
		if(StringUtils.isEmpty(saleReport.getProductType())){
             String sql ="SELECT concat(a.order_type,'-',a.country),SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) sales_volume,SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)) sure_sales_volume,TRUNCATE((CASE WHEN a.`country` in ('de','de1','de2','de3')||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN SUM(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+" WHEN a.`country`='cn' THEN SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+ " ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) sales,TRUNCATE((CASE WHEN a.`country` in ('de','de1','de2','de3')||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+" WHEN a.`country`='cn' THEN SUM(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+ "  ELSE sum(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) sure_sales,DATE_FORMAT(a.`date`,"+typeSql+") dates ,SUM(a.`real_sales_volume`*IFNULL(a.`pack_num`,1)) real_sales_volume, TRUNCATE((CASE WHEN a.`country` in ('de','de1','de2','de3') ||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+" WHEN a.`country`='cn' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+ "  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) real_sales FROM amazoninfo_sale_report a  WHERE  a.`date`>=:p1 AND a.`date`<=:p2  GROUP BY concat(a.order_type,'-',a.country),dates  ";
			list = saleReportDao.findBySql(sql, new Parameter(start,end));
		}else{
			
			  String sql ="SELECT concat(a.order_type,'-',a.country),SUM(a.`sales_volume`) sales_volume,SUM(a.`sure_sales_volume`) sure_sales_volume,TRUNCATE((CASE WHEN a.`country` in ('de','de1','de2','de3')||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
						MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN SUM(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+" WHEN a.`country`='cn' THEN SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+ " ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) sales,TRUNCATE((CASE WHEN a.`country` in ('de','de1','de2','de3')||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
						MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+" WHEN a.`country`='cn' THEN SUM(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+ "  ELSE sum(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) sure_sales,DATE_FORMAT(a.`date`,"+typeSql+") dates ,SUM(a.`real_sales_volume`) real_sales_volume, TRUNCATE((CASE WHEN a.`country` in ('de','de1','de2','de3') ||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
			            MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+" WHEN a.`country`='cn' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+ "  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) real_sales FROM amazoninfo_sale_report_type a  WHERE a.`date`>=:p1 AND a.`date`<=:p2 and a.type in ("+saleReport.getProductType()+")  GROUP BY concat(a.order_type,'-',a.country),dates ";
			list = saleReportDao.findBySql(sql, new Parameter(start,end));
		}
		Map<String,Map<String, SaleReport>> rs = Maps.newLinkedHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
			Integer salesVolume = ((BigDecimal)objs[1]).intValue();
			Integer sureSalesVolume = ((BigDecimal)objs[2]).intValue();
			Float sales = ((BigDecimal)objs[3]).floatValue();
			Float sureSales = ((BigDecimal)objs[4]).floatValue();
			String date = objs[5].toString(); 
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Integer realSalesVolume = ((BigDecimal)objs[6]).intValue();
			Float realSales = ((BigDecimal)objs[7]).floatValue();
			
			Map<String, SaleReport> datas = rs.get(country);
			if(datas==null){
				datas = Maps.newLinkedHashMap();
				rs.put(country, datas);
			}
			datas.put(date, new SaleReport(sales,sureSales,realSales,salesVolume,sureSalesVolume,realSalesVolume));
			String[] arr=country.split("-");
			if("1".equals(arr[0])){
				Map<String, SaleReport> amazonDatas = rs.get("amazonTotal");
				if(amazonDatas==null){
					amazonDatas = Maps.newLinkedHashMap();
					rs.put("amazonTotal", amazonDatas);
				}
				SaleReport amazonSales = amazonDatas.get(date);
				if(amazonSales==null){
					amazonSales = new SaleReport(0f,0f,0f,0,0,0);
					amazonDatas.put(date, amazonSales);
				}
				amazonSales.setSales(sales+amazonSales.getSales());
				amazonSales.setSalesVolume(salesVolume+amazonSales.getSalesVolume());
				amazonSales.setSureSales(sureSales+amazonSales.getSureSales());
				amazonSales.setSureSalesVolume(sureSalesVolume+amazonSales.getSureSalesVolume());
				amazonSales.setRealSales(realSales+amazonSales.getRealSales());
				amazonSales.setRealSalesVolume(realSalesVolume+amazonSales.getRealSalesVolume());	
			}
			
			
			Map<String, SaleReport> datas1 = rs.get("total");
			if(datas1==null){
				datas1 = Maps.newLinkedHashMap();
				rs.put("total", datas1);
			}
			SaleReport saleEntry = datas1.get(date);
			if(saleEntry==null){
				saleEntry = new SaleReport(0f,0f,0f,0,0,0);
				datas1.put(date, saleEntry);
			}
			saleEntry.setSales(sales+saleEntry.getSales());
			saleEntry.setSalesVolume(salesVolume+saleEntry.getSalesVolume());
			saleEntry.setSureSales(sureSales+saleEntry.getSureSales());
			saleEntry.setSureSalesVolume(sureSalesVolume+saleEntry.getSureSalesVolume());
			saleEntry.setRealSales(realSales+saleEntry.getRealSales());
			saleEntry.setRealSalesVolume(realSalesVolume+saleEntry.getRealSalesVolume());	
		}
		return rs;
	}


	//国家[日期 /数据]
	public Map<String,Map<String, SaleReport>> getOtherSales2(SaleReport saleReport, Map<String, Float> rateRs){
		String currencyType = saleReport.getCurrencyType(); //统计的货币类型（EUR/USD）
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		}
		List<Object[]> list = null;
		if(StringUtils.isEmpty(saleReport.getProductType())){
             String sql ="select a.order_type,sum(sales_volume),sum(sure_sales_volume),sum(sales),sum(sure_sales),dates,sum(real_sales_volume),sum(real_sales) from (SELECT a.order_type,a.country,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) sales_volume,SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)) sure_sales_volume,TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN SUM(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+" WHEN a.`country`='cn' THEN SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+ " ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) sales,TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+" WHEN a.`country`='cn' THEN SUM(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+ "  ELSE sum(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) sure_sales,DATE_FORMAT(a.`date`,"+typeSql+") dates ,SUM(a.`real_sales_volume`*IFNULL(a.`pack_num`,1)) real_sales_volume, TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+" WHEN a.`country`='cn' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+ "  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) real_sales FROM amazoninfo_sale_report a  WHERE  a.`date`>=:p1 AND a.`date`<=:p2  GROUP BY a.order_type,a.country,dates )  a  GROUP BY a.order_type,dates ";
			list = saleReportDao.findBySql(sql, new Parameter(start,end));
		}else{
			
			  String sql ="select  a.order_type,sum(sales_volume),sum(sure_sales_volume),sum(sales),sum(sure_sales),dates,sum(real_sales_volume),sum(real_sales) from (SELECT a.order_type,a.country,SUM(a.`sales_volume`) sales_volume,SUM(a.`sure_sales_volume`) sure_sales_volume,TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
						MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN SUM(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+" WHEN a.`country`='cn' THEN SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+ " ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) sales,TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
						MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+" WHEN a.`country`='cn' THEN SUM(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+ "  ELSE sum(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) sure_sales,DATE_FORMAT(a.`date`,"+typeSql+") dates ,SUM(a.`real_sales_volume`) real_sales_volume, TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
			            MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+" WHEN a.`country`='cn' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+ "  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) real_sales FROM amazoninfo_sale_report_type a  WHERE a.`date`>=:p1 AND a.`date`<=:p2 and a.type in ("+saleReport.getProductType()+")  GROUP BY a.order_type,a.country,dates )  a  GROUP BY a.order_type,dates ";
			list = saleReportDao.findBySql(sql, new Parameter(start,end));
		}
		Map<String,Map<String, SaleReport>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
			Integer salesVolume = ((BigDecimal)objs[1]).intValue();
			Integer sureSalesVolume = ((BigDecimal)objs[2]).intValue();
			Float sales = ((BigDecimal)objs[3]).floatValue();
			Float sureSales = ((BigDecimal)objs[4]).floatValue();
			String date = objs[5].toString(); 
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Integer realSalesVolume = ((BigDecimal)objs[6]).intValue();
			Float realSales = ((BigDecimal)objs[7]).floatValue();
			
			Map<String, SaleReport> datas = rs.get(country);
			if(datas==null){
				datas = Maps.newLinkedHashMap();
				rs.put(country, datas);
			}
			datas.put(date, new SaleReport(sales,sureSales,realSales,salesVolume,sureSalesVolume,realSalesVolume));
			Map<String, SaleReport> datas1 = rs.get("total");
			if(datas1==null){
				datas1 = Maps.newLinkedHashMap();
				rs.put("total", datas1);
			}
			SaleReport saleEntry = datas1.get(date);
			if(saleEntry==null){
				saleEntry = new SaleReport(0f,0f,0f,0,0,0);
				datas1.put(date, saleEntry);
			}
			saleEntry.setSales(sales+saleEntry.getSales());
			saleEntry.setSalesVolume(salesVolume+saleEntry.getSalesVolume());
			saleEntry.setSureSales(sureSales+saleEntry.getSureSales());
			saleEntry.setSureSalesVolume(sureSalesVolume+saleEntry.getSureSalesVolume());
			saleEntry.setRealSales(realSales+saleEntry.getRealSales());
			saleEntry.setRealSalesVolume(realSalesVolume+saleEntry.getRealSalesVolume());	
		}
		return rs;
	}
	
	
	//日期 [国家/数据]
	public Map<String, Map<String,SaleReport>> getSalesBySingleProduct(SaleReport saleReport){
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String currencyType=saleReport.getCurrencyType();
		if(StringUtils.isBlank(currencyType)){
			saleReport.setCurrencyType("EUR");
			currencyType="EUR";
		}
		String typeSql = "'%Y%m%d'";
	if("2".equals(saleReport.getSearchType())){
		if(start==null){
			Date today = new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			start = DateUtils.addWeeks(today, -19);
			end = today;
			saleReport.setStart(start);
			saleReport.setEnd(end);
		}else{
			Date end1 = DateUtils.addWeeks(start, 3);
			if(end.before(end1)){
				end = end1;
				saleReport.setEnd(end1);
			}
		}
		start = DateUtils.getMonday(start);
		end = DateUtils.getSunday(end);
		typeSql="'%x%v'";
	}else if("3".equals(saleReport.getSearchType())){
		if(start==null){
			Date today = new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			start = DateUtils.addMonths(today, -18);
			end = today;
			saleReport.setStart(start);
			saleReport.setEnd(end);
		}else{
			Date end1 = DateUtils.addMonths(start, 3);
			if(end.before(end1)){
				end = end1;
				saleReport.setEnd(end1);
			}
		}
		start = DateUtils.getFirstDayOfMonth(start);
		end = DateUtils.getLastDayOfMonth(end);
		typeSql="'%Y%m'";
	}else{
		if(start==null){
			Date today = new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			start = DateUtils.addMonths(today, -1);
			end = today;
			saleReport.setStart(start);
			saleReport.setEnd(end);
		}else{
			Date end1 = DateUtils.addDays(start, 3);
			if(end.before(end1)){
				end = end1;
				saleReport.setEnd(end1);
			}
		}
	}
	List<Object[]> list = null;
	String sql ="SELECT a.`country`,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
			+MathUtils.getRate("EUR", currencyType,null)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType,null)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
			+MathUtils.getRate("CAD", currencyType,null)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
			MathUtils.getRate("JPY", currencyType,null)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType,null)+" ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType,null)+" END ),2),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
			+MathUtils.getRate("EUR", currencyType,null)+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType,null)+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
			+MathUtils.getRate("CAD", currencyType,null)+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
			MathUtils.getRate("JPY", currencyType,null)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType,null)+" ELSE sum(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType,null)+" END ),2),CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) as product,DATE_FORMAT(a.`date`,"+typeSql+") dates,max(max_order),sum(promotions_order),sum(flash_sales_order),sum(free_order),sum(ads_order),sum(review_volume),sum(support_volume),sum(ams_order),sum(outside_order),sum(ifnull(business_order,0)),sum(coupon) FROM amazoninfo_sale_report a  WHERE a.order_type='1' and a.`date`>=:p1 AND a.`date`<=:p2  and a.`product_name`=:p3 and a.`color`=:p4 GROUP BY a.`country`,product,dates ORDER BY a.`country`,dates DESC ";
	String color = "";
	String name=saleReport.getSku();
	if(StringUtils.isBlank(name)){
		throw new RuntimeException("The product name can not empty");
	}
	if(saleReport.getSku().indexOf("_")>0){
		name=saleReport.getSku().substring(0,saleReport.getSku().lastIndexOf("_"));
		color = saleReport.getSku().substring(saleReport.getSku().lastIndexOf("_")+1);
	}
	list = saleReportDao.findBySql(sql, new Parameter(start,end,name,color));
	Map<String, Map<String,SaleReport>> rs = Maps.newHashMap();
	for (Object[] objs : list) {
		String country = objs[0].toString(); 
		Integer salesVolume = ((BigDecimal)objs[1]).intValue();
		Integer sureSalesVolume = ((BigDecimal)objs[2]).intValue();
		Float sales = Float.valueOf(objs[3]==null?"0":objs[3].toString());
		Float sureSales = Float.valueOf(objs[4]==null?"":objs[4].toString());
		String date = objs[6].toString(); 
		Integer maxOrder=Integer.parseInt(objs[7]==null?"0":objs[7].toString());
		Integer promotionsOrder=Integer.parseInt(objs[8]==null?"0":objs[8].toString());
		Integer flashSalesOrder=Integer.parseInt(objs[9]==null?"0":objs[9].toString());
		Integer freeOrder=Integer.parseInt(objs[10]==null?"0":objs[10].toString());
		Integer adsOrder=Integer.parseInt(objs[11]==null?"0":objs[11].toString());
		Integer reviewVolume=Integer.parseInt(objs[12]==null?"0":objs[12].toString());
		Integer supportVolume=Integer.parseInt(objs[13]==null?"0":objs[13].toString());
		Integer amsOrder=Integer.parseInt(objs[14]==null?"0":objs[14].toString());
		Integer outsideOrder=Integer.parseInt(objs[15]==null?"0":objs[15].toString());
		Integer businessOrder=Integer.parseInt(objs[16]==null?"0":objs[16].toString());
		Integer coupon=Integer.parseInt(objs[17]==null?"0":objs[17].toString());
		
		if(outsideOrder>0){
			promotionsOrder=promotionsOrder-outsideOrder;
		}
		String classType="";
		int num=0;
		if(maxOrder>0){
			num++;
			classType="btn-primary";
		}
		if(promotionsOrder>0||outsideOrder>0||coupon>0){
			num++;
			classType="btn-warning";
		}
		if(flashSalesOrder>0){
			num++;
			classType="btn-danger";
		}
		if(freeOrder>0||reviewVolume>0||supportVolume>0){
			num++;
			classType="btn-info";
		}
		if(adsOrder>0||amsOrder>0){
			num++;
			classType="btn-success";
		}
		if(businessOrder>0){
			num++;
			classType="btn-special";
		}
		if(num>=2){
			classType="btn-inverse";
		}
		if("2".equals(saleReport.getSearchType())){
			Integer i = Integer.parseInt(date.substring(4));
			if(i==53){
				Integer year = Integer.parseInt(date.substring(0,4));
				date =  (year+1)+"01";
			}else if (date.contains("2016")){
				i = i+1;
				date = "2016"+(i<10?("0"+i):i);
			}
		}
		Map<String,SaleReport> countrys = rs.get(date);
		if(countrys==null){
			countrys = Maps.newLinkedHashMap();
			rs.put(date,countrys);
		}
		SaleReport report=new SaleReport(sales,sureSales,salesVolume,sureSalesVolume,maxOrder,promotionsOrder,flashSalesOrder,freeOrder,adsOrder,classType,reviewVolume,supportVolume,amsOrder,outsideOrder);
		report.setBusinessOrder(businessOrder);
		report.setCoupon(coupon);
		countrys.put(country,report);
		
		
		
		SaleReport saleEntry = countrys.get("total");
		if(saleEntry==null){
			saleEntry = new SaleReport(0f,0f,0,0,0,0,0,0,0);
			saleEntry.setReviewVolume(0);
			saleEntry.setSupportVolume(0);
			saleEntry.setAmsOrder(0);
			saleEntry.setOutsideOrder(0);
			saleEntry.setBusinessOrder(0);
			saleEntry.setCoupon(0);
			countrys.put("total", saleEntry);
		}
			saleEntry.setSales(sales+saleEntry.getSales());
			saleEntry.setSalesVolume(salesVolume+saleEntry.getSalesVolume());
			saleEntry.setSureSales(sureSales+saleEntry.getSureSales());
			saleEntry.setSureSalesVolume(sureSalesVolume+saleEntry.getSureSalesVolume());
			if(maxOrder>saleEntry.getMaxOrder()){
				saleEntry.setMaxOrder(maxOrder);
			}
			saleEntry.setPromotionsOrder(promotionsOrder+saleEntry.getPromotionsOrder());
			saleEntry.setFlashSalesOrder(flashSalesOrder+saleEntry.getFlashSalesOrder());
			saleEntry.setFreeOrder(freeOrder+saleEntry.getFreeOrder());
			saleEntry.setAdsOrder(adsOrder+saleEntry.getAdsOrder());
			saleEntry.setAmsOrder(amsOrder+saleEntry.getAmsOrder());
			saleEntry.setOutsideOrder(outsideOrder+saleEntry.getOutsideOrder());
			saleEntry.setReviewVolume(reviewVolume+saleEntry.getReviewVolume());
			saleEntry.setSupportVolume(supportVolume+saleEntry.getSupportVolume());
			saleEntry.setBusinessOrder(businessOrder+saleEntry.getBusinessOrder());
			saleEntry.setCoupon(coupon+saleEntry.getCoupon());
			int num2=0;
			String classType2="";
			if(saleEntry.getMaxOrder()>0){
				num2++;
				classType2="btn-primary";
			}
			if(saleEntry.getPromotionsOrder()>0||saleEntry.getOutsideOrder()>0||saleEntry.getCoupon()>0){
				num2++;
				classType2="btn-warning";
			}
			if(saleEntry.getFlashSalesOrder()>0){
				num2++;
				classType2="btn-danger";
			}
			if(saleEntry.getFreeOrder()>0||saleEntry.getReviewVolume()>0||saleEntry.getSupportVolume()>0){
				num2++;
				classType2="btn-info";
			}
			if(saleEntry.getAdsOrder()>0||saleEntry.getAmsOrder()>0){
				num2++;
				classType2="btn-success";
			}
			if(saleEntry.getBusinessOrder()>0){
				num2++;
				classType2="btn-special";
			}
			if(num2>=2){
				classType2="btn-inverse";
			}
			saleEntry.setClassType(classType2);
			
			
			if("de,fr,it,es,uk".contains(country)){
				SaleReport euSaleEntry = countrys.get("eu");
				if(euSaleEntry==null){
					euSaleEntry = new SaleReport(0f,0f,0,0,0,0,0,0,0);
					euSaleEntry.setReviewVolume(0);
					euSaleEntry.setSupportVolume(0);
					euSaleEntry.setAmsOrder(0);
					euSaleEntry.setOutsideOrder(0);
					euSaleEntry.setBusinessOrder(0);
					euSaleEntry.setCoupon(0);
					countrys.put("eu", euSaleEntry);
				}
				euSaleEntry.setSales(sales+euSaleEntry.getSales());
				euSaleEntry.setSalesVolume(salesVolume+euSaleEntry.getSalesVolume());
				euSaleEntry.setSureSales(sureSales+euSaleEntry.getSureSales());
				euSaleEntry.setSureSalesVolume(sureSalesVolume+euSaleEntry.getSureSalesVolume());
					if(maxOrder>euSaleEntry.getMaxOrder()){
						euSaleEntry.setMaxOrder(maxOrder);
					}
					euSaleEntry.setPromotionsOrder(promotionsOrder+euSaleEntry.getPromotionsOrder());
					euSaleEntry.setFlashSalesOrder(flashSalesOrder+euSaleEntry.getFlashSalesOrder());
					euSaleEntry.setFreeOrder(freeOrder+euSaleEntry.getFreeOrder());
					euSaleEntry.setAdsOrder(adsOrder+euSaleEntry.getAdsOrder());
					euSaleEntry.setAmsOrder(amsOrder+euSaleEntry.getAmsOrder());
					euSaleEntry.setReviewVolume(reviewVolume+euSaleEntry.getReviewVolume());
					euSaleEntry.setSupportVolume(supportVolume+euSaleEntry.getSupportVolume());
					euSaleEntry.setOutsideOrder(outsideOrder+euSaleEntry.getOutsideOrder());
					euSaleEntry.setBusinessOrder(businessOrder+euSaleEntry.getBusinessOrder());
					euSaleEntry.setCoupon(coupon+euSaleEntry.getCoupon());
					int num3=0;
					String classType3="";
					if(euSaleEntry.getMaxOrder()>0){
						num3++;
						classType3="btn-primary";
					}
					if(euSaleEntry.getPromotionsOrder()>0||euSaleEntry.getOutsideOrder()>0||euSaleEntry.getCoupon()>0){
						num3++;
						classType3="btn-warning";
					}
					if(euSaleEntry.getFlashSalesOrder()>0){
						num3++;
						classType3="btn-danger";
					}
					if(euSaleEntry.getFreeOrder()>0||euSaleEntry.getReviewVolume()>0||euSaleEntry.getSupportVolume()>0){
						num3++;
						classType3="btn-info";
					}
					if(euSaleEntry.getAdsOrder()>0||euSaleEntry.getAmsOrder()>0){
						num3++;
						classType3="btn-success";
					}
					if(euSaleEntry.getBusinessOrder()>0){
						num3++;
						classType3="btn-special";
					}
					if(num3>=2){
						classType3="btn-inverse";
					}
					euSaleEntry.setClassType(classType3);
			}
			
		}
		return rs;
	}
	
	
	public Map<String, Map<String,SaleReport>> getSalesByUnionProduct(SaleReport saleReport,Set<String> nameList,String color){
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String currencyType=saleReport.getCurrencyType();
		if(StringUtils.isBlank(currencyType)){
			saleReport.setCurrencyType("EUR");
			currencyType="EUR";
		}
		String typeSql = "'%Y%m%d'";
	  if("2".equals(saleReport.getSearchType())){
		if(start==null){
			Date today = new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			start = DateUtils.addWeeks(today, -19);
			end = today;
			saleReport.setStart(start);
			saleReport.setEnd(end);
		}else{
			Date end1 = DateUtils.addWeeks(start, 3);
			if(end.before(end1)){
				end = end1;
				saleReport.setEnd(end1);
			}
		}
		start = DateUtils.getMonday(start);
		end = DateUtils.getSunday(end);
		typeSql="'%x%v'";
	}else if("3".equals(saleReport.getSearchType())){
		if(start==null){
			Date today = new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			start = DateUtils.addMonths(today, -18);
			end = today;
			saleReport.setStart(start);
			saleReport.setEnd(end);
		}else{
			Date end1 = DateUtils.addMonths(start, 3);
			if(end.before(end1)){
				end = end1;
				saleReport.setEnd(end1);
			}
		}
		start = DateUtils.getFirstDayOfMonth(start);
		end = DateUtils.getLastDayOfMonth(end);
		typeSql="'%Y%m'";
	}else{
		if(start==null){
			Date today = new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			start = DateUtils.addMonths(today, -1);
			end = today;
			saleReport.setStart(start);
			saleReport.setEnd(end);
		}else{
			Date end1 = DateUtils.addDays(start, 3);
			if(end.before(end1)){
				end = end1;
				saleReport.setEnd(end1);
			}
		}
	}
	List<Object[]> list = null;
	String sql="";
	if(StringUtils.isBlank(color)){
		sql ="SELECT a.`country`,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
				+MathUtils.getRate("EUR", currencyType,null)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType,null)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
				+MathUtils.getRate("CAD", currencyType,null)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
				MathUtils.getRate("JPY", currencyType,null)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType,null)+" ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType,null)+" END ),2),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
				+MathUtils.getRate("EUR", currencyType,null)+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType,null)+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
				+MathUtils.getRate("CAD", currencyType,null)+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
				MathUtils.getRate("JPY", currencyType,null)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType,null)+" ELSE sum(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType,null)+" END ),2),DATE_FORMAT(a.`date`,"+typeSql+") dates,max(max_order),sum(promotions_order),sum(flash_sales_order),sum(free_order),sum(ads_order),sum(review_volume),sum(support_volume),sum(ams_order),sum(outside_order),sum(coupon) FROM amazoninfo_sale_report a  WHERE a.order_type='1' and a.`date`>=:p1 AND a.`date`<=:p2  and a.`product_name` in :p3  GROUP BY a.`country`,dates ORDER BY a.`country`,dates DESC ";
		
		list = saleReportDao.findBySql(sql, new Parameter(start,end,nameList));
	}else{
		sql ="SELECT a.`country`,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
				+MathUtils.getRate("EUR", currencyType,null)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType,null)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
				+MathUtils.getRate("CAD", currencyType,null)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
				MathUtils.getRate("JPY", currencyType,null)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType,null)+" ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType,null)+" END ),2),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
				+MathUtils.getRate("EUR", currencyType,null)+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType,null)+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
				+MathUtils.getRate("CAD", currencyType,null)+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
				MathUtils.getRate("JPY", currencyType,null)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType,null)+" ELSE sum(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType,null)+" END ),2),DATE_FORMAT(a.`date`,"+typeSql+") dates,max(max_order),sum(promotions_order),sum(flash_sales_order),sum(free_order),sum(ads_order),sum(review_volume),sum(support_volume),sum(ams_order),sum(outside_order),sum(coupon) FROM amazoninfo_sale_report a  WHERE a.order_type='1' and a.`date`>=:p1 AND a.`date`<=:p2  and a.`product_name` in :p3 and a.`color`=:p4 GROUP BY a.`country`,dates ORDER BY a.`country`,dates DESC ";
		
		list = saleReportDao.findBySql(sql, new Parameter(start,end,nameList,color));
	}
	Map<String, Map<String,SaleReport>> rs = Maps.newHashMap();
	for (Object[] objs : list) {
		String country = objs[0].toString(); 
		Integer salesVolume = ((BigDecimal)objs[1]).intValue();
		Integer sureSalesVolume = ((BigDecimal)objs[2]).intValue();
		Float sales = Float.valueOf(objs[3]==null?"0":objs[3].toString());
		Float sureSales = Float.valueOf(objs[4]==null?"":objs[4].toString());
		String date = objs[5].toString(); 
		Integer maxOrder=Integer.parseInt(objs[6]==null?"0":objs[6].toString());
		Integer promotionsOrder=Integer.parseInt(objs[7]==null?"0":objs[7].toString());
		Integer flashSalesOrder=Integer.parseInt(objs[8]==null?"0":objs[8].toString());
		Integer freeOrder=Integer.parseInt(objs[9]==null?"0":objs[9].toString());
		Integer adsOrder=Integer.parseInt(objs[10]==null?"0":objs[10].toString());
		Integer reviewVolume=Integer.parseInt(objs[11]==null?"0":objs[11].toString());
		Integer supportVolume=Integer.parseInt(objs[12]==null?"0":objs[12].toString());
		Integer amsOrder=Integer.parseInt(objs[13]==null?"0":objs[13].toString());
		Integer outsideOrder=Integer.parseInt(objs[14]==null?"0":objs[14].toString());
		Integer coupon=Integer.parseInt(objs[15]==null?"0":objs[15].toString());
		if(outsideOrder>0){
			promotionsOrder=promotionsOrder-outsideOrder;
		}
		String classType="";
		int num=0;
		if(maxOrder>0){
			num++;
			classType="btn-primary";
		}
		if(promotionsOrder>0||outsideOrder>0||coupon>0){
			num++;
			classType="btn-warning";
		}
		if(flashSalesOrder>0){
			num++;
			classType="btn-danger";
		}
		if(freeOrder>0||reviewVolume>0||supportVolume>0){
			num++;
			classType="btn-info";
		}
		if(adsOrder>0||amsOrder>0){
			num++;
			classType="btn-success";
		}
		if(num>=2){
			classType="btn-inverse";
		}
		if("2".equals(saleReport.getSearchType())){
			Integer i = Integer.parseInt(date.substring(4));
			if(i==53){
				Integer year = Integer.parseInt(date.substring(0,4));
				date =  (year+1)+"01";
			}else if (date.contains("2016")){
				i = i+1;
				date = "2016"+(i<10?("0"+i):i);
			}
		}
		Map<String,SaleReport> countrys = rs.get(date);
		if(countrys==null){
			countrys = Maps.newLinkedHashMap();
			rs.put(date,countrys);
		}
		SaleReport temp = new SaleReport(sales,sureSales,salesVolume,sureSalesVolume,maxOrder,promotionsOrder,flashSalesOrder,freeOrder,adsOrder,classType,reviewVolume,supportVolume,amsOrder,outsideOrder);
		temp.setCoupon(coupon);
		countrys.put(country,temp );
		
		
		
		SaleReport saleEntry = countrys.get("total");
		if(saleEntry==null){
			saleEntry = new SaleReport(0f,0f,0,0,0,0,0,0,0);
			saleEntry.setReviewVolume(0);
			saleEntry.setSupportVolume(0);
			saleEntry.setAmsOrder(0);
			saleEntry.setCoupon(coupon);
			saleEntry.setOutsideOrder(0);
			countrys.put("total", saleEntry);
		}
			saleEntry.setSales(sales+saleEntry.getSales());
			saleEntry.setSalesVolume(salesVolume+saleEntry.getSalesVolume());
			saleEntry.setSureSales(sureSales+saleEntry.getSureSales());
			saleEntry.setSureSalesVolume(sureSalesVolume+saleEntry.getSureSalesVolume());
			if(maxOrder>saleEntry.getMaxOrder()){
				saleEntry.setMaxOrder(maxOrder);
			}
			saleEntry.setPromotionsOrder(promotionsOrder+saleEntry.getPromotionsOrder());
			saleEntry.setFlashSalesOrder(flashSalesOrder+saleEntry.getFlashSalesOrder());
			saleEntry.setFreeOrder(freeOrder+saleEntry.getFreeOrder());
			saleEntry.setAdsOrder(adsOrder+saleEntry.getAdsOrder());
			saleEntry.setAmsOrder(amsOrder+saleEntry.getAmsOrder());
			saleEntry.setOutsideOrder(outsideOrder+saleEntry.getOutsideOrder());
			saleEntry.setReviewVolume(reviewVolume+saleEntry.getReviewVolume());
			saleEntry.setSupportVolume(supportVolume+saleEntry.getSupportVolume());
			saleEntry.setCoupon(coupon+saleEntry.getCoupon());
			
			int num2=0;
			String classType2="";
			if(saleEntry.getMaxOrder()>0){
				num2++;
				classType2="btn-primary";
			}
			if(saleEntry.getPromotionsOrder()>0||saleEntry.getOutsideOrder()>0||saleEntry.getCoupon()>0){
				num2++;
				classType2="btn-warning";
			}
			if(saleEntry.getFlashSalesOrder()>0){
				num2++;
				classType2="btn-danger";
			}
			if(saleEntry.getFreeOrder()>0||saleEntry.getReviewVolume()>0||saleEntry.getSupportVolume()>0){
				num2++;
				classType2="btn-info";
			}
			if(saleEntry.getAdsOrder()>0||saleEntry.getAmsOrder()>0){
				num2++;
				classType2="btn-success";
			}
			if(num2>=2){
				classType2="btn-inverse";
			}
			saleEntry.setClassType(classType2);
			
			
			if("de,fr,it,es,uk".contains(country)){
				SaleReport euSaleEntry = countrys.get("eu");
				if(euSaleEntry==null){
					euSaleEntry = new SaleReport(0f,0f,0,0,0,0,0,0,0);
					euSaleEntry.setReviewVolume(0);
					euSaleEntry.setSupportVolume(0);
					euSaleEntry.setAmsOrder(0);
					euSaleEntry.setOutsideOrder(0);
					euSaleEntry.setCoupon(0);
					countrys.put("eu", euSaleEntry);
				}
				euSaleEntry.setSales(sales+euSaleEntry.getSales());
				euSaleEntry.setSalesVolume(salesVolume+euSaleEntry.getSalesVolume());
				euSaleEntry.setSureSales(sureSales+euSaleEntry.getSureSales());
				euSaleEntry.setSureSalesVolume(sureSalesVolume+euSaleEntry.getSureSalesVolume());
					if(maxOrder>euSaleEntry.getMaxOrder()){
						euSaleEntry.setMaxOrder(maxOrder);
					}
					euSaleEntry.setPromotionsOrder(promotionsOrder+euSaleEntry.getPromotionsOrder());
					euSaleEntry.setFlashSalesOrder(flashSalesOrder+euSaleEntry.getFlashSalesOrder());
					euSaleEntry.setFreeOrder(freeOrder+euSaleEntry.getFreeOrder());
					euSaleEntry.setAdsOrder(adsOrder+euSaleEntry.getAdsOrder());
					euSaleEntry.setAmsOrder(amsOrder+euSaleEntry.getAmsOrder());
					euSaleEntry.setReviewVolume(reviewVolume+euSaleEntry.getReviewVolume());
					euSaleEntry.setSupportVolume(supportVolume+euSaleEntry.getSupportVolume());
					euSaleEntry.setOutsideOrder(outsideOrder+euSaleEntry.getOutsideOrder());
					euSaleEntry.setCoupon(coupon+euSaleEntry.getCoupon());
					
					int num3=0;
					String classType3="";
					if(euSaleEntry.getMaxOrder()>0){
						num3++;
						classType3="btn-primary";
					}
					if(euSaleEntry.getPromotionsOrder()>0||euSaleEntry.getOutsideOrder()>0||euSaleEntry.getCoupon()>0){
						num3++;
						classType3="btn-warning";
					}
					if(euSaleEntry.getFlashSalesOrder()>0){
						num3++;
						classType3="btn-danger";
					}
					if(euSaleEntry.getFreeOrder()>0||euSaleEntry.getReviewVolume()>0||euSaleEntry.getSupportVolume()>0){
						num3++;
						classType3="btn-info";
					}
					if(euSaleEntry.getAdsOrder()>0||euSaleEntry.getAmsOrder()>0){
						num3++;
						classType3="btn-success";
					}
					if(num3>=2){
						classType3="btn-inverse";
					}
					euSaleEntry.setClassType(classType3);
			}
			
		}
		return rs;
	}


	//日期 [国家/type]
	public Map<String, Map<String,Map<String,Integer>>> getSalesTypeBySingleProduct(SaleReport saleReport){
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		}
		List<Object[]> list = null;
		String sql="SELECT a.dates,a.country,a.type,(CASE WHEN a.type='3' THEN MAX(a.quantity) ELSE SUM(a.quantity) END) FROM (SELECT DATE_FORMAT(a.`purchase_date`,"+typeSql+") dates,a.`sales_channel` AS country,CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN CONCAT ('_',b.`color`) ELSE '' END) AS product, "+ 
        " (CASE WHEN (b.`promotion_ids` LIKE '%,F-%' or b.`promotion_ids` LIKE 'F-%') THEN '1' WHEN (b.`promotion_ids` IS NULL AND b.`promotion_discount` >0) THEN '2' WHEN (SUM(b.quantity_ordered)>=10) THEN '3' ELSE '4' END) TYPE,SUM(b.quantity_ordered) quantity "+
        " FROM  amazoninfo_order a JOIN amazoninfo_orderitem b ON a.id=b.order_id "+				
        " WHERE a.`purchase_date`>=:p1 AND a.`purchase_date`<=:p2   AND CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN CONCAT ('_',b.`color`) ELSE '' END)=:p3 AND  "+
        " a.order_status IN ('Shipped','Pending','Unshipped') "+
        " GROUP BY a.id,product,country,dates,b.`promotion_ids`,b.`promotion_discount` HAVING TYPE!='4') a GROUP BY a.dates,a.country,a.type ORDER BY a.dates,a.country ";
		list = saleReportDao.findBySql(sql, new Parameter(start,end,saleReport.getSku()));
		Map<String, Map<String,Map<String,Integer>>> rs = Maps.newHashMap();
		for (Object[] obj: list) {
			String date=obj[0].toString();
			String country=obj[1].toString().substring(obj[1].toString().lastIndexOf(".")+1);
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Map<String,Map<String,Integer>> map=rs.get(date);
			if(map==null){
				map=Maps.newHashMap();
				rs.put(date,map);
			}
			Map<String,Integer> temp=map.get(country);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(country,temp);
			}
			temp.put(obj[2].toString(),Integer.parseInt(obj[3].toString()));
		}
		return rs;
	}
	
	//产品名/type
	public Map<String,Map<String,Integer>> getSalesType(String dateStr,String byTime,String country){
		Date start = null;
		Date end = null;
		String typeSql = "'%Y%m%d'";
		if("1".equals(byTime)){
			try {
				start = format.parse(dateStr);
				end = DateUtils.addDays(start, 1);
			} catch (ParseException e) {}
		}else if("2".equals(byTime)){
			int year = Integer.parseInt(dateStr.substring(0,4));
			int week = Integer.parseInt(dateStr.substring(4));
			start = DateUtils.getFirstDayOfWeek(year, week);
			end = DateUtils.getLastDayOfWeek(year, week);
			end = DateUtils.addDays(end, 1);
			typeSql="'%x%v'";
		}else if("3".equals(byTime)){
			int year = Integer.parseInt(dateStr.substring(0,4));
			int month = Integer.parseInt(dateStr.substring(4))-1;
			try {
				start = DateUtils.getFirstDayOfMonth(year, month);
				end = DateUtils.getLastDayOfMonth(year, month);
				end = DateUtils.addDays(end, 1);
			} catch (ParseException e) {}
			typeSql="'%Y%m'";
		}
		String temp = "";
		if(!"total".equals(country) && !"eu".equals(country)){
			temp = "  and  a.`sales_channel` like '%"+country+"%'";
		} else if ("eu".equals(country)) {
			temp = "  and  a.`sales_channel` in ('Amazon.de','Amazon.co.uk','Amazon.es','Amazon.fr','Amazon.it') ";
		}
		List<Object[]> list = null;
		String sql="SELECT a.type,(CASE WHEN a.type='3' THEN MAX(a.quantity) ELSE SUM(a.quantity) END),a.product FROM (SELECT DATE_FORMAT(a.`purchase_date`,"+typeSql+") dates,REPLACE(REPLACE(REPLACE(a.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') AS country,CONCAT((case when b.`product_name` is null then CONCAT(b.sellersku,'(未匹配)') else b.`product_name` end ),CASE  WHEN b.`color`='' || b.`color` is null THEN '' ELSE CONCAT('_',b.`color`) END) AS product, "+ 
        " (CASE WHEN (b.`promotion_ids` LIKE 'F-%' or b.`promotion_ids` LIKE '%,F-%') THEN '1' WHEN (b.`promotion_ids` IS NULL AND b.`promotion_discount` >0) THEN '2' WHEN (SUM(b.quantity_ordered)>=10) THEN '3' ELSE '4' END) TYPE,SUM(b.quantity_ordered) quantity "+
        " FROM  amazoninfo_order a JOIN amazoninfo_orderitem b ON a.id=b.order_id "+				
        " WHERE a.`purchase_date`>=:p1 AND a.`purchase_date`<=:p2   AND  "+
        " a.order_status IN ('Shipped','Pending','Unshipped')  "+ temp+
        " GROUP BY a.id,product,b.`promotion_ids`,b.`promotion_discount` HAVING TYPE!='4') a GROUP BY a.product,a.type  ";
		list = saleReportDao.findBySql(sql, new Parameter(start,end));
		Map<String,Map<String,Integer>> rs = Maps.newHashMap();
		for (Object[] obj: list) {
			if(obj[2]==null){
				continue;
			}
			String name = obj[2].toString();
			Map<String,Integer> map=rs.get(name);
			if(map==null){
				map=Maps.newHashMap();
				rs.put(name,map);
			}
			map.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
		}
		return rs;
	}

	
	

	//日期 [orderType/数据]
	public Map<String, Map<String,SaleReport>> getSalesBySingleProduct2(SaleReport saleReport){
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String currencyType=saleReport.getCurrencyType();
		if(StringUtils.isBlank(currencyType)){
			saleReport.setCurrencyType("EUR");
			currencyType="EUR";
		}
		String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		}
		List<Object[]> list = null;
		String sql ="SELECT concat(a.order_type,'-',a.`country`),SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) sales_volume,SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)) sure_sales_volume,TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
				+MathUtils.getRate("EUR", currencyType,null)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType,null)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
				+MathUtils.getRate("CAD", currencyType,null)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
				MathUtils.getRate("JPY", currencyType,null)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType,null)+" ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType,null)+" END ),2) sales,TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
				+MathUtils.getRate("EUR", currencyType,null)+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType,null)+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
				+MathUtils.getRate("CAD", currencyType,null)+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
				MathUtils.getRate("JPY", currencyType,null)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType,null)+"  ELSE sum(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType,null)+" END ),2) sure_sales,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) as product,DATE_FORMAT(a.`date`,"+typeSql+") dates FROM amazoninfo_sale_report a  WHERE a.`date`>=:p1 AND a.`date`<=:p2  and a.`product_name`=:p3 and a.`color`=:p4 GROUP BY concat(a.order_type,'-',a.`country`),product,dates ";
		String color = "";
		String name=saleReport.getSku();
		if(saleReport.getSku().indexOf("_")>0){
			name=saleReport.getSku().substring(0,saleReport.getSku().lastIndexOf("_"));
			color = saleReport.getSku().substring(saleReport.getSku().lastIndexOf("_")+1);
		}
		list = saleReportDao.findBySql(sql, new Parameter(start,end,name,color));
		Map<String, Map<String,SaleReport>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
			Integer salesVolume = ((BigDecimal)objs[1]).intValue();
			Integer sureSalesVolume = ((BigDecimal)objs[2]).intValue();
			Float sales = Float.valueOf(objs[3]==null?"0":objs[3].toString());
			Float sureSales = Float.valueOf(objs[4]==null?"":objs[4].toString());
			String date = objs[6].toString(); 
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Map<String,SaleReport> countrys = rs.get(date);
			if(countrys==null){
				countrys = Maps.newLinkedHashMap();
				rs.put(date,countrys);
			}
			countrys.put(country, new SaleReport(sales,sureSales,salesVolume,sureSalesVolume));
			
			SaleReport saleEntry = countrys.get("total");
			if(saleEntry==null){
				saleEntry = new SaleReport(0f,0f,0,0);
				countrys.put("total", saleEntry);
			}
			saleEntry.setSales(sales+saleEntry.getSales());
			saleEntry.setSalesVolume(salesVolume+saleEntry.getSalesVolume());
			saleEntry.setSureSales(sureSales+saleEntry.getSureSales());
			saleEntry.setSureSalesVolume(sureSalesVolume+saleEntry.getSureSalesVolume());
		}
		return rs;
	}

	public Map<String, Map<String,Float>> getSalesBydat(Date start,Date end, String currencyType, Map<String, Float> rateRs,String lineType){
		String typeSql = "'%Y%m%d'";
		if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
		}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
				}
		}
		List<Object[]> list = null;
		String sql ="SELECT a.`country`,"+
			   "TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sales`)*"+
				+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN sum(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN sum(a.`sales`)*"
				+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN sum(a.`sales`)*"+
				MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE sum(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),DATE_FORMAT(a.`date`,"+typeSql+") dates FROM amazoninfo_sale_report_type a  ";
		if(!"total".equals(lineType)){
			sql+="  JOIN sys_dict d ON d.`value`=a.`type` AND d.del_flag='0' AND d.type='product_type' "+
				// " left join sys_dict d on d.value=a.type and d.type='product_type' and d.del_flag='0' "+
				 "  join psi_product_type_group g on g.dict_id=d.id "+
				 "  join psi_product_type_dict t on g.id=t.id and t.del_flag='0' ";
		}
		sql+=" WHERE a.order_type='1' and a.`date`>=:p1 AND a.`date`<=:p2   ";
		if(!"total".equals(lineType)){
			if(!"unGrouped".equals(lineType)){
        		sql+=" and g.id='"+lineType+"' ";
        	}else{
        		sql+=" and g.id is null";
        	}
		}
		sql+=" GROUP BY a.`country`,dates ORDER BY dates";
		list = saleReportDao.findBySql(sql, new Parameter(start,end));
		Map<String, Map<String,Float>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
			Float sales = ((BigDecimal)objs[1]).floatValue();
			String date = objs[2].toString(); 
			Map<String,Float> countrys = rs.get(date);
			if(countrys==null){
				countrys = Maps.newLinkedHashMap();
				rs.put(date,countrys);
			}
			countrys.put(country, sales);
		}
		return rs;
	}
		
	public Map<String,Map<String, Map<String,SaleReport>>> getSalesByProduct(SaleReport saleReport, Map<String, Float> rateRs){
		String currencyType = saleReport.getCurrencyType(); //统计的货币类型（EUR/USD）
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		}
		List<Object[]> list = null;
		if(StringUtils.isEmpty(saleReport.getProductType())){
			String sql ="SELECT a.`country`,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)),round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE sum(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),(CASE when a.`product_name` is null  then 'noProductName'  else CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) end) product,DATE_FORMAT(a.`date`,"+typeSql+") dates,SUM(a.`real_sales_volume`*IFNULL(a.`pack_num`,1)), " +
					"round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) FROM amazoninfo_sale_report a  WHERE a.order_type='1' and  a.`date`>=:p1 AND a.`date`<=:p2  GROUP BY a.`country`,product,dates ORDER BY a.`country`,dates DESC ";
			list = saleReportDao.findBySql(sql, new Parameter(start,end));
		}else{
			String sql ="SELECT a.`country`,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)),round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sure_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sure_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sure_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) product,DATE_FORMAT(a.`date`,"+typeSql+") dates,SUM(a.`real_sales_volume`*IFNULL(a.`pack_num`,1)), " +
					"round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) FROM amazoninfo_sale_report a " +
					" join psi_product p on a.`product_name`=CONCAT(p.`brand`,' ',p.`model`) and p.del_flag='0' " +
					" WHERE a.order_type='1' and  a.`date`>=:p1 AND a.`date`<=:p2 and p.type in ("+saleReport.getProductType()+")  GROUP BY a.`country`,product,dates ORDER BY a.`country`,dates DESC ";
		   list = saleReportDao.findBySql(sql, new Parameter(start,end));
		}
		Map<String,Map<String, Map<String,SaleReport>>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
			Integer salesVolume = ((BigDecimal)(objs[1]==null?new BigDecimal(0):objs[1])).intValue();
			Integer sureSalesVolume = ((BigDecimal)(objs[2]==null?new BigDecimal(0):objs[2])).intValue();
			Float sales = ((BigDecimal)(objs[3]==null?new BigDecimal(0):objs[3])).floatValue();
			Float sureSales = ((BigDecimal)(objs[4]==null?new BigDecimal(0):objs[4])).floatValue();
			String productName = (objs[5]==null)?"":objs[5].toString(); 
			String date = objs[6].toString(); 
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Integer realSalesVolume = ((BigDecimal)(objs[7]==null?new BigDecimal(0):objs[7])).intValue();
			Float realSales = ((BigDecimal)(objs[8]==null?new BigDecimal(0):objs[8])).floatValue();
			Map<String, Map<String,SaleReport>> datas = rs.get(country);
			if(datas==null){
				datas = Maps.newLinkedHashMap();
				rs.put(country, datas);
			}
			Map<String, SaleReport> sale=datas.get(date);
			if(sale==null){
				sale=Maps.newLinkedHashMap();
				datas.put(date, sale);
			}
			sale.put(productName,  new SaleReport(sales,sureSales,realSales,salesVolume,sureSalesVolume,realSalesVolume));
			
			//欧洲汇总
			if ("de,fr,it,es,uk".contains(country)) {
				Map<String, Map<String,SaleReport>> datas2 = rs.get("eu");
				if(datas2==null){
					datas2 = Maps.newLinkedHashMap();
					rs.put("eu", datas2);
				}
				Map<String, SaleReport>  dateMap = datas2.get(date);
				if(dateMap==null){
					dateMap = Maps.newLinkedHashMap();
					datas2.put(date, dateMap);
				}
				SaleReport report=dateMap.get(productName);
				if(report==null){
					report = new SaleReport(0f,0f,0f,0,0,0);
					dateMap.put(productName, report);
				}
				report.setSales(sales+report.getSales());
				report.setSalesVolume(salesVolume+report.getSalesVolume());
				report.setSureSales(sureSales+report.getSureSales());
				report.setSureSalesVolume(sureSalesVolume+report.getSureSalesVolume());
				report.setRealSales(realSales+report.getRealSales());
				report.setRealSalesVolume(realSalesVolume+report.getRealSalesVolume());
			}
			
			//英语国家汇总
			if ("com,ca,uk,com1".contains(country)||country.startsWith("com")) {
				Map<String, Map<String,SaleReport>> datas2 = rs.get("en");
				if(datas2==null){
					datas2 = Maps.newLinkedHashMap();
					rs.put("en", datas2);
				}
				Map<String, SaleReport>  dateMap = datas2.get(date);
				if(dateMap==null){
					dateMap = Maps.newLinkedHashMap();
					datas2.put(date, dateMap);
				}
				SaleReport report=dateMap.get(productName);
				if(report==null){
					report = new SaleReport(0f,0f,0f,0,0,0);
					dateMap.put(productName, report);
				}
				report.setSales(sales+report.getSales());
				report.setSalesVolume(salesVolume+report.getSalesVolume());
				report.setSureSales(sureSales+report.getSureSales());
				report.setSureSalesVolume(sureSalesVolume+report.getSureSalesVolume());
				report.setRealSales(realSales+report.getRealSales());
				report.setRealSalesVolume(realSalesVolume+report.getRealSalesVolume());
			}
			
			//非英语国家汇总
			if ("de,fr,it,es,jp".contains(country)) {
				Map<String, Map<String,SaleReport>> datas2 = rs.get("unEn");
				if(datas2==null){
					datas2 = Maps.newLinkedHashMap();
					rs.put("unEn", datas2);
				}
				Map<String, SaleReport>  dateMap = datas2.get(date);
				if(dateMap==null){
					dateMap = Maps.newLinkedHashMap();
					datas2.put(date, dateMap);
				}
				SaleReport report=dateMap.get(productName);
				if(report==null){
					report = new SaleReport(0f,0f,0f,0,0,0);
					dateMap.put(productName, report);
				}
				report.setSales(sales+report.getSales());
				report.setSalesVolume(salesVolume+report.getSalesVolume());
				report.setSureSales(sureSales+report.getSureSales());
				report.setSureSalesVolume(sureSalesVolume+report.getSureSalesVolume());
				report.setRealSales(realSales+report.getRealSales());
				report.setRealSalesVolume(realSalesVolume+report.getRealSalesVolume());
			}
		}
		return rs;
	}
	
	
	public Map<String,Map<String, Map<String,SaleReport>>> getSalesByProduct2(SaleReport saleReport, Map<String, Float> rateRs){
		String currencyType = saleReport.getCurrencyType(); //统计的货币类型（EUR/USD）
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		}
		List<Object[]> list = null;
		if(StringUtils.isEmpty(saleReport.getProductType())){
			String sql ="SELECT a.`country`,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)),round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE sum(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),(CASE when a.`product_name` is null  then 'noProductName'  else CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) end) product,DATE_FORMAT(a.`date`,"+typeSql+") dates,SUM(a.`real_sales_volume`*IFNULL(a.`pack_num`,1)), " +
					"round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) FROM amazoninfo_sale_report a  WHERE a.order_type='1' and  a.`date`>=:p1 AND a.`date`<=:p2  GROUP BY a.`country`,product,dates ORDER BY a.`country`,dates DESC ";
			list = saleReportDao.findBySql(sql, new Parameter(start,end));
		}else{
			String sql ="SELECT a.`country`,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)),round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sure_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sure_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sure_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) product,DATE_FORMAT(a.`date`,"+typeSql+") dates,SUM(a.`real_sales_volume`*IFNULL(a.`pack_num`,1)), " +
					"round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) FROM amazoninfo_sale_report a " +
					" join psi_product p on a.`product_name`=CONCAT(p.`brand`,' ',p.`model`) and p.del_flag='0' " +
					" WHERE a.order_type='1' and  a.`date`>=:p1 AND a.`date`<=:p2 and p.type in ("+saleReport.getProductType()+")  GROUP BY a.`country`,product,dates ORDER BY a.`country`,dates DESC ";
		   list = saleReportDao.findBySql(sql, new Parameter(start,end));
		}
		Map<String,Map<String, Map<String,SaleReport>>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
			Integer salesVolume = ((BigDecimal)(objs[1]==null?new BigDecimal(0):objs[1])).intValue();
			Integer sureSalesVolume = ((BigDecimal)(objs[2]==null?new BigDecimal(0):objs[2])).intValue();
			Float sales = ((BigDecimal)(objs[3]==null?new BigDecimal(0):objs[3])).floatValue();
			Float sureSales = ((BigDecimal)(objs[4]==null?new BigDecimal(0):objs[4])).floatValue();
			String productName = (objs[5]==null)?"":objs[5].toString(); 
			String date = objs[6].toString(); 
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Integer realSalesVolume = ((BigDecimal)(objs[7]==null?new BigDecimal(0):objs[7])).intValue();
			Float realSales = ((BigDecimal)(objs[8]==null?new BigDecimal(0):objs[8])).floatValue();
			Map<String, Map<String,SaleReport>> datas = rs.get(productName);
			if(datas==null){
				datas = Maps.newLinkedHashMap();
				rs.put(productName, datas);
			}
			Map<String, SaleReport> sale=datas.get(date);
			if(sale==null){
				sale=Maps.newLinkedHashMap();
				datas.put(date, sale);
			}
			sale.put(country,  new SaleReport(sales,sureSales,realSales,salesVolume,sureSalesVolume,realSalesVolume));
		}
		return rs;
	}
	
	public Map<String,Map<String,Object>> getProductsMoqAndPrice(SaleReport saleReport, Map<String, Float> rateRs){
		String currencyType = saleReport.getCurrencyType(); //统计的货币类型（EUR/USD）
		 Map<String,Map<String,Object>> rs =this.tieredPriceService.getMoqPriceBaseMoq(currencyType, rateRs);
//		String sql ="SELECT DISTINCT CONCAT(a.`brand`,' ',a.`model`),a.`color`,a.`min_order_placed`, " +
//				"TRUNCATE( CASE WHEN c.`currency_type`='CNY' THEN  b.rmb_price*"+MathUtils.getRate("CNY", currencyType, rateRs)
//				+"  ELSE  b.price*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ,2) FROM psi_product a,psi_product_supplier b,psi_supplier c WHERE b.`supplier_id` = c.`id` AND  a.`del_flag` = '0' AND a.`id` = b.`product_id`";
//		List<Object[]> list = saleReportDao.findBySql(sql);
//		Map<String,Map<String,Object>> rs = Maps.newHashMap();
//		for (Object[] objects : list) {
//			String name = objects[0].toString();
//			String color = objects[1].toString();
//			Object moq = objects[2];
//			Object price = objects[3];
//			Map<String,Object> data = Maps.newHashMap();
//			data.put("moq", moq);
//			data.put("price",price);
//			if(StringUtils.isEmpty(color)){
//				rs.put(name, data);
//			}else{
//				for (String colorStr : color.split(",")) {
//					rs.put(name+"_"+colorStr, data);
//				}
//			}
//		}
		return rs;
	}
	
	public Map<String,Map<String, Map<String,SaleReport>>> getSalesByProductType(SaleReport saleReport, Map<String, Float> rateRs){
		String currencyType = saleReport.getCurrencyType(); //统计的货币类型（EUR/USD）
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -3);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		}
		List<Object[]> list = null;
		if(StringUtils.isEmpty(saleReport.getProductType())){
			String sql ="SELECT a.`country`,SUM(a.`sales_volume`),SUM(a.`sure_sales_volume`),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sure_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sure_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sure_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),a.type,DATE_FORMAT(a.`date`,"+typeSql+") dates,SUM(a.`real_sales_volume`), " +
					"TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) FROM  amazoninfo_sale_report_type a  " +
					" WHERE a.order_type='1' and a.`date`>=:p1 AND a.`date`<=:p2  GROUP BY a.`country`,a.type,dates ORDER BY a.`country`,dates DESC ";
			list = saleReportDao.findBySql(sql, new Parameter(start,end));
		}else{
			String sql ="SELECT a.`country`,SUM(a.`sales_volume`),SUM(a.`sure_sales_volume`),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sure_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sure_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sure_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),a.type,DATE_FORMAT(a.`date`,"+typeSql+") dates ,SUM(a.`real_sales_volume`), " +
					"TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) FROM  amazoninfo_sale_report_type a  " +
					" WHERE a.order_type='1' and  a.`date`>=:p1 AND a.`date`<=:p2 and a.type in ("+saleReport.getProductType()+")  GROUP BY a.`country`,a.type,dates ORDER BY a.`country`,dates DESC ";
		   list = saleReportDao.findBySql(sql, new Parameter(start,end));
		}
		Map<String,Map<String, Map<String,SaleReport>>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
			Integer salesVolume = ((BigDecimal)objs[1]).intValue();
			Integer sureSalesVolume = ((BigDecimal)objs[2]).intValue();
			Float sales = ((BigDecimal)(objs[3]==null?new BigDecimal(0):objs[3])).floatValue();
			Float sureSales = ((BigDecimal)(objs[4]==null?new BigDecimal(0):objs[4])).floatValue();
			String productName = (objs[5]==null)?"":objs[5].toString(); 
			String date = objs[6].toString(); 
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Integer realSalesVolume = ((BigDecimal)objs[7]).intValue();
			Float realSales = ((BigDecimal)(objs[8]==null?new BigDecimal(0):objs[8])).floatValue();
			Map<String, Map<String,SaleReport>> datas = rs.get(country);
			if(datas==null){
				datas = Maps.newLinkedHashMap();
				rs.put(country, datas);
			}
			Map<String, SaleReport> sale=datas.get(date);
			if(sale==null){
				sale=Maps.newLinkedHashMap();
				datas.put(date, sale);
			}
			sale.put(productName, new SaleReport(sales,sureSales,realSales,salesVolume,sureSalesVolume,realSalesVolume));
			
			
			//欧洲汇总
			if ("de,fr,it,es,uk".contains(country)) {
				Map<String, Map<String,SaleReport>> datas2 = rs.get("eu");
				if(datas2==null){
					datas2 = Maps.newLinkedHashMap();
					rs.put("eu", datas2);
				}
				Map<String, SaleReport>  dateMap = datas2.get(date);
				if(dateMap==null){
					dateMap = Maps.newLinkedHashMap();
					datas2.put(date, dateMap);
				}
				SaleReport report=dateMap.get(productName);
				if(report==null){
					report = new SaleReport(0f,0f,0f,0,0,0);
					dateMap.put(productName, report);
				}
				report.setSales(sales+report.getSales());
				report.setSalesVolume(salesVolume+report.getSalesVolume());
				report.setSureSales(sureSales+report.getSureSales());
				report.setSureSalesVolume(sureSalesVolume+report.getSureSalesVolume());
				report.setRealSales(realSales+report.getRealSales());
				report.setRealSalesVolume(realSalesVolume+report.getRealSalesVolume());
			}
			
			//英语国家汇总
			if ("com,ca,uk,com1".contains(country)||country.startsWith("com")) {
				Map<String, Map<String,SaleReport>> datas2 = rs.get("en");
				if(datas2==null){
					datas2 = Maps.newLinkedHashMap();
					rs.put("en", datas2);
				}
				Map<String, SaleReport>  dateMap = datas2.get(date);
				if(dateMap==null){
					dateMap = Maps.newLinkedHashMap();
					datas2.put(date, dateMap);
				}
				SaleReport report=dateMap.get(productName);
				if(report==null){
					report = new SaleReport(0f,0f,0f,0,0,0);
					dateMap.put(productName, report);
				}
				report.setSales(sales+report.getSales());
				report.setSalesVolume(salesVolume+report.getSalesVolume());
				report.setSureSales(sureSales+report.getSureSales());
				report.setSureSalesVolume(sureSalesVolume+report.getSureSalesVolume());
				report.setRealSales(realSales+report.getRealSales());
				report.setRealSalesVolume(realSalesVolume+report.getRealSalesVolume());
			}
		}
		return rs;
	}
	
	public Map<String,Map<String, Map<String,SaleReport>>> getSalesByProductGroupType(SaleReport saleReport, Map<String, Float> rateRs){
		String currencyType = saleReport.getCurrencyType(); //统计的货币类型（EUR/USD）
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -3);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		}
		List<Object[]> list = null;
		String sql ="SELECT a.`country`,SUM(a.`sales_volume`),SUM(a.`sure_sales_volume`),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sure_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sure_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sure_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),t.name,DATE_FORMAT(a.`date`,"+typeSql+") dates,SUM(a.`real_sales_volume`), " +
					"TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) FROM  amazoninfo_sale_report_type a  " +
					" join sys_dict d on d.value=a.type and d.type='product_type' and d.del_flag='0' "+
					" join psi_product_type_group g on g.dict_id=d.id "+
					" join psi_product_type_dict t on g.id=t.id and t.del_flag='0' "+
					" WHERE a.order_type='1' and  a.`date`>=:p1 AND a.`date`<=:p2  GROUP BY a.`country`,t.name,dates ORDER BY a.`country`,dates DESC ";
		list = saleReportDao.findBySql(sql, new Parameter(start,end));
		
		Map<String,Map<String, Map<String,SaleReport>>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
			Integer salesVolume = ((BigDecimal)objs[1]).intValue();
			Integer sureSalesVolume = ((BigDecimal)objs[2]).intValue();
			Float sales = ((BigDecimal)(objs[3]==null?new BigDecimal(0):objs[3])).floatValue();
			Float sureSales = ((BigDecimal)(objs[4]==null?new BigDecimal(0):objs[4])).floatValue();
			String productName = (objs[5]==null)?"":objs[5].toString(); 
			String date = objs[6].toString();
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Integer realSalesVolume = ((BigDecimal)objs[7]).intValue();
			Float realSales = ((BigDecimal)(objs[8]==null?new BigDecimal(0):objs[8])).floatValue();
			Map<String, Map<String,SaleReport>> datas = rs.get(country);
			if(datas==null){
				datas = Maps.newLinkedHashMap();
				rs.put(country, datas);
			}
			Map<String, SaleReport> sale=datas.get(date);
			if(sale==null){
				sale=Maps.newLinkedHashMap();
				datas.put(date, sale);
			}
			sale.put(productName, new SaleReport(sales,sureSales,realSales,salesVolume,sureSalesVolume,realSalesVolume));
		}
		for (Map.Entry<String, Map<String, Map<String, SaleReport>>> entry: rs.entrySet()) { 
			Map<String, Map<String,SaleReport>> datas = entry.getValue();
			for (Map.Entry<String, Map<String, SaleReport>> entryDatas: datas.entrySet()) { 
				Map<String, SaleReport> sale=entryDatas.getValue();
				float sales=0f;
				float sureSales=0f;
				float realSales=0f;
			
				int salesVolume=0;
				int sureSalesVolume=0;
				int realSalesVolume=0;
				for (Map.Entry<String, SaleReport> entryRs: sale.entrySet()) { 
				    SaleReport report=entryRs.getValue();
					sales+=report.getSales();
					sureSales+=report.getSureSales();
					realSales+=report.getRealSales();
					salesVolume+=report.getSalesVolume();
					sureSalesVolume+=report.getSureSalesVolume();
					realSalesVolume+=report.getRealSalesVolume();
				}
				sale.put("total",new SaleReport(sales,sureSales,realSales,salesVolume,sureSalesVolume,realSalesVolume));
			}
		}
		return rs;
	}
	private static DateFormat format = new SimpleDateFormat("yyyyMMdd");
	
	public List<Object[]> findOrder(String dateStr,String byTime,String country,String productName,String currencyType, Map<String, Float> rateRs,String orderType,String lineType,Set<String> nameSet){
		Date start = null;
		Date end = null;
		if("1".equals(byTime)){
			try {
				start = format.parse(dateStr);
				end = DateUtils.addDays(start, 1);
			} catch (ParseException e) {}
		}else if("2".equals(byTime)){
			int year = Integer.parseInt(dateStr.substring(0,4));
			int week = Integer.parseInt(dateStr.substring(4));
			start = DateUtils.getFirstDayOfWeek(year, week);
			end = DateUtils.getLastDayOfWeek(year, week);
			end = DateUtils.addDays(end, 1);
		}else if("3".equals(byTime)){
			int year = Integer.parseInt(dateStr.substring(0,4));
			int month = Integer.parseInt(dateStr.substring(4))-1;
			try {
				start = DateUtils.getFirstDayOfMonth(year, month);
				end = DateUtils.getLastDayOfMonth(year, month);
				end = DateUtils.addDays(end, 1);
			} catch (ParseException e) {}
		}
		
		if(StringUtils.isBlank(orderType)||orderType.startsWith("1")){
			String temp = "";
			if(!"total".equals(country) && !"eu".equals(country)&&!"en".equals(country)&&!"unEn".equals(country)){
				temp = "  and  a.`sales_channel` like '%"+country+"%'";
			}else if ("eu".equals(country)) {
				temp = "  and  a.`sales_channel` in ('Amazon.de','Amazon.co.uk','Amazon.es','Amazon.fr','Amazon.it') ";
			}else if ("unEn".equals(country)) {
				temp = "  and  a.`sales_channel` in ('Amazon.de','Amazon.co.jp','Amazon.es','Amazon.fr','Amazon.it','Amazon.com.mx') ";
			}else if ("en".equals(country)) {
				temp = "  and ( a.`sales_channel` in ('Amazon.co.uk','Amazon.ca') or a.sales_channel like 'Amazon.com%' ) ";
			}
			String temp1="";
			if(StringUtils.isNotEmpty(productName)){
				if(nameSet!=null&&nameSet.size()>0){
					Set<String> tempName=Sets.newHashSet();
					for (String name: nameSet) {
						tempName.add("'"+name+"'");
					}
					temp1 = "  and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) in ("+StringUtils.join(tempName.toArray(),",")+")";
				}else{
					temp1 = "  and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END)='"+productName+"'";
				}
				
			}
	        String sql = "SELECT a.`amazon_order_id`,a.`sales_channel`,b.`sellersku`,CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) AS productName,SUM(b.`quantity_ordered`),a.`order_status`,TRUNCATE((CASE WHEN a.sales_channel='Amazon.ca' THEN sum(b.`item_price`-IFNULL(b.`promotion_discount`,0))*:p1 WHEN a.sales_channel='Amazon.co.jp' THEN sum(b.`item_price`-IFNULL(b.`promotion_discount`,0))*:p2 WHEN a.sales_channel='Amazon.co.uk' THEN sum(b.`item_price`-IFNULL(b.`promotion_discount`,0))*:p3 WHEN LOCATE('mx',a.sales_channel)>0 THEN sum(b.`item_price`-IFNULL(b.`promotion_discount`,0))*:p4 WHEN locate('Amazon.com',a.`sales_channel`)>0 THEN sum(b.`item_price`-IFNULL(b.`promotion_discount`,0))*:p5  ELSE sum(b.`item_price`-IFNULL(b.`promotion_discount`,0))*:p6 END),2),a.`purchase_date` "+
			" ,(CASE when a.is_business_order='1' then '9' WHEN (b.`promotion_ids` LIKE '%,F-%' or b.`promotion_ids` LIKE 'F-%' or b.`promotion_ids` LIKE 'S-%'  OR b.`promotion_ids` LIKE '%,S-%' or b.`promotion_ids` LIKE '%Coupon%') THEN '1' WHEN (  (b.`promotion_ids` IS NULL  OR (b.`promotion_ids` IS NOT NULL AND b.`promotion_ids` NOT LIKE '%,%' AND (b.`promotion_ids` LIKE '%Free Shipping%' or b.`promotion_ids` LIKE 'Free Delivery%'))) AND b.`promotion_discount` >0) THEN '2' WHEN (b.`promotion_ids` LIKE 'Free-%' or b.`promotion_ids` LIKE '%,Free-%' or b.`promotion_ids` LIKE 'R-%' or b.`promotion_ids` LIKE '%,R-%'  ) THEN '5' ELSE '4' END) orderType,k.state_or_region,k.country_code "+
			" FROM amazoninfo_order a join amazoninfo_orderitem b on a.`id` = b.`order_id` "+
			" left join amazoninfo_address k on k.id=a.shipping_address ";
	        if(!"total".equals(lineType)){
				sql+=" left join psi_product  c on  b.`product_name` = CONCAT(c.`brand`,' ',c.`model`) AND c.`del_flag`='0' left JOIN sys_dict d ON d.`value`=c.`type` AND d.del_flag='0' AND d.type='product_type' LEFT JOIN psi_product_type_group g ON g.`dict_id`=d.id  ";
			}
	        sql+=" where  a.`order_status` IN('UnShipped','Shipped','Pending')  and a.`order_channel` IS NULL   AND a.`purchase_date`>=:p7 AND a.`purchase_date` <:p8 "+temp+temp1+" ";
	        if(!"total".equals(lineType)){
	        	if(!"unGrouped".equals(lineType)){
	        		sql+=" and g.id='"+lineType+"' ";
	        	}else{
	        		sql+=" and g.id is null";
	        	}
	        }
	        sql+=" group by a.`amazon_order_id`,a.`sales_channel`,b.`sellersku`,productName,a.`order_status`,a.`purchase_date`,orderType order by a.`purchase_date` ";
	        List<Object[]>  list = saleReportDao.findBySql(sql, new Parameter(MathUtils.getRate("CAD", currencyType, rateRs),MathUtils.getRate("JPY", currencyType, rateRs),MathUtils.getRate("GBP", currencyType, rateRs),MathUtils.getRate("MXN", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("EUR", currencyType, rateRs),start,end));
	        return list;
		}else if(orderType.startsWith("2")){
			String temp="";
			String[] arr=orderType.split("-");
			if(StringUtils.isNotEmpty(productName)){
				temp += "  and b.product_name='"+productName+"'";
			}
				temp += "  and  a.country like '%"+arr[1]+"%'";
			String sql="SELECT a.order_id,a.country,b.sku,b.product_name,SUM(b.accepted_quantity),a.status,TRUNCATE((CASE WHEN a.country='de' THEN SUM(b.`accepted_quantity`*b.unit_price)*:p1 ELSE SUM(b.`accepted_quantity`*b.unit_price)*:p2 END),2),a.ordered_date,a.id "+
             " FROM amazoninfo_vendor_order a JOIN amazoninfo_vendor_orderitem b ON a.id=b.`order_id` ";
             if(!"total".equals(lineType)){
 				sql+=" left join psi_product c on  SUBSTRING_INDEX(b.`product_name`,'_',1) = CONCAT(c.`brand`,' ',c.`model`) and c.del_flag='0' left JOIN sys_dict d ON d.`value`=c.`type` AND d.del_flag='0' AND d.type='product_type' LEFT JOIN psi_product_type_group g ON g.`dict_id`=d.id  ";
 			 }	
             sql+=" where a.`ordered_date`>=:p3 AND a.`ordered_date` <:p4 "+temp+"  ";	
             if(!"total".equals(lineType)){
 	        	if(!"unGrouped".equals(lineType)){
 	        		sql+=" and g.id='"+lineType+"' ";
 	        	}else{
 	        		sql+=" and g.id is null";
 	        	}
 	         }
             sql+= " GROUP BY a.order_id,a.country,b.sku,product_name,a.status,a.ordered_date ORDER BY a.ordered_date ";
			List<Object[]>  list =saleReportDao.findBySql(sql, new Parameter(MathUtils.getRate("EUR", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),start,end));
			return list;
		}else if(orderType.startsWith("3")){
			String temp="";
			String[] arr=orderType.split("-");
			if(StringUtils.isNotEmpty(productName)){
				temp += "  and CONCAT(s.`product_name`,CASE  WHEN s.`color`='' THEN '' ELSE CONCAT('_',s.`color`) END)='"+productName+"'";
			}
			String sql="SELECT a.order_id,a.country,b.sku,CONCAT(s.`product_name`,CASE  WHEN s.`color`='' THEN '' ELSE CONCAT('_',s.`color`) END) product_name,SUM(b.quantity_purchased),(CASE WHEN a.`status`='0' THEN 'UnPaid' WHEN a.`status`='1' THEN 'Paid' ELSE 'Shipped' END) orderStatus , "+
					" TRUNCATE(SUM(b.`quantity_purchased`*b.transaction_price)*:p1,2),a.`created_time`,a.id "+
					" FROM ebay_order a JOIN ebay_orderitem b  ON a.id=b.`order_id` "+
					" left JOIN (SELECT DISTINCT sku,product_name,color FROM psi_sku s WHERE s.del_flag='0' AND country in ('ebay','ebay_com')) s ON b.sku=s.sku ";
			  if(!"total".equals(lineType)){
 				sql+=" left join psi_product c on  s.`product_name` = CONCAT(c.`brand`,' ',c.`model`) and c.del_flag='0' left JOIN sys_dict d ON d.`value`=c.`type` AND d.del_flag='0' AND d.type='product_type' LEFT JOIN psi_product_type_group g ON g.`dict_id`=d.id  ";
 			  }	
			   sql+=" WHERE a.country='"+arr[1]+"' and b.sku is not null and a.`order_status`!='Cancelled' and a.`created_time`>=:p2 AND a.`created_time` <:p3 "+temp+" ";
			   if(!"total".equals(lineType)){
	 	        	if(!"unGrouped".equals(lineType)){
	 	        		sql+=" and g.id='"+lineType+"' ";
	 	        	}else{
	 	        		sql+=" and g.id is null";
	 	        	}
	 	         }
			   sql+=" group by a.order_id,a.country,b.sku,product_name,orderStatus,a.`created_time` order by a.`created_time`";
			Float rate=MathUtils.getRate("EUR", currencyType, rateRs);
			if("com".equals(arr[1])){
				rate=MathUtils.getRate("USD", currencyType, rateRs);
			}
			List<Object[]>  list =saleReportDao.findBySql(sql, new Parameter(rate,start,end));
			return list;
		}else if(orderType.startsWith("4")){
			String temp="";
			if(StringUtils.isNotEmpty(productName)){
				temp += "  and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END)='"+productName+"'";
			}
			String sql="SELECT a.amazon_order_id,(CASE WHEN a.sales_channel=19 THEN 'de' WHEN a.sales_channel=120 THEN 'com' ELSE 'cn' END) country,b.sellersku, "+
	            " CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) product_name,SUM(b.quantity_ordered),a.order_status, "+
	            " TRUNCATE((CASE WHEN a.sales_channel=19 THEN SUM(b.item_price*quantity_ordered)*:p1 WHEN a.sales_channel=120 THEN SUM(b.item_price*quantity_ordered)*:p2 ELSE SUM(b.item_price*quantity_ordered)*:p3 END),2),a.purchase_date,a.id "+ 
	            " FROM amazoninfo_unline_order a JOIN amazoninfo_unline_orderitem b ON a.id=b.order_id  ";
			if(!"total".equals(lineType)){
	 				sql+=" left join psi_product c on b.`product_name` = CONCAT(c.`brand`,' ',c.`model`) and c.del_flag='0' left JOIN sys_dict d ON d.`value`=c.`type`  AND d.del_flag='0' AND d.type='product_type' LEFT JOIN psi_product_type_group g ON g.`dict_id`=d.id  ";
	 		}    
			sql+="  WHERE cancel_date IS NULL and  a.order_channel!='管理员' and  a.order_channel!='check24' and a.order_channel not like '%-OTHER-%' AND a.`order_status`!='Canceled' AND a.`order_status`!='PaymentPending'	  and a.`purchase_date`>=:p4 AND a.`purchase_date` <:p5 "+temp+" ";
			if(!"total".equals(lineType)){
 	        	if(!"unGrouped".equals(lineType)){
 	        		sql+=" and g.id='"+lineType+"' ";
 	        	}else{
 	        		sql+=" and g.id is null";
 	        	}
 	         }
			sql+=" GROUP BY a.amazon_order_id,country,b.sellersku,product_name,a.order_status,a.purchase_date  ORDER BY a.purchase_date ";
			List<Object[]>  list =saleReportDao.findBySql(sql, new Parameter(MathUtils.getRate("EUR", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),start,end));
			return list;	
		}else if(orderType.startsWith("5")){
			String temp="";
			String[] arr=orderType.split("-");
			
			if(StringUtils.isNotEmpty(productName)){
				temp += "  and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END)='"+productName+"'";
			}
			String sql="SELECT a.amazon_order_id,(CASE WHEN a.sales_channel=19 THEN 'de' WHEN a.sales_channel=120 THEN 'com' ELSE 'cn' END) country,b.sellersku, "+
	            " CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) product_name,SUM(b.quantity_ordered),a.order_status, "+
	            " TRUNCATE((CASE WHEN a.sales_channel=19 THEN SUM(b.item_price*quantity_ordered)*:p1 WHEN a.sales_channel=120 THEN SUM(b.item_price*quantity_ordered)*:p2 ELSE SUM(b.item_price*quantity_ordered)*:p3 END),2),a.purchase_date,a.id "+ 
	            " FROM amazoninfo_unline_order a JOIN amazoninfo_unline_orderitem b ON a.id=b.order_id  ";
			if(!"total".equals(lineType)){
	 				sql+=" left join psi_product c on b.`product_name` = CONCAT(c.`brand`,' ',c.`model`) and c.del_flag='0' left JOIN sys_dict d ON d.`value`=c.`type`  AND d.del_flag='0' AND d.type='product_type' LEFT JOIN psi_product_type_group g ON g.`dict_id`=d.id  ";
	 		}    
			sql+="  WHERE cancel_date IS NULL and  a.order_channel='管理员' and a.sales_channel="+("de".equals(arr[1])?19:120)+" AND a.`order_status`!='Canceled' AND a.`order_status`!='PaymentPending'	  and a.`purchase_date`>=:p4 AND a.`purchase_date` <:p5 "+temp+" ";
			if(!"total".equals(lineType)){
 	        	if(!"unGrouped".equals(lineType)){
 	        		sql+=" and g.id='"+lineType+"' ";
 	        	}else{
 	        		sql+=" and g.id is null";
 	        	}
 	         }
			sql+=" GROUP BY a.amazon_order_id,country,b.sellersku,product_name,a.order_status,a.purchase_date  ORDER BY a.purchase_date ";
			List<Object[]>  list =saleReportDao.findBySql(sql, new Parameter(MathUtils.getRate("EUR", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),start,end));
			return list;
		}else if(orderType.startsWith("6")){
			String temp="";
			if(StringUtils.isNotEmpty(productName)){
				temp += "  and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END)='"+productName+"'";
			}
			String sql="SELECT a.amazon_order_id,(CASE WHEN a.sales_channel=19 THEN 'de' WHEN a.sales_channel=120 THEN 'com' ELSE 'cn' END) country,b.sellersku, "+
	            " CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) product_name,SUM(b.quantity_ordered),a.order_status, "+
	            " TRUNCATE((CASE WHEN a.sales_channel=19 THEN SUM(b.item_price*quantity_ordered)*:p1 WHEN a.sales_channel=120 THEN SUM(b.item_price*quantity_ordered)*:p2 ELSE SUM(b.item_price*quantity_ordered)*:p3 END),2),a.purchase_date,a.id "+ 
	            " FROM amazoninfo_unline_order a JOIN amazoninfo_unline_orderitem b ON a.id=b.order_id  ";
			if(!"total".equals(lineType)){
	 				sql+=" left join psi_product c on b.`product_name` = CONCAT(c.`brand`,' ',c.`model`) and c.del_flag='0' left JOIN sys_dict d ON d.`value`=c.`type`  AND d.del_flag='0' AND d.type='product_type' LEFT JOIN psi_product_type_group g ON g.`dict_id`=d.id  ";
	 		}    
			sql+="  WHERE cancel_date IS NULL and  a.order_channel='check24' AND a.`order_status`!='Canceled' AND a.`order_status`!='PaymentPending'	  and a.`purchase_date`>=:p4 AND a.`purchase_date` <:p5 "+temp+" ";
			if(!"total".equals(lineType)){
 	        	if(!"unGrouped".equals(lineType)){
 	        		sql+=" and g.id='"+lineType+"' ";
 	        	}else{
 	        		sql+=" and g.id is null";
 	        	}
 	         }
			sql+=" GROUP BY a.amazon_order_id,country,b.sellersku,product_name,a.order_status,a.purchase_date  ORDER BY a.purchase_date ";
			List<Object[]>  list =saleReportDao.findBySql(sql, new Parameter(MathUtils.getRate("EUR", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),start,end));
			return list;
		}else if(orderType.startsWith("7")){
			String temp="";
			if(StringUtils.isNotEmpty(productName)){
				temp += "  and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END)='"+productName+"'";
			}
			String sql="SELECT a.amazon_order_id,(CASE WHEN a.sales_channel=19 THEN 'de' WHEN a.sales_channel=120 THEN 'com' ELSE 'cn' END) country,b.sellersku, "+
	            " CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) product_name,SUM(b.quantity_ordered),a.order_status, "+
	            " TRUNCATE((CASE WHEN a.sales_channel=19 THEN SUM(b.item_price*quantity_ordered)*:p1 WHEN a.sales_channel=120 THEN SUM(b.item_price*quantity_ordered)*:p2 ELSE SUM(b.item_price*quantity_ordered)*:p3 END),2),a.purchase_date,a.id "+ 
	            " FROM amazoninfo_unline_order a JOIN amazoninfo_unline_orderitem b ON a.id=b.order_id  ";
			if(!"total".equals(lineType)){
	 				sql+=" left join psi_product c on b.`product_name` = CONCAT(c.`brand`,' ',c.`model`) and c.del_flag='0' left JOIN sys_dict d ON d.`value`=c.`type`  AND d.del_flag='0' AND d.type='product_type' LEFT JOIN psi_product_type_group g ON g.`dict_id`=d.id  ";
	 		}    
			sql+="  WHERE cancel_date IS NULL and a.order_channel like '%-OTHER-%' AND a.`order_status`!='Canceled' AND a.`order_status`!='PaymentPending'	  and a.`purchase_date`>=:p4 AND a.`purchase_date` <:p5 "+temp+" ";
			if(!"total".equals(lineType)){
 	        	if(!"unGrouped".equals(lineType)){
 	        		sql+=" and g.id='"+lineType+"' ";
 	        	}else{
 	        		sql+=" and g.id is null";
 	        	}
 	         }
			sql+=" GROUP BY a.amazon_order_id,country,b.sellersku,product_name,a.order_status,a.purchase_date  ORDER BY a.purchase_date ";
			List<Object[]>  list =saleReportDao.findBySql(sql, new Parameter(MathUtils.getRate("EUR", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),start,end));
			return list;	
			
		}
		return null;
	}
	
	
	
	public List<Object[]> findReviewOrder(String dateStr,String byTime,String country,String productName,String currencyType, Map<String, Float> rateRs,String orderType,String lineType,Set<String> nameSet){
		Date start = null;
		Date end = null;
		if("1".equals(byTime)){
			try {
				start = format.parse(dateStr);
				end = DateUtils.addDays(start, 1);
			} catch (ParseException e) {}
		}else if("2".equals(byTime)){
			int year = Integer.parseInt(dateStr.substring(0,4));
			int week = Integer.parseInt(dateStr.substring(4));
			start = DateUtils.getFirstDayOfWeek(year, week);
			end = DateUtils.getLastDayOfWeek(year, week);
			end = DateUtils.addDays(end, 1);
		}else if("3".equals(byTime)){
			int year = Integer.parseInt(dateStr.substring(0,4));
			int month = Integer.parseInt(dateStr.substring(4))-1;
			try {
				start = DateUtils.getFirstDayOfMonth(year, month);
				end = DateUtils.getLastDayOfMonth(year, month);
				end = DateUtils.addDays(end, 1);
			} catch (ParseException e) {}
		}
		
			String temp = "";
			if(!"total".equals(country) && !"eu".equals(country)&&!"en".equals(country)&&!"unEn".equals(country)){
				temp = "  and  a.`country` like '%"+country+"%'";
			}else if ("eu".equals(country)) {
				temp = "  and  a.`country` in ('de','uk','es','fr','it') ";
			}else if ("unEn".equals(country)) {
				temp = "  and  a.`country` in ('de','jp','es','fr','it','mx') ";
			}else if ("en".equals(country)) {
				temp = "  and  a.`country` in ('uk','ca','com') ";
			}
			String temp1="";
			if(StringUtils.isNotEmpty(productName)){
				if(nameSet!=null&&nameSet.size()>0){
					Set<String> tempName=Sets.newHashSet();
					for (String name: nameSet) {
						tempName.add("'"+name+"'");
					}
					temp1 = "  and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) in ("+StringUtils.join(tempName.toArray(),",")+")";
				}else{
					temp1 = "  and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END)='"+productName+"'";
				}
				
			}
	        String sql = "SELECT a.`seller_order_id`,a.`country`,b.`sellersku`,CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) AS productName,SUM(b.`quantity_ordered`),a.`order_status`,a.`create_date`,a.`order_type`,a.remark,s.name "+
			" FROM amazoninfo_outbound_order a join amazoninfo_outbound_orderitem b on a.`id` = b.`order_id`  join sys_user s on s.`id` = a.`create_user` ";
	        if(!"total".equals(lineType)){
				sql+=" left join psi_product  c on  b.`product_name` = CONCAT(c.`brand`,' ',c.`model`) AND c.`del_flag`='0' left JOIN sys_dict d ON d.`value`=c.`type` AND d.del_flag='0' AND d.type='product_type' LEFT JOIN psi_product_type_group g ON g.`dict_id`=d.id  ";
			}
	        sql+=" where  a.`order_status`='COMPLETE' AND a.`order_type` in ('Review','Paypal_Refund','Support','Marketing')   AND a.`create_date`>=:p1 AND a.`create_date` <:p2 "+temp+temp1+" ";
	        if(!"total".equals(lineType)){
	        	if(!"unGrouped".equals(lineType)){
	        		sql+=" and g.id='"+lineType+"' ";
	        	}else{
	        		sql+=" and g.id is null";
	        	}
	        }
	        sql+=" group by a.`seller_order_id`,a.`country`,b.`sellersku`,productName,a.`order_status`,a.`create_date` order by a.order_type ";
	        List<Object[]>  list = saleReportDao.findBySql(sql, new Parameter(start,end));
	        return list;
	}	
		
	
	public List<String> findOrder(String dateStr,String byTime,String country,String orderType){
		Date start = null;
		Date end = null;
		if("1".equals(byTime)){
			try {
				start = format.parse(dateStr);
				end = DateUtils.addDays(start, 1);
			} catch (ParseException e) {}
		}else if("2".equals(byTime)){
			int year = Integer.parseInt(dateStr.substring(0,4));
			int week = Integer.parseInt(dateStr.substring(4));
			start = DateUtils.getFirstDayOfWeek(year, week);
			end = DateUtils.getLastDayOfWeek(year, week);
			end = DateUtils.addDays(end, 1);
		}else if("3".equals(byTime)){
			int year = Integer.parseInt(dateStr.substring(0,4));
			int month = Integer.parseInt(dateStr.substring(4))-1;
			try {
				start = DateUtils.getFirstDayOfMonth(year, month);
				end = DateUtils.getLastDayOfMonth(year, month);
				end = DateUtils.addDays(end, 1);
			} catch (ParseException e) {}
		}
		
		if(StringUtils.isBlank(orderType)||orderType.startsWith("1")){
			String temp = "";
			if(!"total".equals(country) && !"eu".equals(country)&&!"en".equals(country)&&!"unEn".equals(country)){
				temp = "  and  a.`country` like '%"+country+"%'";
			}else if ("eu".equals(country)) {
				temp = "  and  a.`country` in ('de','uk','es','fr','it') ";
			}else if ("unEn".equals(country)) {
				temp = "  and  a.`country` in ('de','jp','es','fr','it','mx') ";
			}else if ("en".equals(country)) {
				temp = "  and  a.`country` in ('uk','ca','com') ";
			}
	        String sql = "SELECT distinct a.`amazon_order_id` FROM amazoninfo_outbound_order a where a.create_date>=:p1 and create_date<=:p2 and a.`order_type`='Paypal_Refund' "+temp;
	        List<String>  list = saleReportDao.findBySql(sql, new Parameter(start,end));
	        return list;
		}
		return null;
	}
	
	/**
	 * 
	 * 主表操作中的SKU
	 * @param dateStr
	 * @param byTime
	 * @param country
	 * @return
	 */
	public List<Object[]> findSKU(String dateStr,String byTime,String country,String currencyType, Map<String, Float> rateRs,String orderType,String lineType){
		Date start = null;
		Date end = null;
		if("1".equals(byTime)){
			try {
				start = format.parse(dateStr);
				end = DateUtils.addDays(start, 1);
			} catch (ParseException e) {}
		}else if("2".equals(byTime)){
			int year = Integer.parseInt(dateStr.substring(0,4));
			int week = Integer.parseInt(dateStr.substring(4));
			start = DateUtils.getFirstDayOfWeek(year, week);
			end = DateUtils.getLastDayOfWeek(year, week);
			end = DateUtils.addDays(end, 1);
		}else if("3".equals(byTime)){
			int year = Integer.parseInt(dateStr.substring(0,4));
			int month = Integer.parseInt(dateStr.substring(4))-1;
			try {
				start = DateUtils.getFirstDayOfMonth(year, month);
				end = DateUtils.getLastDayOfMonth(year, month);
				end = DateUtils.addDays(end, 1);
			} catch (ParseException e) {}
		}
		
		String temp = "";
		if(!"total".equals(country) && !"eu".equals(country)&& !"en".equals(country) && !"unEn".equals(country)){
			temp += " and  a.country = '"+country+"'";
		} else if ("eu".equals(country)) {
			temp = "  and a.country in ('de','uk','es','fr','it') ";
		} else if ("unEn".equals(country)) {
			temp = "  and a.country in ('de','jp','es','fr','it') ";
		}else if ("en".equals(country)) {
			temp = "  and (a.country in ('uk','ca') or a.country like 'com%') ";
		}
		if(StringUtils.isBlank(orderType)||orderType.startsWith("1")){
			temp+=" and a.order_type='1' ";
		}else if(orderType.startsWith("2")||orderType.startsWith("3")){
			String[] arr=orderType.split("-");
			temp+=" and a.order_type='"+arr[0]+"' and a.country='"+arr[1]+"' ";
		}else if(orderType.startsWith("4")){
			temp+=" and a.order_type='4' ";
		}else if(orderType.startsWith("5")){
			String[] arr=orderType.split("-");
			temp+=" and a.order_type='5' and  a.country='"+arr[1]+"'  ";
		}else if(orderType.startsWith("6")){
			temp+=" and a.order_type='6' ";
		}else if(orderType.startsWith("7")){
			temp+=" and a.order_type='7' ";
		}
        String sql = "SELECT a.`sku`,CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END) AS productName,a.`country`,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) as sales,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.sales*:p1 WHEN a.country='jp' THEN a.sales*:p2 WHEN a.country='uk' THEN a.sales*:p3 WHEN locate('com',a.`country`)>0 THEN a.sales*:p4 WHEN a.country='mx' THEN a.sales*:p5 when a.country='cn' then  a.sales*:p6  ELSE a.sales*:p7 END),2) FROM amazoninfo_sale_report a  ";
        if(!"total".equals(lineType)){
			sql+=" left join psi_product  b on  a.`product_name` = CONCAT(b.`brand`,' ',b.`model`) and b.del_flag='0' left JOIN sys_dict d ON d.`value`=b.`type` AND d.del_flag='0' AND d.type='product_type' LEFT JOIN psi_product_type_group g ON g.`dict_id`=d.id  ";
		}
        sql+="WHERE  a.`date`>=:p8 AND a.`date` <:p9  "+temp+" ";
        if(!"total".equals(lineType)){
        	if(!"unGrouped".equals(lineType)){
        		sql+=" and g.id='"+lineType+"' ";
        	}else{
        		sql+=" and g.id is null";
        	}
        	
        }
        sql+=" GROUP BY a.`sku`,productName,a.`country` order by sales desc";
        List<Object[]>  list = saleReportDao.findBySql(sql, new Parameter(MathUtils.getRate("CAD", currencyType, rateRs),MathUtils.getRate("JPY", currencyType, rateRs),MathUtils.getRate("GBP", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("MXN", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("EUR", currencyType, rateRs),start,end));
        return list;
	}
	/**
	 * 主表操作中的商品
	 * @param dateStr
	 * @param byTime
	 * @param country
	 * @return
	 */
	public List<Object[]> findProduct(Date start,Date end,String country,String currencyType, Map<String, Float> rateRs,String orderType,String lineType){
		String temp = "";
		if(!"total".equals(country) && !"eu".equals(country)&& !"en".equals(country)&&!"unEn".equals(country)){
			temp += "and  a.`country` ='"+country+"'";
		} else if ("eu".equals(country)) {
			temp = " and a.country in ('de','uk','es','fr','it') ";
		} else if ("unEn".equals(country)) {
			temp = " and a.country in ('de','jp','es','fr','it','mx') ";
		}else if ("en".equals(country)) {
			temp = " and (a.country in ('uk','ca') or a.country like 'com%') ";
		}
		
		if(StringUtils.isBlank(orderType)||orderType.startsWith("1")){
			temp+=" and a.order_type='1' ";
		}else if(orderType.startsWith("2")||orderType.startsWith("3")){
			String[] arr=orderType.split("-");
			temp+=" and a.order_type='"+arr[0]+"' and a.country='"+arr[1]+"' ";
		}else if(orderType.startsWith("4")){
			temp+=" and a.order_type='4' ";
		}else if(orderType.startsWith("5")){
			String[] arr=orderType.split("-");
			temp+=" and a.order_type='5' and a.country='"+arr[1]+"' ";
		}else if(orderType.startsWith("6")){
			temp+=" and a.order_type='6' ";
		}else if(orderType.startsWith("7")){
			temp+=" and a.order_type='7' ";
		}
		String temp1 = "";
		Parameter parameter = null;
		if((StringUtils.isBlank(orderType)||orderType.startsWith("1"))&&UserUtils.hasPermission("amazoninfo:profits:view")){
			temp1 = " ,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.sales_no_tax*:p1 WHEN a.country='jp' THEN a.sales_no_tax*:p2 WHEN a.country='uk' THEN a.sales_no_tax*:p3/1.2 WHEN locate('com',a.`country`)>0 THEN a.sales_no_tax*:p4 WHEN a.country='mx' THEN a.sales_no_tax*:p5 WHEN a.country='cn' THEN a.sales_no_tax*:p6 WHEN a.country='es' THEN a.sales_no_tax*:p7/1.21  WHEN a.country='fr' THEN a.sales_no_tax*:p7/1.2 WHEN a.country='it' THEN a.sales_no_tax*:p7/1.22 ELSE a.sales_no_tax*:p7/1.19  END),2)" +
	        		" ,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.fee*:p1 WHEN a.country='jp' THEN a.fee*:p2 WHEN a.country='uk' THEN a.fee*:p3 WHEN locate('com',a.`country`)>0 THEN a.fee*:p4 WHEN a.country='mx' THEN a.fee*:p5 WHEN a.country='cn' THEN a.fee*:p6 ELSE a.fee*:p7 END),2)" +
	        		" ,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.fee_other*:p1 WHEN a.country='jp' THEN a.fee_other*:p2 WHEN a.country='uk' THEN a.fee_other*:p3 WHEN locate('com',a.`country`)>0 THEN a.fee_other*:p4 WHEN a.country='mx' THEN a.fee_other*:p5 WHEN a.country='cn' THEN a.fee_other*:p6 ELSE a.fee_other*:p7 END),2)" +
	        		" ,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.fee_quantity*:p10*"+ProductPrice.tranFee.get("ca")+" WHEN a.country='jp' THEN a.fee_quantity * :p10 * "+ProductPrice.tranFee.get("jp")+" WHEN a.country='de' THEN a.fee_quantity * :p10 * "+ProductPrice.tranFee.get("de")+" WHEN a.country='uk' THEN a.fee_quantity * :p10 * "+ProductPrice.tranFee.get("uk")+" WHEN locate('com',a.`country`)>0 THEN a.fee_quantity * :p10 *"+ProductPrice.tranFee.get("com")+" WHEN a.country='mx' THEN a.fee_quantity * :p10 * "+ProductPrice.tranFee.get("ca")+" WHEN a.country='es' THEN a.fee_quantity * :p10 * "+ProductPrice.tranFee.get("es")+" WHEN a.country='it' THEN a.fee_quantity * :p10 * "+ProductPrice.tranFee.get("it")+" WHEN a.country='fr' THEN a.fee_quantity * :p10 * "+ProductPrice.tranFee.get("fr")+" ELSE a.fee_quantity * :p10 * 14 END),2)" +
	        		" ,sum(fee_quantity),TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.refund*:p1 WHEN a.country='jp' THEN a.refund*:p2 WHEN a.country='uk' THEN a.refund*:p3 WHEN a.country='com' THEN a.refund*:p4 WHEN a.country='mx' THEN a.refund*:p5 WHEN a.country='cn' THEN a.refund*:p6 ELSE a.refund*:p7 END),2)" ;
			parameter = new Parameter(MathUtils.getRate("CAD", currencyType, rateRs),MathUtils.getRate("JPY", currencyType, rateRs),MathUtils.getRate("GBP", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("MXN", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("EUR", currencyType, rateRs),start,end,MathUtils.getRate("CNY", currencyType, rateRs));
		}else{
			parameter = new Parameter(MathUtils.getRate("CAD", currencyType, rateRs),MathUtils.getRate("JPY", currencyType, rateRs),MathUtils.getRate("GBP", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("MXN", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("EUR", currencyType, rateRs),start,end);
		}
		
        String sql = "SELECT CONCAT((case when (a.`product_name` is null or a.`product_name`='') then CONCAT(a.sku,'(未匹配)') else a.`product_name` end ),CASE  WHEN a.`color`='' || a.`color` is null THEN '' ELSE CONCAT('_',a.`color`) END) AS productName,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) as sales,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.sales*:p1 WHEN a.country='jp' THEN a.sales*:p2 WHEN a.country='uk' THEN a.sales*:p3 WHEN locate('com',a.`country`)>0 THEN a.sales*:p4 WHEN a.country='mx' THEN a.sales*:p5 WHEN a.country='cn' THEN a.sales*:p6 ELSE a.sales*:p7 END),2),sum(a.promotions_order-IFNULL(a.outside_order,0)+ifnull(a.coupon,0)),sum(a.flash_sales_order),max(max_order),sum(free_order),sum(ads_order),sum(review_volume),sum(support_volume),sum(ams_order),sum(outside_order),sum(ifnull(business_order,0)),SUBSTRING_INDEX(group_concat(a.product_attr order by FIELD(a.country,'de','uk','fr','jp','it','es','ca','mx','com','com1','com2','com3')),',',1)  " +
        		temp1+"  FROM amazoninfo_sale_report a  ";
        if(!"total".equals(lineType)){
			sql+=" left join psi_product  b on  a.`product_name` = CONCAT(b.`brand`,' ',b.`model`) and b.del_flag='0' left JOIN sys_dict d ON d.`value`=b.`type` AND d.del_flag='0' AND d.type='product_type' LEFT JOIN psi_product_type_group g ON g.`dict_id`=d.id  ";
		}
        sql+=" WHERE  a.`date`>=:p8 AND a.`date` <:p9  "+temp+" ";
        if(!"total".equals(lineType)){
        	if(!"unGrouped".equals(lineType)){
        		sql+=" and g.id='"+lineType+"' ";
        	}else{
        		sql+=" and g.id is null";
        	}
        	
        }
        sql+=" GROUP BY productName order by sales desc";
        List<Object[]>  list = saleReportDao.findBySql(sql,parameter);
       
        return list;
	}
	
	
	public Map<String,List<Object[]>> findProduct2(Date start,Date end,String country,String currencyType, Map<String, Float> rateRs,String orderType,String lineType){
		Map<String,List<Object[]>> map=Maps.newHashMap();
		String temp = "";
		if(!"total".equals(country) && !"eu".equals(country)&& !"en".equals(country)&&!"unEn".equals(country)){
			temp += "and  a.`country` ='"+country+"'";
		} else if ("eu".equals(country)) {
			temp = " and a.country in ('de','uk','es','fr','it') ";
		} else if ("unEn".equals(country)) {
			temp = " and a.country in ('de','jp','es','fr','it','mx') ";
		}else if ("en".equals(country)) {
			temp = " and a.country in ('uk','com','ca','com1') ";
		}
		
		if(StringUtils.isBlank(orderType)||orderType.startsWith("1")){
			temp+=" and a.order_type='1' ";
		}else if(orderType.startsWith("2")||orderType.startsWith("3")){
			String[] arr=orderType.split("-");
			temp+=" and a.order_type='"+arr[0]+"' and a.country='"+arr[1]+"' ";
		}else if(orderType.startsWith("4")){
			temp+=" and a.order_type='4' ";
		}else if(orderType.startsWith("5")){
			String[] arr=orderType.split("-");
			temp+=" and a.order_type='5' and a.country='"+arr[1]+"' ";
		}else if(orderType.startsWith("6")){
			temp+=" and a.order_type='6' ";
		}else if(orderType.startsWith("7")){
			temp+=" and a.order_type='7' ";
		}
		String temp1 = "";
		Parameter parameter = null;
		if((StringUtils.isBlank(orderType)||orderType.startsWith("1"))&&UserUtils.hasPermission("amazoninfo:profits:view")){
			temp1 = " ,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.sales_no_tax*:p1 WHEN a.country='jp' THEN a.sales_no_tax*:p2 WHEN a.country='uk' THEN a.sales_no_tax*:p3/1.2 WHEN (a.country='com'||a.country='com1') THEN a.sales_no_tax*:p4 WHEN a.country='mx' THEN a.sales_no_tax*:p5 WHEN a.country='cn' THEN a.sales_no_tax*:p6 WHEN a.country='es' THEN a.sales_no_tax*:p7/1.21  WHEN a.country='fr' THEN a.sales_no_tax*:p7/1.2 WHEN a.country='it' THEN a.sales_no_tax*:p7/1.22 ELSE a.sales_no_tax*:p7/1.19  END),2)" +
	        		" ,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.fee*:p1 WHEN a.country='jp' THEN a.fee*:p2 WHEN a.country='uk' THEN a.fee*:p3 WHEN (a.country='com'||a.country='com1') THEN a.fee*:p4 WHEN a.country='mx' THEN a.fee*:p5 WHEN a.country='cn' THEN a.fee*:p6 ELSE a.fee*:p7 END),2)" +
	        		" ,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.fee_other*:p1 WHEN a.country='jp' THEN a.fee_other*:p2 WHEN a.country='uk' THEN a.fee_other*:p3 WHEN (a.country='com'||a.country='com1') THEN a.fee_other*:p4 WHEN a.country='mx' THEN a.fee_other*:p5 WHEN a.country='cn' THEN a.fee_other*:p6 ELSE a.fee_other*:p7 END),2)" +
	        		" ,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.fee_quantity*:p10*"+ProductPrice.tranFee.get("ca")+" WHEN a.country='jp' THEN a.fee_quantity * :p10 * "+ProductPrice.tranFee.get("jp")+" WHEN a.country='de' THEN a.fee_quantity * :p10 * "+ProductPrice.tranFee.get("de")+" WHEN a.country='uk' THEN a.fee_quantity * :p10 * "+ProductPrice.tranFee.get("uk")+" WHEN (a.country='com'||a.country='com1') THEN a.fee_quantity * :p10 *"+ProductPrice.tranFee.get("com")+" WHEN a.country='mx' THEN a.fee_quantity * :p10 * "+ProductPrice.tranFee.get("ca")+" WHEN a.country='es' THEN a.fee_quantity * :p10 * "+ProductPrice.tranFee.get("es")+" WHEN a.country='it' THEN a.fee_quantity * :p10 * "+ProductPrice.tranFee.get("it")+" WHEN a.country='fr' THEN a.fee_quantity * :p10 * "+ProductPrice.tranFee.get("fr")+" ELSE a.fee_quantity * :p10 * 14 END),2)" +
	        		" ,sum(fee_quantity),TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.refund*:p1 WHEN a.country='jp' THEN a.refund*:p2 WHEN a.country='uk' THEN a.refund*:p3 WHEN a.country='com' THEN a.refund*:p4 WHEN a.country='mx' THEN a.refund*:p5 WHEN a.country='cn' THEN a.refund*:p6 ELSE a.refund*:p7 END),2)" ;
			parameter = new Parameter(MathUtils.getRate("CAD", currencyType, rateRs),MathUtils.getRate("JPY", currencyType, rateRs),MathUtils.getRate("GBP", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("MXN", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("EUR", currencyType, rateRs),start,end,MathUtils.getRate("CNY", currencyType, rateRs));
		}else{
			parameter = new Parameter(MathUtils.getRate("CAD", currencyType, rateRs),MathUtils.getRate("JPY", currencyType, rateRs),MathUtils.getRate("GBP", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("MXN", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("EUR", currencyType, rateRs),start,end);
		}
		
        String sql = "SELECT CONCAT((case when (a.`product_name` is null or a.`product_name`='') then CONCAT(a.sku,'(未匹配)') else a.`product_name` end ),CASE  WHEN a.`color`='' || a.`color` is null THEN '' ELSE CONCAT('_',a.`color`) END) AS productName,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) as sales,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.sales*:p1 WHEN a.country='jp' THEN a.sales*:p2 WHEN a.country='uk' THEN a.sales*:p3 WHEN (a.country='com'||a.country='com1') THEN a.sales*:p4 WHEN a.country='mx' THEN a.sales*:p5 WHEN a.country='cn' THEN a.sales*:p6 ELSE a.sales*:p7 END),2),sum(a.promotions_order-IFNULL(a.outside_order,0)),sum(a.flash_sales_order),max(max_order),sum(free_order),sum(ads_order),sum(review_volume),sum(support_volume),sum(ams_order),sum(outside_order),sum(ifnull(business_order,0)),SUBSTRING_INDEX(group_concat(distinct a.product_attr order by FIELD(a.country,'com','de','uk','fr','jp','it','es','ca','mx','com1'),FIELD(a.product_attr,'淘汰','新品','主力','普通')),',',1),DATE_FORMAT(a.`date`,'%Y%m%d') dates  " +
        		temp1+"  FROM amazoninfo_sale_report a  ";
        if(!"total".equals(lineType)){
			sql+=" left join psi_product  b on  a.`product_name` = CONCAT(b.`brand`,' ',b.`model`) and b.del_flag='0' left JOIN sys_dict d ON d.`value`=b.`type` AND d.del_flag='0' AND d.type='product_type' LEFT JOIN psi_product_type_group g ON g.`dict_id`=d.id  ";
		}
        sql+=" WHERE  a.`date`>=:p8 AND a.`date` <:p9  "+temp+" ";
        if(!"total".equals(lineType)){
        	if(!"unGrouped".equals(lineType)){
        		sql+=" and g.id='"+lineType+"' ";
        	}else{
        		sql+=" and g.id is null";
        	}
        	
        }
        sql+=" GROUP BY productName,dates order by sales desc";
        List<Object[]>  list = saleReportDao.findBySql(sql,parameter);
        for (Object[] obj: list) {
        	List<Object[]> tempList=map.get(obj[14].toString());
        	if(tempList==null){
        		tempList=Lists.newArrayList();
        		map.put(obj[14].toString(),tempList);
        	}
        	tempList.add(obj);
		}
        return map;
	}
	
	public Date getMaxDateFee(String country){
		String temp  = "";
		if(!"total".equals(country) && !"eu".equals(country)){
			temp += "and  a.`country` ='"+country+"'";
		} else if ("eu".equals(country)) {
			temp = " and a.country in ('de','uk','es','fr','it') ";
		}
		String sql = "SELECT MAX(a.`date`) FROM amazoninfo_sale_report a WHERE a.`fee_quantity` >0 "+temp ;
		 List<Object>  list = saleReportDao.findBySql(sql,null);
		return (Date)list.get(0);
	}
	
	
	public Map<String, Map<String,Float>> getProductPriceAndTranGw(String currencyType, Map<String, Float> rateRs){
		String priceSql = "SELECT CONCAT(b.`brand`,' ',b.`model`),b.`color`,Max(TRUNCATE((CASE WHEN a.`currency_type` = 'CNY'  THEN a.`price`*(100+c.`tax_rate`)/(100)*:p1 ELSE a.`price`*(100+c.`tax_rate`)/(100) END),2)*:p2)  AS realprice,TRUNCATE(CASE WHEN b.volume_ratio <= 167  THEN 167*b.box_volume/b.pack_quantity ELSE b.gw/b.pack_quantity  END,2) AS tranGw ,b.id FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c WHERE a.`product_id`=b.`id` AND a.`supplier_id`=c.id AND a.`del_flag`='0' AND b.`del_flag` = '0' AND a.`price` >0 AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END ) GROUP BY a.`product_id` ORDER BY a.`price` ASC   ";
		String partSql = "SELECT CONCAT(a.`product_id`,'_',a.`color`),TRUNCATE(SUM(a.`mixture_ratio`*(CASE WHEN b.`price` IS NULL THEN b.rmb_price*:p1 ELSE price END)*:p2),2) AS price FROM psi_product_parts a, psi_parts b WHERE a.`parts_id` = b.`id` GROUP BY a.`product_id`,a.`color` HAVING price >0";
		List<Object[]>  list = saleReportDao.findBySql(priceSql, new Parameter(MathUtils.getRate("CNY", "USD", rateRs),MathUtils.getRate("USD", currencyType, rateRs)));
		List<Object[]>  list1 = saleReportDao.findBySql(partSql, new Parameter(MathUtils.getRate("CNY", "USD", rateRs),MathUtils.getRate("USD", currencyType, rateRs)));
	    Map<String, Float> partMap = Maps.newHashMap();
	    for (Object[] objects : list1) {
	    	partMap.put(objects[0].toString(), Float.parseFloat(objects[1].toString()));
		}
		Map<String, Map<String,Float>> map = Maps.newHashMap();
		for (Object[] objects : list) {
			String productName = objects[0].toString();
			String color = objects[1].toString();
			Float price = Float.parseFloat(objects[2].toString());
			Float gw = Float.parseFloat(objects[3].toString());
			String pid = objects[4].toString();
			for (String colorStr : color.split(",")) {
				String key = productName;
				/*if(key.contains("-10")){
					System.out.println("ssss");
				}*/
				String key1 = pid+"_"+colorStr;
				Float price1 = price;
				if(partMap.get(key1)!=null){
					price1 = price1+partMap.get(key1);
				}
				if(colorStr.trim().length()>0){
					key = key+"_"+colorStr;
				}
				Map<String,Float> temp = map.get(key);
				if(temp==null){
					temp = Maps.newHashMap();
					map.put(key, temp);
				}
				temp.put("price", price1);
				temp.put("gw", gw);
				temp.put("parts",partMap.get(key1)==null?0:partMap.get(key1));
			}
		}
		return map;
	}
	
	public Map<String, Map<String,Float>> getProductPriceAndTranGwNoTax(String currencyType, Map<String, Float> rateRs){
		String priceSql = "SELECT CONCAT(b.`brand`,' ',b.`model`),b.`color`,MIN(TRUNCATE((CASE WHEN a.`currency_type` = 'CNY'  THEN a.`price`*(100+c.`tax_rate`)/(CASE WHEN b.`tax_refund` IS NULL THEN 117 ELSE   b.`tax_refund`+100 END)*:p1 ELSE a.`price`*(100+c.`tax_rate`)/(CASE WHEN b.`tax_refund` IS NULL THEN 117 ELSE   b.`tax_refund`+100 END) END),2)*:p2)  AS realprice,TRUNCATE(CASE WHEN b.volume_ratio <= 167  THEN 167*b.box_volume/b.pack_quantity ELSE b.gw/b.pack_quantity  END,2) AS tranGw ,b.id FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c WHERE a.`product_id`=b.`id` AND a.`supplier_id`=c.id AND a.`del_flag`='0' AND b.`del_flag` = '0' AND a.`price` >0 AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END ) GROUP BY a.`product_id`,a.`supplier_id` ORDER BY a.`price` ASC   ";
		String partSql = "SELECT CONCAT(a.`product_id`,'_',a.`color`),TRUNCATE(SUM(a.`mixture_ratio`*(CASE WHEN b.`price` IS NULL THEN b.rmb_price*:p1 ELSE price END)*:p2),2) AS price FROM psi_product_parts a, psi_parts b WHERE a.`parts_id` = b.`id` GROUP BY a.`product_id`,a.`color` HAVING price >0";
		List<Object[]>  list = saleReportDao.findBySql(priceSql, new Parameter(MathUtils.getRate("CNY", "USD", rateRs),MathUtils.getRate("USD", currencyType, rateRs)));
		List<Object[]>  list1 = saleReportDao.findBySql(partSql, new Parameter(MathUtils.getRate("CNY", "USD", rateRs),MathUtils.getRate("USD", currencyType, rateRs)));
	    Map<String, Float> partMap = Maps.newHashMap();
	    for (Object[] objects : list1) {
	    	partMap.put(objects[0].toString(), Float.parseFloat(objects[1].toString()));
		}
		Map<String, Map<String,Float>> map = Maps.newHashMap();
		for (Object[] objects : list) {
			String productName = objects[0].toString();
			String color = objects[1].toString();
			Float price = Float.parseFloat(objects[2].toString());
			Float gw = Float.parseFloat(objects[3].toString());
			String pid = objects[4].toString();
			for (String colorStr : color.split(",")) {
				String key = productName;
				/*if(key.contains("-10")){
					System.out.println("ssss");
				}*/
				String key1 = pid+"_"+colorStr;
				Float price1 = price;
				if(partMap.get(key1)!=null){
					price1 = price1+partMap.get(key1);
				}
				if(colorStr.trim().length()>0){
					key = key+"_"+colorStr;
				}
				Map<String,Float> temp = map.get(key);
				if(temp==null){
					temp = Maps.newHashMap();
					map.put(key, temp);
				}
				temp.put("price", price1);
				temp.put("gw", gw);
				temp.put("parts",partMap.get(key1)==null?0:partMap.get(key1));
			}
		}
		return map;
	}
	
	public Map<String,Float> getProductPrice(String currencyType, Map<String, Float> rateRs){
		String priceSql = "SELECT CONCAT(b.`brand`,' ',b.`model`),b.`color`,MIN(TRUNCATE((CASE WHEN a.`currency_type` = 'CNY'  THEN a.`price`*((100+c.`tax_rate`)/100)*:p1 ELSE a.`price`*((100+c.`tax_rate`)/100) END),2)*:p2)  AS realprice,b.id FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c WHERE a.`product_id`=b.`id` AND a.`supplier_id`=c.id AND a.`price` >0 AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END ) GROUP BY a.`product_id`";
		String partSql = "SELECT CONCAT(a.`product_id`,'_',a.`color`),TRUNCATE(SUM(a.`mixture_ratio`*(CASE WHEN b.`price` IS NULL THEN b.rmb_price*:p1 ELSE price END)*:p2),2) AS price FROM psi_product_parts a, psi_parts b WHERE a.`parts_id` = b.`id` GROUP BY a.`product_id`,a.`color` HAVING price >0";
		List<Object[]>  list = saleReportDao.findBySql(priceSql, new Parameter(MathUtils.getRate("CNY", "USD", rateRs),MathUtils.getRate("USD", currencyType, rateRs)));
		List<Object[]>  list1 = saleReportDao.findBySql(partSql, new Parameter(MathUtils.getRate("CNY", "USD", rateRs),MathUtils.getRate("USD", currencyType, rateRs)));
	    Map<String, Float> partMap = Maps.newHashMap();
	    for (Object[] objects : list1) {
	    	partMap.put(objects[0].toString(), Float.parseFloat(objects[1].toString()));
		}
		Map<String,Float> map = Maps.newHashMap();
		for (Object[] objects : list) {
			String productName = objects[0].toString();
			String color = objects[1].toString();
			Float price = Float.parseFloat(objects[2].toString());
			String pid = objects[3].toString();
			for (String colorStr : color.split(",")) {
				String key = productName;
				String key1 = pid+"_"+colorStr;
				Float price1 = price;
				if(partMap.get(key1)!=null){
					price1 = price1+partMap.get(key1);
				}
				if(colorStr.trim().length()>0){
					key = key+"_"+colorStr;
				}
				map.put(key, price1);
			}
		}
		return map;
	}
	
	
	public Map<String,Object[]> getProductTranVatPcent(){
		String sql = "SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME ,IFNULL(a.`jp_custom_duty`,0),IFNULL(a.`eu_custom_duty`,0),IFNULL(a.`us_custom_duty`,0),IFNULL(a.`ca_custom_duty`,0),IFNULL(a.`mx_custom_duty`,0) "+
          " FROM psi_product a JOIN mysql.help_topic b "+
          " ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) "+
          " WHERE a.`del_flag`='0' ";
		List<Object[]>  list = saleReportDao.findBySql(sql,null);
		Map<String,Object[]> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(), objects);
		}
		return rs;
	}
	
	public Map<String, Map<String,Integer>> findAllCommission(){
		String sql = "SELECT  "+
					 "   a.country,  b.`sku`,  "+
					 "   SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT  TRUNCATE(  "+
					 "     ROUND(  "+
					 "       IFNULL(b.`commission`, 0) / (d.`item_price`-IFNULL(d.`promotion_discount`,0)) ,  "+
					 "       2  "+
					 "     ) * - 100,  "+
					 "     0  "+
					 "   )  ORDER BY b.commission, c.`purchase_date` DESC),',',1)  "+
					 " FROM  "+
					 "   amazoninfo_financial a,  "+
					 "   amazoninfo_financial_item b,  "+
					 "   amazoninfo_order c,  "+
					 "   amazoninfo_orderitem d  "+
					 "  WHERE a.`id` = b.`order_id`   "+
					 "   AND a.`amazon_order_id` = c.`amazon_order_id`  "+
					 "   AND c.id = d.`order_id`  "+
					 "   AND c.`number_of_items_shipped` = 1  "+
					 "   AND a.`type` = 'order'   "+
					 "   AND b.`principal`>0  "+
					 "   AND a.`add_time` > DATE_ADD(CURDATE(), INTERVAL - 1 MONTH)   "+
					 "   AND a.`marketplace_name` != 'Non-Amazon'   "+
				//	 "   AND c.`fulfillment_channel` = 'AFN'  "+
					 "   AND ABS(b.`commission`)>0  "+
					 "   AND d.`quantity_shipped` = 1  "+
					 "   AND c.`order_total`>0   "+
					 "   GROUP BY  b.`sku`,a.country  ";
		List<Object[]>  list = saleReportDao.findBySql(sql,null);
		Map<String, Map<String,Integer>> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			String country = objects[0].toString();
			 Map<String,Integer> map = rs.get(country);
			if(map == null){
				map = Maps.newHashMap();
				rs.put(country, map);
			}
			int pcent = 0;
			try {
				pcent = Integer.parseInt(objects[2].toString());
			} catch (Exception e) {}
			if(pcent>0&&pcent<50){
				map.put(objects[1].toString(),pcent);
			}
		}
		return rs;
	}
	
	//美金
	public Map<String, Map<String,Float>> findAllFbaFee(){
		//String sql = "SELECT DISTINCT  a.`country`,b.`sku`,abs(ROUND((CASE WHEN a.country='ca' THEN SUM(IFNULL(b.`fba_per_unit_fulfillment_fee`,0)+IFNULL(b.`fba_weight_based_fee`,0))/SUM(b.`quantity`)*:p1 WHEN a.country='jp' THEN SUM(IFNULL(b.`fba_per_unit_fulfillment_fee`,0)+IFNULL(b.`fba_weight_based_fee`,0))/SUM(b.`quantity`)*:p2 WHEN a.country='uk' THEN SUM(IFNULL(b.`fba_per_unit_fulfillment_fee`,0)+IFNULL(b.`fba_weight_based_fee`,0))/SUM(b.`quantity`)*:p3 WHEN a.country='com' THEN (SUM(IFNULL(b.`fba_per_unit_fulfillment_fee`,0)+IFNULL(b.`fba_weight_based_fee`,0))/SUM(b.`quantity`)) WHEN a.country='mx' THEN SUM(IFNULL(b.`fba_per_unit_fulfillment_fee`,0)+IFNULL(b.`fba_weight_based_fee`,0))/SUM(b.`quantity`)*:p4 ELSE SUM(IFNULL(b.`fba_per_unit_fulfillment_fee`,0)+IFNULL(b.`fba_weight_based_fee`,0))/SUM(b.`quantity`)*:p5 END),2)) FROM settlementreport_order a ,settlementreport_item b ,amazoninfo_order c  WHERE a.`id` = b.`order_id`  AND a.`amazon_order_id` = c.`amazon_order_id`  AND a.`type` = 'order'   AND  a.`add_time` >DATE_ADD(CURDATE(),INTERVAL -1 MONTH)  AND a.`merchant_fulfillment_id` = 'AFN' AND c.`number_of_items_shipped` = 1 GROUP BY b.`sku`,a.`country`,a.`amazon_order_id` ORDER BY a.`posted_date` ";
		String sql = "SELECT DISTINCT  a.`country`,b.`sku`,abs(ROUND((CASE WHEN a.country='ca' THEN SUM(IFNULL(b.`fba_per_unit_fulfillment_fee`,0)+IFNULL(b.`fba_weight_based_fee`,0))/SUM(b.`quantity`)*:p1 WHEN a.country='jp' THEN SUM(IFNULL(b.`fba_per_unit_fulfillment_fee`,0)+IFNULL(b.`fba_weight_based_fee`,0))/SUM(b.`quantity`)*:p2 WHEN a.country='uk' THEN SUM(IFNULL(b.`fba_per_unit_fulfillment_fee`,0)+IFNULL(b.`fba_weight_based_fee`,0))/SUM(b.`quantity`)*:p3 WHEN a.country='com' THEN (SUM(IFNULL(b.`fba_per_unit_fulfillment_fee`,0)+IFNULL(b.`fba_weight_based_fee`,0))/SUM(b.`quantity`)) WHEN a.country='mx' THEN SUM(IFNULL(b.`fba_per_unit_fulfillment_fee`,0)+IFNULL(b.`fba_weight_based_fee`,0))/SUM(b.`quantity`)*:p4 ELSE SUM(IFNULL(b.`fba_per_unit_fulfillment_fee`,0)+IFNULL(b.`fba_weight_based_fee`,0))/SUM(b.`quantity`)*:p5 END),2)) FROM amazoninfo_financial a ,amazoninfo_financial_item b ,amazoninfo_order c  WHERE a.`id` = b.`order_id`  AND a.`amazon_order_id` = c.`amazon_order_id`  AND a.`type` = 'order'   AND  a.`add_time` >DATE_ADD(CURDATE(),INTERVAL -1 MONTH)  AND c.`fulfillment_channel` = 'AFN' AND c.`number_of_items_shipped` = 1 GROUP BY b.`sku`,a.`country`,a.`amazon_order_id` ORDER BY a.`posted_date` ";
		List<Object[]>  list = saleReportDao.findBySql(sql,new Parameter(AmazonProduct2Service.getRateConfig().get("CAD/USD"),AmazonProduct2Service.getRateConfig().get("JPY/USD"),AmazonProduct2Service.getRateConfig().get("GBP/USD"),AmazonProduct2Service.getRateConfig().get("MXN/USD"),AmazonProduct2Service.getRateConfig().get("EUR/USD")));
		Map<String, Map<String,Float>> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			String country = objects[0].toString();
			 Map<String,Float> map = rs.get(country);
			if(map == null){
				map = Maps.newHashMap();
				rs.put(country, map);
			}
			map.put(objects[1].toString(), Float.parseFloat(objects[2].toString()));
		}
		return rs;
	}
	
	//查单产品的销量
	public Map<String,Integer> getSalesByDate(Date dateStar,Date dateEnd,String country){
		String sql ="";
		Parameter para = null;
		if(StringUtils.isNotEmpty(country)){
			sql="SELECT CASE WHEN a.`color`!='' THEN CONCAT( a.`product_name`,'_',a.`color`) ELSE a.`product_name` END AS aa  ,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) FROM amazoninfo_sale_report AS a WHERE a.order_type='1' and a.`date` BETWEEN :p1 AND :p2 AND a.`country`=:p3  GROUP BY a.`product_name`,a.color";
			para = new Parameter(dateStar,dateEnd,country);
		}else{
			sql="SELECT CASE WHEN a.`color`!='' THEN CONCAT( a.`product_name`,'_',a.`color`) ELSE a.`product_name` END AS aa ,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) FROM amazoninfo_sale_report AS a WHERE a.order_type='1' and a.`date` BETWEEN :p1 AND :p2  GROUP BY a.`product_name`,a.color";
			para = new Parameter(dateStar,dateEnd);
		}
		
		Map<String,Integer> map = Maps.newHashMap(); 
		List<Object[]> list = this.saleReportDao.findBySql(sql,para);
		if(list!=null&&list.size()>0){
			for(Object[] obj : list){
				if(obj[0]!=null){
					map.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
				}
			}
		}
		
		return map;
		
	}
	
	
	
   //产品线[日期 /数据]
	public Map<String,Map<String, SaleReport>> getProductLineSales(SaleReport saleReport, Map<String, Float> rateRs){
		String currencyType = saleReport.getCurrencyType(); //统计的货币类型（EUR/USD）
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		} 
		
       String sql ="SELECT line,SUM(sales_volume),SUM(a.`sure_sales_volume`),TRUNCATE(SUM(a.`sales`),2),TRUNCATE(SUM(a.`sure_sales`),2),dates,SUM(a.`real_sales_volume`),TRUNCATE(SUM(a.`real_sales`),2) FROM (SELECT IFNULL(g.`id`,'unGrouped') line,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) sales_volume,SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)) sure_sales_volume,(CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
				+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
				+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
				MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ) sales,(CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sure_sales`)*"
				+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sure_sales`)*"
				+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sure_sales`)*"+
				MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ) sure_sales,DATE_FORMAT(a.`date`,"+typeSql+") dates ,SUM(a.`real_sales_volume`*IFNULL(a.`pack_num`,1)) real_sales_volume, (CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
				+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
				+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
				MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END) real_sales FROM amazoninfo_sale_report a  join psi_product  b on  a.`product_name` = CONCAT(b.`brand`,' ',b.`model`) AND b.`del_flag`='0'   JOIN sys_dict d ON d.`value`=b.`type` AND d.del_flag='0' AND d.type='product_type' JOIN psi_product_type_group g ON g.`dict_id`=d.id  WHERE a.order_type='1' and  a.`date`>=:p1 AND a.`date`<=:p2 GROUP BY line,dates,a.country )a GROUP BY line,dates  ";
        List<Object[]> list = saleReportDao.findBySql(sql, new Parameter(start,end));
		
		Map<String,Map<String, SaleReport>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String line = objs[0].toString(); 
			Integer salesVolume = ((BigDecimal)objs[1]).intValue();
			Integer sureSalesVolume = ((BigDecimal)objs[2]).intValue();
			Float sales = ((BigDecimal)objs[3]).floatValue();
			Float sureSales = ((BigDecimal)objs[4]).floatValue();
			String date = objs[5].toString(); 
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Integer realSalesVolume = ((BigDecimal)objs[6]).intValue();
			Float realSales = ((BigDecimal)objs[7]).floatValue();
			
			Map<String, SaleReport> datas = rs.get(line);
			if(datas==null){
				datas = Maps.newLinkedHashMap();
				rs.put(line, datas);
			}
			datas.put(date, new SaleReport(sales,sureSales,realSales,salesVolume,sureSalesVolume,realSalesVolume));
			Map<String, SaleReport> datas1 = rs.get("total");
			if(datas1==null){
				datas1 = Maps.newLinkedHashMap();
				rs.put("total", datas1);
			}
			SaleReport saleEntry = datas1.get(date);
			if(saleEntry==null){
				saleEntry = new SaleReport(0f,0f,0f,0,0,0);
				datas1.put(date, saleEntry);
			}
			saleEntry.setSales(sales+saleEntry.getSales());
			saleEntry.setSalesVolume(salesVolume+saleEntry.getSalesVolume());
			saleEntry.setSureSales(sureSales+saleEntry.getSureSales());
			saleEntry.setSureSalesVolume(sureSalesVolume+saleEntry.getSureSalesVolume());
			saleEntry.setRealSales(realSales+saleEntry.getRealSales());
			saleEntry.setRealSalesVolume(realSalesVolume+saleEntry.getRealSalesVolume());	
		}
		return rs;
	}

	public SaleReport getSaleReport(String productName, String color, String country) {
		DetachedCriteria dc = saleReportDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(productName)) {
			dc.add(Restrictions.eq("productName", productName));
		}
		if (StringUtils.isNotEmpty(color)) {
			dc.add(Restrictions.eq("color", color));
		}
		if (StringUtils.isNotEmpty(country)) {
			dc.add(Restrictions.eq("country", country));
		}
		dc.addOrder(Order.asc("date"));
		List<SaleReport> list = saleReportDao.find(dc);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 查询需要预测的产品(已开售未淘汰的都预测)
	 * @return
	 */
	public Map<String,List<String>> findForForecast(){
		Map<String,List<String>> map = Maps.newHashMap();
		String sql ="SELECT DISTINCT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`),b.`TYPE` "+
				" FROM psi_product_eliminate a,psi_product b WHERE a.`product_id` = b.`id` "+
				" AND a.`added_month` IS NOT NULL AND b.`del_flag`='0' AND a.`del_flag`='0'"+
				" AND a.`product_name` NOT IN ('Inateck Old','Inateck other') AND a.`is_sale` != '4'";
		List<Object[]> list = this.saleReportDao.findBySql(sql,null);
		for(Object[] obj : list){
			String key = obj[1].toString();
			List<String> lists = map.get(key);
			if(lists==null){
				lists = Lists.newArrayList();
				map.put(key, lists);
			}
			lists.add(obj[0].toString());
		}
		return map;
	}
	
	
	public Map<String,List<String>> findByCForecast(){
		Map<String,List<String>> map = Maps.newHashMap();
		String sql ="SELECT DISTINCT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`),b.`TYPE` FROM psi_product_eliminate a,psi_product b WHERE a.`product_id` = b.`id` AND a.`added_month` <= DATE_ADD(CURDATE(),INTERVAL -16 MONTH) AND a.`product_name` NOT IN ('Inateck Old','Inateck other') AND a.`is_sale` != '4'";
		List<Object[]> list = this.saleReportDao.findBySql(sql,null);
		for(Object[] obj : list){
			String key = obj[1].toString();
			List<String> lists = map.get(key);
			if(lists==null){
				lists = Lists.newArrayList();
				map.put(key, lists);
			}
			lists.add(obj[0].toString());
		}
		return map;
	}
	
	public List<String> findByForecast(){
		List<String> rs = Lists.newArrayList();
		String sql ="SELECT DISTINCT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) FROM psi_product_eliminate a WHERE  a.`product_name` NOT IN ('Inateck Old','Inateck other') AND a.`is_sale` != '4' AND a.`is_new` = '0'";
		rs = this.saleReportDao.findBySql(sql,null);
		return rs;
	}
	
	/**
	 * 销量分月指数,供预测(支持总量指数、分类型指数、单品指数)
	 * @param type	产品类型
	 * @param productName	产品名称
	 * @return	参数都为空时返回总量指数,否则以产品名优先(即都不为空时忽略type)
	 */
	public float[] findSalesIndex(String type , String productName){
		List<Object[]> list = Lists.newArrayList();
		if (StringUtils.isEmpty(type) && StringUtils.isEmpty(productName)) {	//算总量
			String sql ="SELECT a.`type`, DATE_FORMAT(a.`date`,'%Y%m') AS MONTH,SUM(a.`sales_volume`)" +
					" FROM amazoninfo_sale_report_type a " +
					" WHERE a.`date` >= DATE_ADD(CURDATE(),INTERVAL -15 MONTH) " +
					" AND a.`order_type` = '1' AND a.`sales_volume`>0 GROUP BY MONTH ORDER BY MONTH ";
			list = this.saleReportDao.findBySql(sql);
		} else if (StringUtils.isNotEmpty(productName)) {	//单品指数
			String sql ="SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) AS NAMES," +
					" DATE_FORMAT(a.`date`,'%Y%m') AS MONTH,SUM(a.`sales_volume`) FROM amazoninfo_sale_report a " +
					" WHERE a.`date` >= DATE_ADD(CURDATE(),INTERVAL -15 MONTH) AND " +
					" CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) = :p1 " +
					" AND a.`order_type` = '1' and a.`sales_volume`>0 GROUP BY NAMES,MONTH order by names,month ";
			list = this.saleReportDao.findBySql(sql, new Parameter(productName));
		} else {	//品类指数
			String sql ="SELECT a.`type`, DATE_FORMAT(a.`date`,'%Y%m') AS MONTH,SUM(a.`sales_volume`) " +
					" FROM amazoninfo_sale_report_type a " +
					" WHERE a.`date` >= DATE_ADD(CURDATE(),INTERVAL -15 MONTH) AND a.`type` = :p1 " +
					" AND a.`order_type` = '1' AND a.`sales_volume`>0 GROUP BY a.`type`,MONTH ORDER BY MONTH ";
			list = this.saleReportDao.findBySql(sql, new Parameter(type));
		}
		//周期必须达到16个月,否则取样数据不能做参考
		if (list == null || list.size() < 16) {
			return null;
		}
		List<Integer> nums = Lists.newArrayList();
		int num = 0 ;
		for (int i = 0; i < list.size()-1; i++) {
			if (i < 3) {	//取样后过滤初期销量以及当月销量(单月销量需要推算)
				continue;
			}
			Object[] obj = list.get(i);
			if (i==3) {//上年度当月销量作为基数
				num = Integer.parseInt(obj[2].toString());
			}
			nums.add(Integer.parseInt(obj[2].toString()));
		}
		if (num == 0) {
			return null;
		}
		
		float[] zisu = new float[12];
		zisu[0] = 1;
		zisu[1] = (float)nums.get(1)/num;
		zisu[2] = (float)nums.get(2)/num;
		zisu[3] = (float)nums.get(3)/num;
		zisu[4] = (float)nums.get(4)/num;
		zisu[5] = (float)nums.get(5)/num;
		zisu[6] = (float)nums.get(6)/num;
		zisu[7] = (float)nums.get(7)/num;
		zisu[8] = (float)nums.get(8)/num;
		zisu[9] = (float)nums.get(9)/num;
		zisu[10] = (float)nums.get(10)/num;
		zisu[11] = (float)nums.get(11)/num;
		for (float f : zisu) {
			if (f > 3f || f < 0.3f) {	//波动太大,不能作为指数参考
				return null;
			}
		}
		return zisu;
	}
	
	//查产品的销量
	public float [] findSalesByForecast(String type ,List<String> products){
		String sql ="SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) AS NAMES,DATE_FORMAT(a.`date`,'%Y%m') AS MONTH,SUM(a.`real_order`) FROM amazoninfo_sale_report a WHERE a.`date` >= DATE_ADD(CURDATE(),INTERVAL -14 MONTH) AND CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) IN :p1 AND a.`order_type` = '1' and a.`real_order`>0 GROUP BY NAMES,MONTH order by names,month ";
		List<Object[]> list = this.saleReportDao.findBySql(sql,new Parameter(products));
		Set<List<Integer>> data = Sets.newHashSet();
		String name = "";
		List<Integer> nums = Lists.newArrayList();
		int i = 0;
		int max = 0 ;
		for(Object[] obj : list){
			String temp = obj[0].toString();
			if(name.equals(temp)){
				if(i<=12){
					nums.add(Integer.parseInt(obj[2].toString()));
					i++;
					if(i==13){
						int tempInt = Math.round(MathUtils.getAverage(nums.toArray(new Integer[0])));
						if(tempInt>max){
							max = tempInt;
						}
						nums.add(0, tempInt);
						data.add(nums);
					}
				}
			}else{
				 name = temp;
				 nums =  Lists.newArrayList();
				 i=0;
			}
		}
		int rs1= 0;
		int rs2= 0;
		int rs3= 0;
		int rs4= 0;
		int rs5= 0;
		int rs6= 0;
		int rs7= 0;
		int rs8= 0;
		int rs9= 0;
		int rs10= 0;
		int rs11= 0;
		int rs12= 0;
		int rs13= 0;
		
		for (List<Integer> intAry : data) {
			float num = (float)max/intAry.get(0);
			rs1+=intAry.get(1)*num;
			rs2+=intAry.get(2)*num;
			rs3+=intAry.get(3)*num;
			rs4+=intAry.get(4)*num;
			rs5+=intAry.get(5)*num;
			rs6+=intAry.get(6)*num;
			rs7+=intAry.get(7)*num;
			rs8+=intAry.get(8)*num;
			rs9+=intAry.get(9)*num;
			rs10+=intAry.get(10)*num;
			rs11+=intAry.get(11)*num;
			rs12+=intAry.get(12)*num;
			rs13+=intAry.get(13)*num;
		}
		
		float [] zisu = new float[12];
		zisu[0] = (float)rs2/rs1;
		zisu[1] = (float)rs3/rs2;
		zisu[2] = (float)rs4/rs3;
		zisu[3] = (float)rs5/rs4;
		zisu[4] = (float)rs6/rs5;
		zisu[5] = (float)rs7/rs6;
		zisu[6] = (float)rs8/rs7;
		zisu[7] = (float)rs9/rs8;
		zisu[8] = (float)rs10/rs9;
		zisu[9] = (float)rs11/rs10;
		zisu[10] = (float)rs12/rs11;
		zisu[11] = (float)rs13/rs12;
		return zisu;
	}
	
	/**
	 * 产品当月各平台销量比例
	 * @param productName
	 * @param fanOuFlag	完全泛欧  1：uk以外4国泛欧  2：不能泛欧
	 * @return
	 */
	public Map<String,Float> getPcentByName(String productName, String fanOuFlag, Date firstDayOfMonth){
		Map<String,Float> rs = Maps.newHashMap();
		if("2".equals(fanOuFlag)){
			String sql = "SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) AS NAMES,SUM(CASE WHEN a.`country`='ca' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(CASE WHEN a.`country`='es' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(CASE WHEN a.`country`='it' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(CASE WHEN a.`country`='fr' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(CASE WHEN a.`country`='de' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(CASE WHEN a.`country`='uk' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(CASE WHEN a.`country`='com' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(CASE WHEN a.`country`='jp' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(CASE WHEN a.`country`='com1' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(CASE WHEN a.`country`='com2' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) FROM amazoninfo_sale_report a WHERE a.`product_name` IS NOT NULL " +
					" AND a.`date` >= :p1 "+
					" AND DATE_FORMAT(a.`date`,'%Y%m%d') < DATE_FORMAT(DATE_ADD(CURDATE(),INTERVAL -1 DAY),'%Y%m%d') "+
					" AND a.`sales`>a.`sales_volume`*IFNULL(a.`pack_num`,1) AND CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`)=:p2 AND a.`order_type` = '1' GROUP BY NAMES";
			List<Object[]> list = this.saleReportDao.findBySql(sql,new Parameter(firstDayOfMonth,productName));
			if(list.size()==1){
				Object[] data = list.get(0);
				rs.put("ca", Float.parseFloat(data[1].toString()));
				rs.put("es", Float.parseFloat(data[2].toString()));
				rs.put("it", Float.parseFloat(data[3].toString()));
				rs.put("fr", Float.parseFloat(data[4].toString()));
				rs.put("de", Float.parseFloat(data[5].toString()));
				rs.put("uk", Float.parseFloat(data[6].toString()));
				rs.put("com", Float.parseFloat(data[7].toString()));
				rs.put("jp", Float.parseFloat(data[8].toString()));
				rs.put("com1", Float.parseFloat(data[9].toString()));
				//locate('com',a.`country`)>0
				rs.put("com2", Float.parseFloat(data[10].toString()));
			}
		}else if ("1".equals(fanOuFlag)) {
			String sql = "SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) AS NAMES,"+
					" SUM(CASE WHEN a.`country`='ca' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),"+
					" SUM(CASE WHEN a.`country`='de' || a.`country`='fr' || a.`country`='it' || a.`country`='es' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),"+
					" SUM(CASE WHEN a.`country`='uk' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),"+
					" SUM(CASE WHEN a.`country`='com' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),"+
					" SUM(CASE WHEN a.`country`='jp' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),"+
					" SUM(CASE WHEN a.`country`='com1' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),"+
					" SUM(CASE WHEN a.`country`='com2' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) "+
					" FROM amazoninfo_sale_report a WHERE a.`product_name` IS NOT NULL " +
					" AND a.`date` >= :p1 "+
					" AND DATE_FORMAT(a.`date`,'%Y%m%d') < DATE_FORMAT(DATE_ADD(CURDATE(),INTERVAL -1 DAY),'%Y%m%d') "+
					" AND a.`sales`>a.`sales_volume` AND CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`)=:p2 AND a.`order_type` = '1'  GROUP BY NAMES";
			List<Object[]> list = this.saleReportDao.findBySql(sql,new Parameter(firstDayOfMonth,productName));
			if(list.size()==1){
				Object[] data = list.get(0);
				rs.put("ca", Float.parseFloat(data[1].toString()));
				rs.put("de", Float.parseFloat(data[2].toString()));
				rs.put("uk", Float.parseFloat(data[3].toString()));
				rs.put("com", Float.parseFloat(data[4].toString()));
				rs.put("jp", Float.parseFloat(data[5].toString()));
				rs.put("com1", Float.parseFloat(data[6].toString()));
				rs.put("com2", Float.parseFloat(data[7].toString()));
			}
		}else{
			String sql = "SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) AS NAMES,SUM(CASE WHEN a.`country`='ca' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(CASE WHEN a.`country`='de' || a.`country`='uk'  || a.`country`='fr' || a.`country`='it' || a.`country`='es' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(CASE WHEN a.`country`='com' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(CASE WHEN a.`country`='jp' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(CASE WHEN a.`country`='com1' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(CASE WHEN a.`country`='com2' THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END )/SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) FROM amazoninfo_sale_report a WHERE a.`product_name` IS NOT NULL " +
					 " AND a.`date` >= :p1 "+
					 " AND DATE_FORMAT(a.`date`,'%Y%m%d') < DATE_FORMAT(DATE_ADD(CURDATE(),INTERVAL -1 DAY),'%Y%m%d') "+
					 " AND a.`sales`>a.`sales_volume` AND CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`)=:p2 AND a.`order_type` = '1'  GROUP BY NAMES";
			List<Object[]> list = this.saleReportDao.findBySql(sql,new Parameter(firstDayOfMonth,productName));
			if(list.size()==1){
				Object[] data = list.get(0);
				rs.put("ca", Float.parseFloat(data[1].toString()));
				rs.put("de", Float.parseFloat(data[2].toString()));
				rs.put("com", Float.parseFloat(data[3].toString()));
				rs.put("jp", Float.parseFloat(data[4].toString()));
				rs.put("com1", Float.parseFloat(data[5].toString()));
				rs.put("com2", Float.parseFloat(data[6].toString()));
			}
		}
		return rs;
	}
	
	public Integer getLastMonthSaleByName(String productName){
		//增加a.`sales`>a.`sales_volume`条件限制类似8.7事件大量免单造成的销量波动影响预测数据
		String sql = "SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) AS NAMES , sum(a.`real_order`) FROM amazoninfo_sale_report a WHERE a.`product_name` IS NOT NULL AND DATE_FORMAT(a.`date`,'%Y%m') = DATE_FORMAT(DATE_ADD(CURDATE(),INTERVAL -1 MONTH),'%Y%m')"+
					 " AND a.`sales`>a.`sales_volume`*IFNULL(a.`pack_num`,1) AND CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`)=:p1 AND a.`order_type` = '1' GROUP BY NAMES";
		List<Object[]> list = this.saleReportDao.findBySql(sql,new Parameter(productName));
		Integer rs = 0;
		if(list.size()==1){
			rs = Integer.parseInt(list.get(0)[1].toString());
		}
		return rs;
	}
	
	/**
	 * 获取推算的月销量(根据当前月日均销推算)
	 * @param productName
	 * @return
	 */
	public Integer getCurrMonthSaleByName(String productName, Date firstDayOfMonth){
		String sql = "SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) AS NAMES , "+
				" SUM(a.`sales_volume`) AS qty "+
				" FROM amazoninfo_sale_report a WHERE a.`product_name` IS NOT NULL "+
				" AND a.`date` >= :p1 "+
				" AND DATE_FORMAT(a.`date`,'%Y%m%d') < DATE_FORMAT(DATE_ADD(CURDATE(),INTERVAL -1 DAY),'%Y%m%d') "+
				" AND a.`sales`>a.`sales_volume`*IFNULL(a.`pack_num`,1) AND  "+
				" CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`)=:p2  "+
				" AND a.`order_type` = '1' GROUP BY NAMES,a.`date`";
		List<Object[]> list = this.saleReportDao.findBySql(sql,new Parameter(firstDayOfMonth, productName));
		float avgDaySales = 0f;
		//总天数
		int day = DateUtils.addDays(new Date(), -2).getDate();
		if(list != null && list.size()>0){
			List<Integer> qtyList = Lists.newArrayList();
			for (Object[] obj : list) {
				qtyList.add(Integer.parseInt(obj[1].toString()));
			}
			avgDaySales = MathUtils.getAverage(qtyList.toArray(new Integer[0]));
			avgDaySales = avgDaySales < 0 ? 0 : avgDaySales;
			int n = 0;	//取样中的噪点数
			if (avgDaySales > 10) {	//日均销大于10去噪点
				for (Iterator<Integer> iterator = qtyList.iterator(); iterator.hasNext();) {
					Integer num =  iterator.next();
					if (num > 5 * avgDaySales || num < avgDaySales / 5) {
						n++;
						iterator.remove();
					}
				}
			}
			//有噪点或者取样区间有销量为0的重新算日均销
			if (n > 0 || qtyList.size() < day - n) {
				for (int i = qtyList.size(); i < day - n; i++) {
					qtyList.add(0);	//没有销量的天数补0
				}
				avgDaySales = MathUtils.getAverage(qtyList.toArray(new Integer[0]));
				avgDaySales = avgDaySales < 0 ? 0 : avgDaySales;
			}
		}
		return Math.round(avgDaySales * DateUtils.getLastDayOfMonth(firstDayOfMonth).getDate());
	}
	
	
	//产品 [国家/数据]
	public Map<String, Map<String, Map<String,Integer>>> findForecastSalesData(){
		String sql = "SELECT aa.* FROM (SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) AS NAMES,a.`country`,DATE_FORMAT(a.`date`,'%Y%m') AS MONTH,SUM(a.`real_order`) AS v FROM amazoninfo_sale_report a WHERE a.`order_type` = '1'  AND (a.`flash_sales_order` IS NULL OR a.`flash_sales_order`=0)  and  a.`date`>=DATE_ADD(CURDATE(),INTERVAL -12 MONTH) AND a.`sales`>a.`sales_volume`*IFNULL(a.`pack_num`,1) AND a.`product_name` IS NOT NULL GROUP BY NAMES,MONTH,a.`country` HAVING v>0 ORDER BY NAMES,MONTH)aa,(SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) AS bname,a.`added_month`,a.`country` FROM psi_product_eliminate a WHERE a.`is_new` = '0' AND a.`is_sale` != '4'  AND a.`del_flag` = '0')bb WHERE aa.names = bb.bname AND aa.country = bb.country AND aa.month < DATE_FORMAT(CURDATE(),'%Y%m') AND aa.month > DATE_FORMAT(DATE_ADD(bb.added_month, INTERVAL 3 MONTH),'%Y%m') ORDER BY NAMES,country,MONTH desc";
		List<Object[]> list = this.saleReportDao.findBySql(sql,null);
		sql = "SELECT CONCAT(a.`product_name_color`,',',a.`country`,',',DATE_FORMAT(a.`data_date`,'%Y%m')) FROM amazoninfo_out_of_product a where a.`country` IN ('com','jp','ca') GROUP BY a.`product_name_color`,a.`country`,DATE_FORMAT(a.`data_date`,'%Y%m') HAVING COUNT(1) >7";
		List<String> list1 = this.saleReportDao.findBySql(sql,null);
		
		//List<Object> hasPowerProduct = hasPowerProducts();
		Map<String, String> fanOuMap = findProductFanOuFlag();
		
		Map<String, Map<String,  Map<String,Integer>>> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			String name = objects[0].toString();
			//boolean hasPower = hasPowerProduct.contains(name);
			String country = objects[1].toString();
			if("0".equals(fanOuMap.get(name)) && "fr,es,it,uk".contains(country)){
				country = "de";
			}
			if("1".equals(fanOuMap.get(name)) && "fr,es,it".contains(country)){
				country = "de";
			}
			String month = objects[2].toString();
			int num = Integer.parseInt(objects[3].toString());
			Map<String, Map<String,Integer>> temp = rs.get(name);
			String key = name+","+country+","+month;
			if(temp==null){
				temp = Maps.newHashMap();
				rs.put(name, temp);
			}
			 Map<String,Integer> data = temp.get(country);
			 if(data == null){
				 data = Maps.newLinkedHashMap();
				 temp.put(country, data);
			 }
			//排除断货月数据,不考虑欧洲断货
			 if(list1.contains(key)){
				 data.put(month,-1);
			 }else{
				 Integer sum = data.get(month);
				 if(sum==null){	 
					 data.put(month,num);
				 }else{
					 data.put(month,num+sum);
				 }
			 }
		}
		return rs;
	}

	//分产品查询最近两个月的销量[type[colorName {销量}]]
	public Map<String, Map<String, List<Integer>>> findByType() {
		Map<String, Map<String, List<Integer>>> rs = Maps.newHashMap();
		String sql = "SELECT p.`TYPE`,CONCAT(t.`product_name`,CASE WHEN t.`color` IS NULL OR t.`color`='' THEN '' ELSE CONCAT('_',t.`color`) END) AS pname,DATE_FORMAT(t.`date`,'%Y%m') AS dates,SUM(t.`real_sales_volume`*IFNULL(t.`pack_num`,1)) AS v FROM `amazoninfo_sale_report` t, psi_product p"+ 
				" WHERE (DATE_FORMAT(t.`date`,'%Y%m')=:p1 OR DATE_FORMAT(t.`date`,'%Y%m')=:p2)  AND t.`order_type`='1' AND t.`product_name`=CONCAT(p.`brand`,' ',p.`model`) "+
				" AND p.`TYPE` != 'Other' AND p.`TYPE` != 'old' AND t.`order_type` = '1' GROUP BY pname,dates ORDER BY p.`TYPE`,pname";
		Date date = new Date();
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		String month1 = monthFormat.format(DateUtils.addMonths(date, -1));
		String month2 = monthFormat.format(DateUtils.addMonths(date, -2));
		List<Object[]> list = this.saleReportDao.findBySql(sql,new Parameter(month1, month2));
		for (Object[] obj : list) {
			String type = obj[0].toString();
			String colorName = obj[1].toString();
			Integer volume = obj[3]==null?0:Integer.parseInt(obj[3].toString());
			Map<String, List<Integer>> typeMap = rs.get(type);
			if (typeMap == null) {
				typeMap = Maps.newHashMap();
				rs.put(type, typeMap);
			}
			List<Integer> volumeList = typeMap.get(colorName);
			if (volumeList == null) {
				volumeList = Lists.newArrayList();
				typeMap.put(colorName, volumeList);
			}
			volumeList.add(volume);
		}
		return rs;
	}
	
	public List<Object> hasPowerProducts(){
		String sql = "SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName "+
					" FROM psi_product a JOIN mysql.help_topic b "+
					" ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.`has_power` = '1'";
		return this.saleReportDao.findBySql(sql,null);
	}
	
	public List<Object> findKeyBoardAndHasPower(){
		String sql = "SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName "+
					" FROM psi_product a JOIN mysql.help_topic b "+
					" ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE (a.`has_power` = '1' or a.`TYPE` = 'Keyboard')";
		List<Object> list = this.saleReportDao.findBySql(sql,null);
		sql = "SELECT DISTINCT a.`sku` FROM psi_sku a WHERE a.`del_flag`= '0'  AND a.`use_barcode` = '1' AND a.`country` = 'de' AND CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`) IN :p1";
		return this.saleReportDao.findBySql(sql,new Parameter(list));
	}
	
	//带电源+KeyBoard产品
	public List<String> findKeyBoardAndHasPowerAllProduct(){
		String sql = "SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName "+
					" FROM psi_product a JOIN mysql.help_topic b "+
					" ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE (a.`has_power` = '1' or a.`TYPE` = 'Keyboard') AND a.`del_flag` = '0' ";
		return  this.saleReportDao.findBySql(sql,null);
	}
	
	//Inateck FD1003 Inateck FE3001 Inateck FD2102_dark gray Inateck FD1102_dark gray Inateck FD1101_dark gray
	public Map<String,Set<String>> findProductByType(){//0:keyboard 1:四国泛欧
		Map<String,Set<String>> map=Maps.newHashMap();
		String sql = "SELECT (case when (a.`TYPE` = 'Keyboard') then 0 else 1 end ) type,CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName "+
				" FROM psi_product a JOIN mysql.help_topic b "+
				" ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE (a.`has_power` = '1' or a.`TYPE` = 'Keyboard') AND a.`del_flag` = '0' ";
		List<Object[]> list=saleReportDao.findBySql(sql);
		for (Object[] obj: list) {
			Set<String> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Sets.newHashSet();
				map.put(obj[0].toString(),temp);
			}
			temp.add(obj[1].toString());
		}
		Set<String> temp=map.get("0");
		if(temp==null){
			map.put("0",temp);
		}
		return map;
	}
	
	/**
	 * @return Map<colorName, flag> flag 0：完全泛欧  1：uk以外4国泛欧  2：不能泛欧
	 */
	public Map<String, String> findProductFanOuFlag(){
		List<String> samePartNoList = Lists.newArrayList();
		Map<String, String> rs = Maps.newHashMap();
		String sql = "SELECT a.`TYPE`,a.`has_power`,CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName  "+
				 " FROM psi_product a JOIN mysql.help_topic b "+
				 " ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.`del_flag` = '0' ";
		List<Object[]> list=saleReportDao.findBySql(sql);
		for (Object[] obj: list) {
			String flag = "0";
			String type = obj[0].toString();
			String hasPower = obj[1].toString();
			String productName = obj[2].toString();
			//Keyboard类型和同partNo的不能泛欧
			if ("Keyboard".equals(type) || samePartNoList.contains(productName)) {
				flag = "2";
			} else if ("1".equals(hasPower)) {
				flag = "1";	//其它带电源类型的只能再UK以外的4国泛欧
			}
			rs.put(productName, flag);
		}
		return rs;
	}
	
	public Map<String,List<Object>> findCanPanEu(){
		String sql = "SELECT a.`asin`,a.`sku`,CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END ,b.`color`) FROM amazoninfo_product2 a ,psi_sku b WHERE  a.`sku` = BINARY(b.`sku`) AND a.`country` = b.`country` AND b.`del_flag` = '0' AND  a.`active` ='1' AND a.`is_fba` = '1' AND a.`country` = 'de' AND a.`sku` LIKE '%DE%' AND b.`product_name` != 'inateck old' AND b.`product_name` !='inateck other' ";	
		List<Object[]> list = this.saleReportDao.findBySql(sql,null);
		Map<String,List<Object>> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			List<Object> temp = Lists.newArrayList(objects);
			temp.add("0");
			temp.add("N");
			temp.add("N");
			temp.add("N");
			temp.add("N");
			temp.add("N");
			rs.put(objects[1].toString(),temp);
		}
		return rs;
	}
	
	public List<Object[]> findNewSkuTd(){
		String sql = "SELECT a.country,a.sku,a.`asin`  FROM amazoninfo_product2 a WHERE a.`open_date`= DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND a.`is_fba` = '1' AND a.`asin` IS NOT NULL AND a.`sku` IS NOT NULL AND a.`country` IS NOT NULL ORDER BY a.`country`";
		return this.saleReportDao.findBySql(sql,null);
	} 
	
	
	public List<Object[]> findFbaSalesWithLess15Days(){
		String sql = "SELECT  aaaa.*,bbbb.qq,bbbb.tran,ROUND(bbbb.qq / aaaa.day_sales) AS DAY  "+
					"	FROM (SELECT "+
				"		    aa.* FROM (SELECT f1.proName AS product_name,f1.country,f2.day_sales,f2.day31_sales FROM  "+
				"		(SELECT aaa.proName,ccc.country,aaa.ispower FROM (SELECT aa.proName,(SUBSTRING_INDEX(SUBSTRING_INDEX(aa.country,',',bb.help_topic_id+1),',',-1)) AS country,aa.ispower FROM (SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName  "+
				"					  ,(CASE WHEN (a.has_power = '1' OR a.TYPE = 'Keyboard') THEN 'de,uk,com,com2,com3,mx,ca,jp' ELSE 'de,com,com2,com3,mx,ca,jp' END) AS country,(CASE WHEN (a.has_power = '1' OR a.TYPE = 'Keyboard') THEN '1' ELSE '0' END) AS ispower FROM psi_product a JOIN mysql.help_topic b  "+
				"					   ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.del_flag = '0') aa JOIN mysql.help_topic bb  ON bb.help_topic_id < (LENGTH(aa.country) - LENGTH(REPLACE(aa.country,',',''))+1))aaa ,psi_product_eliminate ccc WHERE aaa.proName = CONCAT(ccc.product_name,CASE WHEN ccc.color!='' THEN '_' ELSE '' END ,ccc.color) AND aaa.country = ccc.country AND ccc.is_sale != '4' AND ccc.added_month<DATE_ADD(CURDATE(),INTERVAL -8 DAY)) f1, "+
				"			(SELECT a.`product_name`,a.`country`,ROUND(a.`day31_sales` / 31, 2) AS day_sales, "+
				"			      a.`day31_sales` FROM psi_product_variance a WHERE ROUND(a.`day31_sales` / 31, 2) > 0) f2 WHERE f1.proName = f2.product_name AND (CASE WHEN f1.ispower = '0' AND f1.country='de'   THEN 'eu'  WHEN f1.ispower = '1' AND f1.country='de'   THEN 'eunouk' ELSE f1.country END) = f2.country) aa, "+
				"			    (SELECT CONCAT(a.`product_name`,CASE WHEN a.`color` != ''  THEN '_'  ELSE ''  END, a.`color` "+
				"			      ) AS NAME,a.`country` FROM psi_product_eliminate a  WHERE a.`added_month` IS NOT NULL   AND a.`is_sale` != '4') bb WHERE aa.product_name = bb.name   AND aa.country = bb.country) aaaa, "+
				"			  (SELECT "+
				"			    aa.name, aa.country, SUM(bb.q) AS qq, SUM(bb.transit_quantity) AS tran "+
				"			  FROM (SELECT    a.`sku`,   a.`country`, CONCAT(  a.`product_name`,    CASE   WHEN a.`color` != ''     THEN '_'   ELSE ''    END,    a.`color` ) AS NAME  "+
				"			   FROM  psi_sku a  WHERE a.`del_flag` = '0'  AND a.`product_name` != 'inateck old'  AND a.`product_name` != 'inateck other') aa,(SELECT   a.`sku`, a.`country`,  CASE WHEN a.orrect_quantity IS NULL THEN a.`fulfillable_quantity`   ELSE a.`orrect_quantity` + a.fulfillable_quantity   END AS q, a.`transit_quantity`   FROM   psi_inventory_fba a WHERE a.`data_date` = CURDATE()) bb  WHERE aa.sku = BINARY(bb.sku) AND aa.country = bb.country  GROUP BY aa.name,aa.country) AS bbbb "+
				"			WHERE aaaa.product_name = bbbb.name  AND aaaa.country = bbbb.country AND ROUND(bbbb.qq / aaaa.day_sales) <= 30 AND (aaaa.country IN ('de', 'com', 'com2', 'com3', 'ca', 'jp','mx') OR aaaa.day31_sales >= 50) "+
				"			ORDER BY FIELD(aaaa.country,'com', 'com2','com3','de','uk', 'jp','ca','mx' ), DAY ";
		return this.saleReportDao.findBySql(sql,null);
	} 
	
	public Map<String,Map<String,Object[]>> findSalePirceError(){
		String sql = "SELECT a.`asin`,a.`country`,GROUP_CONCAT(CONCAT(a.`sku`,':',CASE WHEN a.is_fba = '1' THEN a.`sale_price` ELSE a.sale_price +3.99 END) ORDER BY CASE WHEN a.is_fba = '1' THEN a.`sale_price` ELSE a.sale_price +3.99 END ),GROUP_CONCAT(DISTINCT (( CASE WHEN a.is_fba = '1' THEN a.`sale_price` ELSE a.sale_price +3.99 END))) AS priceStr FROM amazoninfo_product2 a WHERE a.`active` = '1' AND a.`sku` NOT LIKE '%-old'  AND a.`sku` NOT LIKE '%\\_old' AND a.`country` IN ('de','fr','es','it','uk') AND a.`sale_price`>0  GROUP BY a.`asin`,a.`country`  HAVING COUNT(1)>1  AND  LOCATE(',',priceStr)>0 ORDER BY country";
		Map<String,String> nameMaps = amazonProductService.findProductNameMap();
		List<Object[]> list =  this.saleReportDao.findBySql(sql,null);
		Map<String,Map<String,Object[]>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[1].toString();
			String remark = objs[2].toString();
			String asin = objs[0].toString();
			String name = nameMaps.get(asin);
			if(name==null){
				continue;
			}
			if(!(remark.toLowerCase().contains("-"+country)||("de".equals(country)&&!remark.toLowerCase().contains("-uk")&&!remark.toLowerCase().contains("-fr")&&!remark.toLowerCase().contains("-it")&&!remark.toLowerCase().contains("-es")))){
				continue;
			}
			String[] first =  remark.split(",")[0].split(":");
			String sku = first[0];
			String price = first[1];
			if(!(sku.toLowerCase().contains("-"+country) || ("de".equals(country)&&!sku.toLowerCase().contains("-uk")&&!sku.toLowerCase().contains("-fr")&&!sku.toLowerCase().contains("-it")&&!sku.toLowerCase().contains("-es") ) )){
				Map<String,Object[]> temp = rs.get(country);
				if(temp==null){
					temp = Maps.newHashMap();
					rs.put(country, temp);
				}
				temp.put(name, objs);
			}else{
				for (int i = 1; i < remark.split(",").length; i++) {
					String sku2 = remark.split(",")[i].split(":")[0];
					if(price.equals(remark.split(",")[i].split(":")[1])&&!(sku2.toLowerCase().contains("-"+country) || ("de".equals(country)&&!sku2.toLowerCase().contains("-uk")&&!sku2.toLowerCase().contains("-fr")&&!sku2.toLowerCase().contains("-it")&&!sku2.toLowerCase().contains("-es") ) )){
						Map<String,Object[]> temp = rs.get(country);
						if(temp==null){
							temp = Maps.newHashMap();
							rs.put(country, temp);
						}
						temp.put(name, objs);
						break;
					}
				}
			}
		}
		return rs;
	} 
	
	
	@Transactional(readOnly = false)
	public void savePanEu(Collection<List<Object>>  data){
		String sql = "DELETE FROM amazoninfo_pan_eu  WHERE is_pan_eu = '0'";
		saleReportDao.updateBySql(sql, null);
		String addSql = "INSERT INTO `amazoninfo_pan_eu` (asin,sku,product_name,is_pan_eu,uk,de,fr,it,es,fnsku) values (:p1,:p2,:p3,:p4,:p5,:p6,:p7,:p8,:p9,:p10) ";
		String updateSql = "update amazoninfo_pan_eu set is_pan_eu=:p1,uk=:p2,de=:p3,fr=:p4,it=:p5,es=:p6 where sku =:p7";
		sql = "SELECT a.sku FROM amazoninfo_pan_eu a";
		List<Object> skus = saleReportDao.findBySql(sql,null);
		for (List<Object> list : data) {
			String sku = list.get(1).toString();
			if(skus.contains(sku)){
				saleReportDao.updateBySql(updateSql, new Parameter(list.get(3),list.get(4),list.get(5),list.get(6),list.get(7),list.get(8),sku));
			}else{
				saleReportDao.updateBySql(addSql, new Parameter(list.toArray(new Object[10])));
			}
		}
	}
	
	public List<Object[]> panEuList(){
		String sql = "select * from amazoninfo_pan_eu order by is_pan_eu desc";
		return this.saleReportDao.findBySql(sql,null);
	} 
	
	
	
	/**
	 *查询泛欧产品
	 */
	public Set<String>  getPanEuProduct(){
		String sql = "SELECT a.`product_name` FROM amazoninfo_pan_eu AS a  WHERE a.`is_pan_eu`='1'";
		List<String> list= this.saleReportDao.findBySql(sql,null);
		Set<String>  set = Sets.newHashSet();
		if(list!=null&&list.size()>0){
			set.addAll(list);
		}
		return set;
	}
	
	/**
	 *查询泛欧产品
	 */
	public Set<String>  getPanEuProductAsin(){
		String sql = "SELECT a.`asin` FROM amazoninfo_pan_eu AS a  WHERE a.`is_pan_eu`='1'";
		List<String> list= this.saleReportDao.findBySql(sql,null);
		Set<String>  set = Sets.newHashSet();
		if(list!=null&&list.size()>0){
			set.addAll(list);
		}
		return set;
	}
	
	
	//国家[日期 /数据]
		public Map<String,Map<String, Map<String,Integer>>> getMaxOrder(SaleReport saleReport,List<String> productNames){
			//String currencyType = saleReport.getCurrencyType(); //统计的货币类型（EUR/USD）
			Date start = saleReport.getStart();
			Date end = saleReport.getEnd();
			String typeSql = "'%Y%m%d'";
			if("2".equals(saleReport.getSearchType())){
				if(start==null){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addWeeks(today, -2);
					end = today;
					saleReport.setStart(start);
					saleReport.setEnd(end);
				}else{
					Date end1 = DateUtils.addWeeks(start,2);
					if(end.before(end1)){
						end = end1;
						saleReport.setEnd(end1);
					}
				}
				start = DateUtils.getMonday(start);
				end = DateUtils.getSunday(end);
				typeSql="'%x%v'";
			}else if("3".equals(saleReport.getSearchType())){
				if(start==null){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addMonths(today, -6);
					end = today;
					saleReport.setStart(start);
					saleReport.setEnd(end);
				}else{
					Date end1 = DateUtils.addMonths(start,6);
					if(end.before(end1)){
						end = end1;
						saleReport.setEnd(end1);
					}
				}
				start = DateUtils.getFirstDayOfMonth(start);
				end = DateUtils.getLastDayOfMonth(end);
				typeSql="'%Y%m'";
			}else{
				if(start==null){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addDays(today, -10);
					end = today;
					saleReport.setStart(start);
					saleReport.setEnd(end);
				}else{
					Date end1 = DateUtils.addDays(start,10);
					if(end.before(end1)){
						end = end1;
						saleReport.setEnd(end1);
					}
				}
			}
			List<Object[]> list =null;
			String sql ="SELECT country,DATE_FORMAT(a.`date`,"+typeSql+") dates,product_name,SUM(quantity) from amazoninfo_max_order  a where date>=:p1 AND date<=:p2 and product_name is not null ";
			if(productNames!=null&&productNames.size()>0){
				sql+=" and product_name in :p3 ";
			}
			sql+=" group by country,dates,product_name ";
			if(productNames!=null&&productNames.size()>0){
				list=saleReportDao.findBySql(sql,new Parameter(start,end,productNames));
			}else{
				 list=saleReportDao.findBySql(sql,new Parameter(start,end));
			}
			
			Map<String,Map<String, Map<String,Integer>>> rs = Maps.newHashMap();
			for (Object[] objs : list) {
				String country=objs[0].toString();
				String date = objs[1].toString(); 
				String name=objs[2].toString();
				Integer quantity=Integer.parseInt(objs[3].toString());
				if("2".equals(saleReport.getSearchType())){
					Integer i = Integer.parseInt(date.substring(4));
					if(i==53){
						Integer year = Integer.parseInt(date.substring(0,4));
						date =  (year+1)+"01";
					}else if (date.contains("2016")){
						i = i+1;
						date = "2016"+(i<10?("0"+i):i);
					}
				}
				
				Map<String, Map<String,Integer>> temp=rs.get(name);
				if(temp==null){
					temp=Maps.newHashMap();
					rs.put(name,temp);
				}
				
				
				Map<String,Integer> dateTemp=temp.get(country);
				if(dateTemp==null){
					dateTemp=Maps.newHashMap();
					temp.put(country,dateTemp);
				}
				dateTemp.put(date,quantity);
				dateTemp.put("total",quantity+(dateTemp.get("total")==null?0:dateTemp.get("total")));
			
				Map<String,Integer> totalDateTemp=temp.get("total");
				if(totalDateTemp==null){
					totalDateTemp=Maps.newHashMap();
					temp.put("total",totalDateTemp);
				}
				totalDateTemp.put(date,quantity+(totalDateTemp.get(date)==null?0:totalDateTemp.get(date)));
				totalDateTemp.put("total",quantity+(totalDateTemp.get("total")==null?0:totalDateTemp.get("total")));
				
				//欧洲汇总
				if ("de,fr,it,es,uk".contains(country)) {
					
					Map<String,Integer> euDateTemp=temp.get("eu");
					if(euDateTemp==null){
						euDateTemp=Maps.newHashMap();
						temp.put("eu",euDateTemp);
					}
					euDateTemp.put(date,quantity+(euDateTemp.get(date)==null?0:euDateTemp.get(date)));
					euDateTemp.put("total",quantity+(euDateTemp.get("total")==null?0:euDateTemp.get("total")));
				}
			}
			return rs;
		}
		
		
		public List<Object[]> findMaxOrder(String dateStr,String byTime,String country,String productName){
			Date start = null;
			Date end = null;
			if("1".equals(byTime)){
				try {
					start = format.parse(dateStr);
					end = DateUtils.addDays(start, 1);
				} catch (ParseException e) {}
			}else if("2".equals(byTime)){
				int year = Integer.parseInt(dateStr.substring(0,4));
				int week = Integer.parseInt(dateStr.substring(4));
				start = DateUtils.getFirstDayOfWeek(year, week);
				end = DateUtils.getLastDayOfWeek(year, week);
				end = DateUtils.addDays(end, 1);
			}else if("3".equals(byTime)){
				int year = Integer.parseInt(dateStr.substring(0,4));
				int month = Integer.parseInt(dateStr.substring(4))-1;
				try {
					start = DateUtils.getFirstDayOfMonth(year, month);
					end = DateUtils.getLastDayOfMonth(year, month);
					end = DateUtils.addDays(end, 1);
				} catch (ParseException e) {}
			}
			
				String temp = "";
				if(!"total".equals(country) && !"eu".equals(country)){
					temp = "  and  a.`sales_channel` like '%"+country+"%'";
				} else if ("eu".equals(country)) {
					temp = "  and  a.`sales_channel` in ('Amazon.de','Amazon.co.uk','Amazon.es','Amazon.fr','Amazon.it') ";
				}
				String temp1="";
				if(StringUtils.isNotEmpty(productName)){
					temp1 = "  and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END)='"+productName+"'";
				}
		        String sql = "SELECT a.`amazon_order_id`,a.`sales_channel`,b.`sellersku`,CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) AS productName,SUM(b.`quantity_ordered`),a.`order_status`,TRUNCATE((CASE WHEN a.sales_channel='Amazon.ca' THEN sum(b.`item_price`-IFNULL(b.`promotion_discount`,0))*:p1 WHEN a.sales_channel='Amazon.co.jp' THEN sum(b.`item_price`-IFNULL(b.`promotion_discount`,0))*:p2 WHEN a.sales_channel='Amazon.co.uk' THEN sum(b.`item_price`-IFNULL(b.`promotion_discount`,0))*:p3 WHEN a.sales_channel='Amazon.com' THEN sum(b.`item_price`-IFNULL(b.`promotion_discount`,0))*:p4 WHEN LOCATE('mx',a.sales_channel)>0 THEN sum(b.`item_price`-IFNULL(b.`promotion_discount`,0))*:p5 ELSE sum(b.`item_price`-IFNULL(b.`promotion_discount`,0)) END),2),a.`purchase_date` FROM amazoninfo_order a join amazoninfo_orderitem b on a.`id` = b.`order_id` ";
		        
		        sql+=" where  a.`order_status` IN('UnShipped','Shipped','Pending')  and a.`order_channel` IS NULL   AND a.`purchase_date`>=:p6 AND a.`purchase_date` <:p7 "+temp+temp1+" ";
		       
		        sql+=" group by a.`amazon_order_id`,a.`sales_channel`,b.`sellersku`,productName,a.`order_status`,a.`purchase_date` having SUM(b.`quantity_ordered`)>=10  order by a.`purchase_date` ";
		        return saleReportDao.findBySql(sql, new Parameter(AmazonProduct2Service.getRateConfig().get("CAD/EUR"),AmazonProduct2Service.getRateConfig().get("JPY/EUR"),AmazonProduct2Service.getRateConfig().get("GBP/EUR"),AmazonProduct2Service.getRateConfig().get("USD/EUR"),AmazonProduct2Service.getRateConfig().get("MXN/EUR"),start,end));
		}
		
		/**
		 * 获取海外仓库存
		 * @return 产品名_仓库ID /数量
		 */
		public Map<String, Integer> findInventorys(){
			String sql = "SELECT CONCAT(a.`product_name`,CASE WHEN a.`color_code`!='' THEN '_' ELSE '' END,a.`color_code`,'_',a.`warehouse_id`) AS NAME,SUM(a.`new_quantity`) FROM psi_inventory a WHERE a.`warehouse_id`!=21 AND a.`new_quantity`>0 GROUP BY NAME";
			List<Object[]> list = saleReportDao.findBySql(sql);
			Map<String, Integer> rs = Maps.newHashMap();
			for (Object[] objects : list) {
				rs.put(objects[0].toString(), Integer.parseInt(objects[1].toString()));
			}
			return rs;
		}
		
		
	    //[国家[产品线[月 /销售额]]]
		public Map<String,Map<String,Map<String, SaleReport>>> getCountryLineSales(String date1, String date2, String countryStr){
			String temp = "";
			if (StringUtils.isNotEmpty(countryStr) && !"total".equals(countryStr) 
					&& !"en".equals(countryStr)&& !"notEn".equals(countryStr)&& !"notUs".equals(countryStr)) {
				temp = " AND t.`country`='"+countryStr+"' ";
			} else if ("notEn".equals(countryStr)) {	//非英语国家小语种统计
				temp = " AND t.`country` IN('de','fr','it','es','jp') ";
			} else if ("en".equals(countryStr)) {	//非英语国家小语种统计
				temp = " AND t.`country` IN('com','uk','ca') ";
			} else if ("notUs".equals(countryStr)) {
				temp = " AND t.`country` IN('de','fr','it','es','jp','uk','mx','ca')";
			}
	        String sql = "SELECT t.`line`,SUM(t.`sales`),t.`month`,t.`country` FROM `amazoninfo_report_month_type` t"+
	        		" WHERE t.`month`>=:p1 AND t.`month`<=:p2 "+temp+" GROUP BY t.`line`,t.`month`,t.`country`";
	        List<Object[]> list = saleReportDao.findBySql(sql, new Parameter(date1,date2));
			
	        Map<String,Map<String,Map<String, SaleReport>>> rs = Maps.newHashMap();
			for (Object[] objs : list) {
				String line = objs[0].toString();
				//line = line + " 产品线";
				Float sales = ((BigDecimal)objs[1]).floatValue();
				String date = objs[2].toString(); 
				String country = objs[3].toString(); 

				Map<String,Map<String, SaleReport>> countryMap = rs.get(country);
				if (countryMap == null) {
					countryMap = Maps.newLinkedHashMap();
					rs.put(country, countryMap);
				}
				Map<String, SaleReport> datas = countryMap.get(line);
				if (datas == null) {
					datas = Maps.newLinkedHashMap();
					countryMap.put(line, datas);
				}
				datas.put(date, new SaleReport(sales, 0f, 0f, 0, 0, 0));

				if ("com,uk,ca".contains(country)) {	//英语国家总计
					Map<String,Map<String, SaleReport>> enCountryMap = rs.get("en");
					if (enCountryMap == null) {
						enCountryMap = Maps.newLinkedHashMap();
						rs.put("en", enCountryMap);
					}
					Map<String, SaleReport> datas1 = enCountryMap.get(line);
					if(datas1==null){
						datas1 = Maps.newLinkedHashMap();
						enCountryMap.put(line, datas1);
					}
					SaleReport saleEntry = datas1.get(date);
					if (saleEntry == null) {
						saleEntry = new SaleReport(0f,0f,0f,0,0,0);
						datas1.put(date, saleEntry);
					}
					saleEntry.setSales(sales+saleEntry.getSales());	
				}

				Map<String,Map<String, SaleReport>> totalCountryMap = rs.get("total");
				if (totalCountryMap == null) {
					totalCountryMap = Maps.newLinkedHashMap();
					rs.put("total", totalCountryMap);
				}
				Map<String, SaleReport> datas1 = totalCountryMap.get(line);
				if(datas1==null){
					datas1 = Maps.newLinkedHashMap();
					totalCountryMap.put(line, datas1);
				}
				SaleReport saleEntry = datas1.get(date);
				if (saleEntry == null) {
					saleEntry = new SaleReport(0f,0f,0f,0,0,0);
					datas1.put(date, saleEntry);
				}
				saleEntry.setSales(sales+saleEntry.getSales());	
			}
			return rs;
		}
		
		/**
		 * 组合运营业绩报告数据
		 * @param year
		 * @return//日期[类型	[国家 /销售额]]
		 */
		public Map<String,Map<String, Map<String, Float>>> getSalesResult(String year){
			String currencyType = "EUR"; //统计的货币类型（EUR/USD）
			Map<String, Float> rateRs = null;	//置空,采用实时汇率
			String sql ="SELECT t.`country`,p.`TYPE`,DATE_FORMAT(t.`date`,'%Y%m') as dates,TRUNCATE(Sum(CASE WHEN t.`country`='de'||t.`country`='fr'||t.`country`='it'||t.`country`='es' THEN t.`sales`*"
					+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN t.`country`='uk' THEN t.`sales`*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN t.`country`='ca' THEN t.`sales`*"
					+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN t.`country`='jp' THEN t.`sales`*"+
					MathUtils.getRate("JPY", currencyType, rateRs)+" ELSE t.`sales`*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) FROM `amazoninfo_sale_report` t, `psi_product` p "+
					" WHERE t.order_type='1' AND p.del_flag='0' AND DATE_FORMAT(t.`date`,'%Y')=:p1 AND t.`product_name`=CONCAT(p.`brand`,' ',p.`model`) "+
					" GROUP BY t.`country`,dates,p.`TYPE`";
			
			List<Object[]> list = saleReportDao.findBySql(sql, new Parameter(year));
			
			Map<String,Map<String, Map<String, Float>>> rs = Maps.newLinkedHashMap();
			for (Object[] objs : list) {
				String country = objs[0].toString();
				String type = objs[1].toString();
				String date = objs[2].toString();
				Float sales = Float.parseFloat(objs[3].toString());
				
				Map<String, Map<String, Float>> typeTemp=rs.get(date);
				if(typeTemp==null){
					typeTemp = Maps.newHashMap();
					rs.put(date, typeTemp);
				}
				Map<String, Float> countryTemp = typeTemp.get(type);
				if (countryTemp == null) {
					countryTemp = Maps.newHashMap();
					typeTemp.put(type, countryTemp);
				}
				countryTemp.put(country, sales);
				
				Map<String, Float> countryTotalTemp = typeTemp.get("total");	//分国家总计
				if (countryTotalTemp == null) {
					countryTotalTemp = Maps.newHashMap();
					typeTemp.put("total", countryTotalTemp);
				}
				String totalCountry = country;
				if ("ca,com,uk".contains(country)) {
					totalCountry = "en";
				}
				Float enTotalSales = countryTotalTemp.get(totalCountry);
				if (enTotalSales == null) {
					countryTotalTemp.put(totalCountry, sales);
				} else {
					countryTotalTemp.put(totalCountry, enTotalSales + sales);
				}

				Float totalSales = countryTemp.get("total");	//分类型总计
				if(totalSales==null){
					countryTemp.put("total", sales);
				} else {
					countryTemp.put("total", totalSales + sales);
				}
				//英语国家汇总
				if ("ca,com,uk".contains(country)) {
					Float enSales = countryTemp.get("en")==null?0:countryTemp.get("en");
					countryTemp.put("en", enSales + sales);
				}
				//分季节统计
				String q = "q1";	//一季度
				if (date.endsWith("04") || date.endsWith("05") || date.endsWith("06")) {	//二季度
					q = "q2";
				} else if (date.endsWith("07") || date.endsWith("08") || date.endsWith("09")) {	//三季度
					q = "q3";
				} else if (date.endsWith("10") || date.endsWith("11") || date.endsWith("12")) {	//四季度
					q = "q4";
				}
				Map<String, Map<String, Float>> typeSeasonTemp = rs.get(q);
				if (typeSeasonTemp == null) {
					typeSeasonTemp = Maps.newHashMap();
					rs.put(q, typeSeasonTemp);
				}
				Map<String, Float> countrySeasonTemp = typeSeasonTemp.get(type);
				if (countrySeasonTemp == null) {
					countrySeasonTemp = Maps.newHashMap();
					typeSeasonTemp.put(type, countrySeasonTemp);
				}
				Float seasonSales = countrySeasonTemp.get(country)==null?0:countrySeasonTemp.get(country);
				countrySeasonTemp.put(country, seasonSales + sales);
				Float seasonTotalSales = countrySeasonTemp.get("total")==null?0:countrySeasonTemp.get("total");
				countrySeasonTemp.put("total", seasonTotalSales + sales);
				//英语国家汇总
				if ("ca,com,uk".contains(country)) {
					Float enSales =countrySeasonTemp.get("en");
					if(enSales==null){
						countrySeasonTemp.put("en", sales);
					} else {
						countrySeasonTemp.put("en", enSales + sales);
					}
				}
				
				Map<String, Float> countrySeasonTotal = typeSeasonTemp.get("total");	//分国家季节总计
				if (countrySeasonTotal == null) {
					countrySeasonTotal = Maps.newHashMap();
					typeSeasonTemp.put("total", countrySeasonTotal);
				}
				Float seasonTotal = countrySeasonTotal.get(totalCountry);
				if (seasonTotal == null) {
					countrySeasonTotal.put(totalCountry, sales);
				} else {
					countrySeasonTotal.put(totalCountry, seasonTotal + sales);
				}
			}
			return rs;
		}
		
		//productName [line ...,type ...]
		public Map<String, Map<String, String>> getPorductsTypeAndLine(){
			String sql = "SELECT  a.name,a.type,d.name lineName FROM "+
						 "	(SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.type "+
						 "	 FROM psi_product a JOIN mysql.help_topic b  ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1)  "+
						 "	 WHERE a.del_flag='0') a  JOIN sys_dict t ON a.type=t.value AND t.`del_flag`='0' AND t.`description`='product_type' "+
						 "	 LEFT JOIN psi_product_type_group g ON t.id=g.`dict_id` "+
						 "	 LEFT JOIN psi_product_type_dict d ON g.id=d.id AND d.`del_flag`='0' ";
			
			List<Object[]> list = saleReportDao.findBySql(sql);
			Map<String, Map<String, String>> rs = Maps.newHashMap();
			for (Object[] objs : list) {
				Map<String, String> temp = Maps.newHashMap(); 
				rs.put(objs[0].toString(), temp);
				temp.put("line", objs[2]==null?"":objs[2].toString());
				temp.put("type", objs[1]==null?"":objs[1].toString());
			}
			return rs;
		}
		
		//日期 [国家/数据]
		public Map<String, Map<String,SaleReport>> getSalesByTypes(SaleReport saleReport){
			Date start = saleReport.getStart();
			Date end = saleReport.getEnd();
			String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		}
		List<Object[]> list = null;
		String sql ="SELECT a.`country`,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
				+AmazonProduct2Service.getRateConfig().get("EUR/USD")+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+AmazonProduct2Service.getRateConfig().get("GBP/USD")+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
				+AmazonProduct2Service.getRateConfig().get("CAD/USD")+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
				AmazonProduct2Service.getRateConfig().get("JPY/USD")+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+AmazonProduct2Service.getRateConfig().get("MXN/USD")+" ELSE SUM(a.`sales`) END ),2),TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
				+AmazonProduct2Service.getRateConfig().get("EUR/USD")+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+AmazonProduct2Service.getRateConfig().get("GBP/USD")+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
				+AmazonProduct2Service.getRateConfig().get("CAD/USD")+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
				AmazonProduct2Service.getRateConfig().get("JPY/USD")+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+AmazonProduct2Service.getRateConfig().get("MXN/USD")+" ELSE sum(a.`sure_sales`) END ),2),max(max_order) tempOrder,DATE_FORMAT(a.`date`,"+typeSql+") dates,max(max_order),sum(promotions_order),sum(flash_sales_order),sum(free_order),sum(ads_order) FROM amazoninfo_sale_report a  WHERE a.order_type='1' and a.`date`>=:p1 AND a.`date`<=:p2   GROUP BY a.`country`,dates ORDER BY a.`country`,dates DESC ";
		list = saleReportDao.findBySql(sql, new Parameter(start,end,saleReport.getSku()));
		Map<String, Map<String,SaleReport>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
			Integer salesVolume = ((BigDecimal)objs[1]).intValue();
			Integer sureSalesVolume = ((BigDecimal)objs[2]).intValue();
			Float sales = Float.valueOf(objs[3]==null?"0":objs[3].toString());
			Float sureSales = Float.valueOf(objs[4]==null?"":objs[4].toString());
			String date = objs[6].toString(); 
			Integer maxOrder=Integer.parseInt(objs[7]==null?"0":objs[7].toString());
			Integer promotionsOrder=Integer.parseInt(objs[8]==null?"0":objs[8].toString());
			Integer flashSalesOrder=Integer.parseInt(objs[9]==null?"0":objs[9].toString());
			Integer freeOrder=Integer.parseInt(objs[10]==null?"0":objs[10].toString());
			Integer adsOrder=Integer.parseInt(objs[11]==null?"0":objs[11].toString());
			String classType="";
			int num=0;
			if(maxOrder>0){
				num++;
				classType="btn-primary";
			}
			if(promotionsOrder>0){
				num++;
				classType="btn-warning";
			}
			if(flashSalesOrder>0){
				num++;
				classType="btn-danger";
			}
			if(freeOrder>0){
				num++;
				classType="btn-info";
			}
			if(adsOrder>0){
				num++;
				classType="btn-success";
			}
			if(num>=2){
				classType="btn-inverse";
			}
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Map<String,SaleReport> countrys = rs.get(date);
			if(countrys==null){
				countrys = Maps.newLinkedHashMap();
				rs.put(date,countrys);
			}
			countrys.put(country, new SaleReport(sales,sureSales,salesVolume,sureSalesVolume,maxOrder,promotionsOrder,flashSalesOrder,freeOrder,adsOrder,classType));
			
			SaleReport saleEntry = countrys.get("total");
			if(saleEntry==null){
				saleEntry = new SaleReport(0f,0f,0,0,0,0,0,0,0);
				countrys.put("total", saleEntry);
				}
				saleEntry.setSales(sales+saleEntry.getSales());
				saleEntry.setSalesVolume(salesVolume+saleEntry.getSalesVolume());
				saleEntry.setSureSales(sureSales+saleEntry.getSureSales());
				saleEntry.setSureSalesVolume(sureSalesVolume+saleEntry.getSureSalesVolume());
				if(maxOrder>saleEntry.getMaxOrder()){
					saleEntry.setMaxOrder(maxOrder);
				}
				saleEntry.setPromotionsOrder(promotionsOrder+saleEntry.getPromotionsOrder());
				saleEntry.setFlashSalesOrder(flashSalesOrder+saleEntry.getFlashSalesOrder());
				saleEntry.setFreeOrder(freeOrder+saleEntry.getFreeOrder());
				saleEntry.setAdsOrder(adsOrder+saleEntry.getAdsOrder());
			}
			return rs;
		}
		
		/**
		 * 查询单品评论
		 * @param productName
		 * @param aboutMe
		 * @return
		 */
		public List<Object[]> findProductReview(String country, String productName, SaleReport saleReport) {
			List<Object[]> list = Lists.newArrayList();
			String temp = "";
			String temp1 = "";
			if (StringUtils.isNotEmpty(country)) {
				if ("en".equals(country)) {	//英语国家
					temp = "  and a.`sales_channel` in ('Amazon.co.uk','Amazon.com','Amazon.ca') ";
					temp1 = " and bb.`country` in ('uk','com','ca') ";
				} else if ("eu".equals(country)) {	//欧洲国家
					temp = "  and a.`sales_channel` in ('Amazon.co.uk','Amazon.de','Amazon.es','Amazon.fr','Amazon.it') ";
					temp1 = " and bb.`country` in ('de','uk','es','fr','it') ";
				} else if ("jp,uk".contains(country)) {
					temp = "  and a.`sales_channel` ='Amazon.co."+country+"'";
					temp1 = "  and bb.`country` ='"+country+"'";
				} else {
					temp = "  and a.`sales_channel` ='Amazon."+country+"'";
					temp1 = "  and bb.`country` ='"+country+"'";
				}
			}
			String skuSql = "SELECT DISTINCT bb.`asin` FROM `psi_sku` bb WHERE CONCAT(bb.`product_name`,CASE  WHEN bb.`color`='' THEN '' ELSE CONCAT('_',bb.`color`) END)=:p1 "+temp1+" AND bb.`asin` IS NOT NULL";
			List<String> asinList = saleReportDao.findBySql(skuSql, new Parameter(productName));
			if (asinList==null || asinList.size() == 0) {
				return list;
			}
			String sql="SELECT bb.`review_date`,bb.`review_asin`,bb.`star`,bb.`subject` ,aa.* "+
					" FROM amazoninfo_review_comment bb LEFT JOIN "+
					" (SELECT a.`amazon_order_id`, c.`name`, a.`purchase_date`, a.`buyer_email`, a.`custom_id`, "+
					" b.`quantity_shipped`, ROUND(b.`item_price` / b.`quantity_shipped`, 2 ), b.`promotion_ids`, b.`promotion_discount`, "+
					" c.`phone`, c.`postal_code`, c.`country_code`, c.`city`, b.`asin` "+
					" FROM amazoninfo_order a, amazoninfo_orderitem b, amazoninfo_address c  "+
					" WHERE a.`id` = b.`order_id`  "+
					" AND a.`shipping_address` = c.`id`  "+
					" AND a.`order_status` = 'shipped'  "+
					temp +//" AND a.`sales_channel` = 'Amazon.com'  "+
					" AND b.`product_name` = :p1 AND a.`purchase_date`>:p2 ) aa  "+
					" ON aa.custom_id = bb.`customer_id`  "+
					" AND aa.asin = bb.asin WHERE bb.`review_date` >:p2 " +
					temp1 +//" AND bb.`country` = 'com' " +
					" AND bb.`asin` IN (:p3) ";
			list = saleReportDao.findBySql(sql, new Parameter(productName, saleReport.getStart(), asinList));
			return list;
		}

		/**
		 * 畅销榜前10
		 * @param num 当前天往前推的天数,如查询昨日数据则为1
		 * @return
		 */
		public Map<String, Map<String, String>> getBestseller(int num) {
			Map<String, Map<String, String>> rsMap = Maps.newLinkedHashMap();
			String sql = "SELECT aa.pname,SUM(aa.num) AS num,GROUP_CONCAT(aa.country,'&',aa.num ORDER BY aa.num DESC) FROM "+ 
					" (SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`) AS pname,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) AS num ,a.`country` "+
					" FROM amazoninfo_sale_report a WHERE a.`date` = DATE_ADD(CURDATE(),INTERVAL -"+num+" DAY) AND a.`product_name` IS NOT NULL   AND a.`order_type`='1' "+
					" GROUP BY pname,a.`country`) aa GROUP BY aa.pname ORDER BY num DESC LIMIT 10";
			List<Object[]> list = saleReportDao.findBySql(sql);
			for (Object[] obj : list) {
				String productName = obj[0].toString();
				String totalNum = obj[1].toString();
				String detail = obj[2].toString();
				Map<String, String> productMap = Maps.newHashMap();
				rsMap.put(productName, productMap);
				productMap.put("total", totalNum);
				productMap.put("detail", detail);
			}
			return rsMap;
		}
		
		/**
		 * 统计所有产品的销量
		 * @return Map<String,Integer> [productName qty]
		 */
		public Map<String, Integer> getAllSalesVolume(){
			Map<String,Integer> rs = Maps.newLinkedHashMap();
			String sql="SELECT t.`product_name`,t.`color`,SUM(t.`sales_volume`*IFNULL(a.`pack_num`,1)) FROM `amazoninfo_sale_report` t "+
					" WHERE t.`product_name` IS NOT NULL AND t.`product_name` !='Inateck Old' AND t.`product_name` !='Inateck other' AND t.`order_type` = '1' GROUP BY t.`product_name`,t.`color`";
			List<Object[]> list= saleReportDao.findBySql(sql);
			for(Object[] obj:list){
				String productName = obj[0].toString();
				String color = obj[1]==null?"":obj[1].toString();
				if (StringUtils.isNotEmpty(color)) {
					productName = productName + "_" + color;
				}
				Integer quantity = obj[2]==null?0:Integer.parseInt(obj[2].toString());
				rs.put(productName, quantity);
		    }
			return rs;
		}
		
		/**
		 * 统计产品的销量
		 * @return Map<String,Integer> [productName qty]
		 */
		public Integer getSalesVolumeByProduct(String productName, String startDate){
			String sql="SELECT SUM(t.`sales_volume`*IFNULL(a.`pack_num`,1)) FROM `amazoninfo_sale_report` t WHERE "+
					" DATE_FORMAT(t.`date`,'%Y-%m-%d')>=:p1 "+
					" AND CONCAT(t.`product_name`,CASE  WHEN t.`color`='' THEN '' ELSE CONCAT('_',t.`color`) END) =:p2 AND t.`order_type` = '1'";
			try {
				int num = Integer.parseInt(saleReportDao.findBySql(sql, new Parameter(startDate, productName)).get(0).toString());
				return num;
			} catch (Exception e) {
				return 0;
			}
		}
		   
	   public Map<String,Integer> findPromotions(Date date){
		   Map<String,Integer>  map=Maps.newHashMap();
		   String sql="SELECT p.`country`,SUM(p.`quantity`) FROM amazoninfo_promotions_report p WHERE p.`purchase_date`=:p1 "+
	         " AND (p.`promotion_ids`!='闪购' AND p.promotion_ids!='B2B Free Shipping'   AND ((p.promotion_ids LIKE '%Core Free Shipping%'  AND p.promotion_ids  LIKE '%,%') OR p.promotion_ids NOT LIKE '%Core Free Shipping%' ) ) "+
	         " GROUP BY p.country ";
		   List<Object[]> list=saleReportDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(date)));
		   for (Object[] obj: list) {
			  map.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
		   }
		   return map;
	   }
	   
	   public Map<String,Map<String,Integer>> findPromotionsId(Date date){
		   Map<String,Map<String,Integer>> map=Maps.newLinkedHashMap();
		   String sql="SELECT p.`country`,p.`promotion_ids`,SUM(p.`quantity`) quantity FROM amazoninfo_promotions_report p WHERE p.`purchase_date`=:p1  "+
	        " AND (p.`promotion_ids`!='闪购' AND p.promotion_ids!='B2B Free Shipping'   AND ((p.promotion_ids LIKE '%Core Free Shipping%'  AND p.promotion_ids  LIKE '%,%') OR p.promotion_ids NOT LIKE '%Core Free Shipping%' ) ) "+
	        " GROUP BY p.country ,p.`promotion_ids` ORDER BY country DESC,quantity DESC ";
		   List<Object[]> list=saleReportDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(date)));
		   for (Object[] obj:list) {
			   Map<String,Integer> temp=map.get(obj[0].toString());
			   if(temp==null){
				   temp=Maps.newLinkedHashMap();
				   map.put(obj[0].toString(),temp);
			   }
			   temp.put(obj[1].toString(), Integer.parseInt(obj[2].toString()));
		   }
		   return map;
	   }
	   
	   
	   /**
	    * 连续三个月销量下滑产品预警
	    * key：国家,产品,三个月销量
	    */
	   public Map<String,Map<String,String>> getSaleWarnByMonth(){
		   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		  Date endDay = DateUtils.getFirstDayOfMonth(new Date());
		  Date startDay = DateUtils.addMonths(endDay, -3);
		  Set<String> productColors =this.getNewProColors();
		  String thirdMonth=sdf.format(startDay);//三个月前
		  String secondMonth=sdf.format(DateUtils.addMonths(endDay, -2));
		  String firstMonth=sdf.format(DateUtils.addMonths(endDay, -1));
		  
		   String sql="SELECT (CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`) END) AS proName,DATE_FORMAT(a.`date`,'%Y-%m')," +
		   		" SUM(CASE WHEN a.`country` IN ('de','it','es','fr','uk')  THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END ) AS eu," +
		   		" SUM(CASE WHEN a.`country` IN ('com','ca')  THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END ) AS com," +
		   		" SUM(CASE WHEN a.`country` IN ('jp')  THEN a.`sales_volume`*IFNULL(a.`pack_num`,1) ELSE 0 END ) AS jp" +
		   		" FROM amazoninfo_sale_report AS a  WHERE  a.`order_type` = '1' and a.`date`>=:p1 AND a.`date`<:p2 AND a.`product_name` <>'' " +
		   		"  AND (CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`) END) IN :p3"+
		   		" GROUP BY a.`product_name`,a.`color`,DATE_FORMAT(a.`date`,'%Y-%m')";
		  List<Object[]> list= this.saleReportDao.findBySql(sql, new Parameter(startDay,endDay,productColors));
		   Map<String,Map<String,Map<String,Integer>>> tempMap = Maps.newHashMap();
		   for(Object[] obj:list){
			   String proName = obj[0].toString();
			   String month = obj[1].toString();
			   Integer euQ = Integer.parseInt(obj[2].toString());
			   Integer comQ = Integer.parseInt(obj[3].toString());  
			   Integer jpQ = Integer.parseInt(obj[4].toString());
			   
			   //添加欧洲的
			   Map<String,Map<String,Integer>> inMap=null;
			   if(tempMap.get("eu")==null){
				   inMap = Maps.newHashMap();
			   }else{
				   inMap = tempMap.get("eu");
			   }
			   
			   Map<String,Integer> inInMap=null;
			   if(inMap.get(proName)==null){
				   inInMap=Maps.newHashMap();
			   }else{
				   inInMap =inMap.get(proName);
			   }
			   
			   inInMap.put(month, euQ);
			   inMap.put(proName, inInMap);
			   tempMap.put("eu", inMap);
			   //添加美国的
			   Map<String,Map<String,Integer>> inMap1=null;
			   if(tempMap.get("com")==null){
				   inMap1 = Maps.newHashMap();
			   }else{
				   inMap1 = tempMap.get("com");
			   }
			   
			   Map<String,Integer> inInMap1=null;
			   if(inMap1.get(proName)==null){
				   inInMap1=Maps.newHashMap();
			   }else{
				   inInMap1 =inMap1.get(proName);
			   }
			   
			   inInMap1.put(month, comQ);
			   inMap1.put(proName, inInMap1);
			   tempMap.put("com", inMap1);
			   //添加日本的
			   Map<String,Map<String,Integer>> inMap2=null;
			   if(tempMap.get("jp")==null){
				   inMap2 = Maps.newHashMap();
			   }else{
				   inMap2 = tempMap.get("jp");
			   }
			   
			   Map<String,Integer> inInMap2=null;
			   if(inMap2.get(proName)==null){
				   inInMap2=Maps.newHashMap();
			   }else{
				   inInMap2 =inMap2.get(proName);
			   }
			   
			   inInMap2.put(month, jpQ);
			   inMap2.put(proName, inInMap2);
			   tempMap.put("jp", inMap2);
		   }
		   
		   Map<String,Map<String,String>> rs =Maps.newHashMap();
		   for (Map.Entry<String, Map<String, Map<String, Integer>>> entry : tempMap.entrySet()) { 
		       String county=entry.getKey();
		       for (Map.Entry<String, Map<String, Integer>> entryRs : entry.getValue().entrySet()) {
			       String productName=entryRs.getKey();
				   Map<String,Integer> monthMap = entryRs.getValue();
				   Integer firstQ = monthMap.get(firstMonth)==null?0:monthMap.get(firstMonth);
				   Integer secondQ = monthMap.get(secondMonth)==null?0:monthMap.get(secondMonth);
				   Integer thirdQ = monthMap.get(thirdMonth)==null?0:monthMap.get(thirdMonth);//三个月前
				   if("jp".equals(county)){
					   //前两个月销量大于150； 并且递减
					   if(thirdQ>100&&secondQ>100&&thirdQ>secondQ&&secondQ>firstQ&&((thirdQ-firstQ)*100/thirdQ)>15){
						   Map<String,String> inMap=null;
						   if(rs.get(county)==null){
							   inMap = Maps.newHashMap();
						   }else{
							   inMap = rs.get(county);
						   }
						   inMap.put(productName, thirdQ+","+secondQ+","+firstQ+","+((thirdQ-firstQ)*100/thirdQ));
						   rs.put(county, inMap);
					   }
				   }else{ 
					   //前两个月销量大于150； 并且递减
					   if(thirdQ>150&&secondQ>150&&thirdQ>secondQ&&secondQ>firstQ&&((thirdQ-firstQ)*100/thirdQ)>15){
						   Map<String,String> inMap=null;
						   if(rs.get(county)==null){
							   inMap = Maps.newHashMap();
						   }else{
							   inMap = rs.get(county);
						   }
						   inMap.put(productName, thirdQ+","+secondQ+","+firstQ+","+((thirdQ-firstQ)*100/thirdQ));
						   rs.put(county, inMap);
					   }
				   }
				
			   }
		   }
		   
		   return rs;
	   }
	   
		//淘汰的和新品
		public Set<String> getNewProColors(){
			String sql="SELECT CASE WHEN t.`color`='' THEN t.product_name ELSE CONCAT(t.product_name,'_',t.color) END proName "+
					" FROM `psi_product_eliminate` t WHERE t.is_new='1' AND t.del_flag='0' GROUP BY t.product_name,t.color ";
			List<String> list=this.saleReportDao.findBySql(sql);
			Set<String> newProColorSets = Sets.newHashSet();
			newProColorSets.addAll(list);
			return newProColorSets;
		}
		
		public static void main(String[] args) {
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/spring-context.xml");
			SaleReportService saleReporService = context.getBean(SaleReportService.class);
			//saleReporService.updateProductProfit();
			String productName = "Inateck FE3001";
			System.out.println(saleReporService.getCurrMonthSaleByName(productName, DateUtils.getFirstDayOfMonth(new Date())));
			/*float[] zhishu = saleReporService.findSalesIndex(null, null);
			System.out.println(zhishu.toString());
			zhishu = saleReporService.findSalesIndex("Scanner", null);
			System.out.println(zhishu.toString());
			zhishu = saleReporService.findSalesIndex(null, "Inateck FE2004");
			System.out.println(zhishu.toString());*/
			context.close();
		}
		
		//日期   产品名 国家
		public Map<String,Map<String, Map<String,SaleReport>>> getSalesByProductByYear(SaleReport saleReport, Map<String, Float> rateRs,String type){
			String currencyType ="EUR"; //统计的货币类型（EUR/USD）
			Date start=saleReport.getStart();
			Date end=saleReport.getEnd();
			String typeSql = "'%Y'";
            if("1".equals(type)){
            	typeSql="'%Y%m'";
            }
			String sql ="SELECT a.`country`,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)),round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
						MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
						MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE sum(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),(CASE when a.`product_name` is null  then 'noProductName'  else CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) end) product,DATE_FORMAT(a.`date`,"+typeSql+") dates,SUM(a.`real_sales_volume`*IFNULL(a.`pack_num`,1)), " +
						"round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
						MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) FROM amazoninfo_sale_report a  WHERE a.order_type='1' and  a.`date`>=:p1 AND a.`date`<=:p2  GROUP BY a.`country`,product,dates ORDER BY a.`country`,dates DESC ";
			List<Object[]> list =saleReportDao.findBySql(sql, new Parameter(start,end));
			
			Map<String,Map<String, Map<String,SaleReport>>> rs = Maps.newLinkedHashMap();
			for (Object[] objs : list) {
				String country = objs[0].toString(); 
				Integer salesVolume = ((BigDecimal)(objs[1]==null?new BigDecimal(0):objs[1])).intValue();
				Integer sureSalesVolume = ((BigDecimal)(objs[2]==null?new BigDecimal(0):objs[2])).intValue();
				Float sales = ((BigDecimal)(objs[3]==null?new BigDecimal(0):objs[3])).floatValue();
				Float sureSales = ((BigDecimal)(objs[4]==null?new BigDecimal(0):objs[4])).floatValue();
				String productName = (objs[5]==null)?"":objs[5].toString(); 
				String date = objs[6].toString(); 
				Integer realSalesVolume = ((BigDecimal)(objs[7]==null?new BigDecimal(0):objs[7])).intValue();
				Float realSales = ((BigDecimal)(objs[8]==null?new BigDecimal(0):objs[8])).floatValue();
				Map<String, Map<String,SaleReport>> datas = rs.get(date);
				if(datas==null){
					datas = Maps.newLinkedHashMap();
					rs.put(date, datas);
				}
				Map<String, SaleReport> sale=datas.get(productName);
				if(sale==null){
					sale=Maps.newLinkedHashMap();
					datas.put(productName, sale);
				}
				sale.put(country,  new SaleReport(sales,sureSales,realSales,salesVolume,sureSalesVolume,realSalesVolume));
			}
			return rs;
		}
		
		public Map<String,Object[]> findUsSales(SaleReport saleReport){
			String sql="SELECT DATE_FORMAT(t.`purchase_date`,'%Y%m')AS mon, "+
						" SUM(CASE WHEN a.`postal_code` LIKE '35%' OR a.`postal_code` LIKE '36%' THEN t.`order_total` END) AS '阿拉巴马', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '995%' OR a.`postal_code` LIKE '996%' OR a.`postal_code` LIKE '997%' OR a.`postal_code` LIKE '998%' OR a.`postal_code` LIKE '999%' THEN t.`order_total` END) AS '阿拉斯加州',  "+
						" SUM(CASE WHEN a.`postal_code` LIKE '72%' THEN t.`order_total` END) AS '阿肯色州',  "+
						" SUM(CASE WHEN a.`postal_code` LIKE '197%' OR a.`postal_code` LIKE '198%' OR a.`postal_code` LIKE '199%' THEN t.`order_total` END) AS '特拉华州',  "+
						" SUM(CASE WHEN a.`postal_code` LIKE '96%' THEN t.`order_total` END) AS '夏威夷州',  "+
						" SUM(CASE WHEN a.`postal_code` LIKE '83%' THEN t.`order_total` END) AS '爱达荷州',  "+
						" SUM(CASE WHEN a.`postal_code` LIKE '50%' OR a.`postal_code` LIKE '52%' THEN t.`order_total` END) AS '爱荷华州',  "+
						" SUM(CASE WHEN a.`postal_code` LIKE '70%' THEN t.`order_total` END) AS '路易斯安那州',  "+
						" SUM(CASE WHEN a.`postal_code` LIKE '04%' THEN t.`order_total` END) AS '缅因州',  "+
						" SUM(CASE WHEN a.`postal_code` LIKE '390%' OR a.`postal_code` LIKE '391%' OR a.`postal_code` LIKE '392%' OR a.`postal_code` LIKE '393%' OR a.`postal_code` LIKE '394%' OR a.`postal_code` LIKE '395%' OR a.`postal_code` LIKE '396%' OR a.`postal_code` LIKE '397%' THEN t.`order_total` END) AS '密西西比州',  "+
						" SUM(CASE WHEN a.`postal_code` LIKE '63%' OR a.`postal_code` LIKE '64%' OR a.`postal_code` LIKE '65%' THEN t.`order_total` END) AS '密苏里州',  "+
						" SUM(CASE WHEN a.`postal_code` LIKE '59%' THEN t.`order_total` END) AS '蒙大拿州',  "+
						" SUM(CASE WHEN a.`postal_code` LIKE '68%' THEN t.`order_total` END) AS '内布拉斯加州',  "+
						" SUM(CASE WHEN a.`postal_code` LIKE '03%' THEN t.`order_total` END) AS '新罕布什尔州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '87%' THEN t.`order_total` END) AS '新墨西哥州',  "+
						" SUM(CASE WHEN a.`postal_code` LIKE '73%' OR a.`postal_code` LIKE '74%' THEN t.`order_total` END) AS '俄克拉荷马州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '97%' THEN t.`order_total` END) AS '俄勒冈州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '028%' OR a.`postal_code` LIKE '029%' THEN t.`order_total` END) AS '罗德岛州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '57%' THEN t.`order_total` END) AS '南达科他州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '84%' THEN t.`order_total` END) AS '犹他州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '05%' THEN t.`order_total` END) AS '佛蒙特州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '82%' THEN t.`order_total` END) AS '怀俄明州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '200%' OR a.`postal_code` LIKE '201%' OR a.`postal_code` LIKE '202%' OR a.`postal_code` LIKE '203%' OR a.`postal_code` LIKE '204%' OR a.`postal_code` LIKE '205%' THEN t.`order_total` END) AS '哥伦比亚特区', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '85%' THEN t.`order_total` END) AS '阿利桑那州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '90%' OR a.`postal_code` LIKE '92%' OR a.`postal_code` LIKE '94%' OR a.`postal_code` LIKE '95%' THEN t.`order_total` END) AS '加利福尼亚州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '80%' THEN t.`order_total` END) AS '科罗拉多州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '06%' THEN t.`order_total` END) AS '康涅狄格州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '32%' OR a.`postal_code` LIKE '33%' THEN t.`order_total` END) AS '佛罗里达州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '30%' OR a.`postal_code` LIKE '31%' OR a.`postal_code` LIKE '398%' OR a.`postal_code` LIKE '399%' THEN t.`order_total` END) AS '乔治亚州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '60%' OR a.`postal_code` LIKE '61%' OR a.`postal_code` LIKE '62%' THEN t.`order_total` END) AS '伊利诺斯州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '46%' OR a.`postal_code` LIKE '47%' THEN t.`order_total` END) AS '印第安纳州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '66%' OR a.`postal_code` LIKE '67%' THEN t.`order_total` END) AS '堪萨斯州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '40%' THEN t.`order_total` END) AS '肯塔基州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '21%' OR a.`postal_code` LIKE '206%' OR a.`postal_code` LIKE '207%' OR a.`postal_code` LIKE '208%' OR a.`postal_code` LIKE '209%' THEN t.`order_total` END) AS '马里兰州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '01%' OR a.`postal_code` LIKE '020%' OR a.`postal_code` LIKE '021%' OR a.`postal_code` LIKE '022%' OR a.`postal_code` LIKE '023%' OR a.`postal_code` LIKE '024%' OR a.`postal_code` LIKE '025%' OR a.`postal_code` LIKE '026%' OR a.`postal_code` LIKE '027%' THEN t.`order_total` END) AS '马萨诸塞州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '48%' OR a.`postal_code` LIKE '49%' THEN t.`order_total` END) AS '密歇根州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '55%' THEN t.`order_total` END) AS '明尼苏达州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '89%' THEN t.`order_total` END) AS '内华达州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '07%' OR a.`postal_code` LIKE '08%' THEN t.`order_total` END) AS '新泽西州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '10%' OR a.`postal_code` LIKE '11%' OR a.`postal_code` LIKE '12%' OR a.`postal_code` LIKE '14%' THEN t.`order_total` END) AS '纽约州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '27%' OR a.`postal_code` LIKE '28%' THEN t.`order_total` END) AS '北卡罗来纳州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '58%' THEN t.`order_total` END) AS '北达科他州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '43%' OR a.`postal_code` LIKE '44%' OR a.`postal_code` LIKE '45%' THEN t.`order_total` END) AS '俄亥俄州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '15%' OR a.`postal_code` LIKE '16%' OR a.`postal_code` LIKE '17%' OR a.`postal_code` LIKE '190%' OR a.`postal_code` LIKE '191%' OR a.`postal_code` LIKE '192%' OR a.`postal_code` LIKE '193%' OR a.`postal_code` LIKE '194%' OR a.`postal_code` LIKE '195%' OR a.`postal_code` LIKE '196%' THEN t.`order_total` END) AS '宾夕法尼亚州',  "+
						" SUM(CASE WHEN a.`postal_code` LIKE '29%' THEN t.`order_total` END) AS '南卡罗来纳州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '37%' OR a.`postal_code` LIKE '38%' THEN t.`order_total` END) AS '田纳西州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '75%' OR a.`postal_code` LIKE '77%' OR a.`postal_code` LIKE '78%' THEN t.`order_total` END) AS '得克萨斯州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '23%' THEN t.`order_total` END) AS '弗吉尼亚州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '98%' OR a.`postal_code` LIKE '990%' OR a.`postal_code` LIKE '991%' OR a.`postal_code` LIKE '992%' OR a.`postal_code` LIKE '993%' OR a.`postal_code` LIKE '994%' THEN t.`order_total` END) AS '华盛顿州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '25%' OR a.`postal_code` LIKE '26%' THEN t.`order_total` END) AS '西弗吉尼亚州', "+
						" SUM(CASE WHEN a.`postal_code` LIKE '53%' THEN t.`order_total` END) AS '威斯康辛州' "+
						" FROM `amazoninfo_order` t, `amazoninfo_address` a "+
						" WHERE t.`shipping_address`= a.`id` AND t.`order_status`='Shipped' AND a.`country_code`='US' AND t.`purchase_date`>=:p1 AND t.`purchase_date`<:p2  GROUP BY mon ";
			List<Object[]> list =saleReportDao.findBySql(sql, new Parameter(saleReport.getStart(),DateUtils.addDays(saleReport.getEnd(),1)));
			Map<String,Object[]> map=Maps.newHashMap();
			for (Object[] obj: list) {
				map.put(obj[0].toString(),obj);
			}
			return map;
		}
		
		public Map<String,Integer> getSales(String productName,Date start,Date end,String country) {
			Map<String,Integer> rs = Maps.newHashMap();
			String sql="SELECT DATE_FORMAT(date,'%Y-%m-%d') dates,sum(sure_sales_volume*IFNULL(t.`pack_num`,1)) FROM amazoninfo_sale_report t WHERE order_type='1' and date>=:p2 and date<=:p3 and country=:p4  and (CASE WHEN t.`color`='' THEN t.product_name ELSE CONCAT(t.product_name,'_',t.color) end)=:p1 group by dates ";
			List<Object[]> salesList=saleReportDao.findBySql(sql,new Parameter(productName,start,end,country));
			for (Object[] obj : salesList) {
				 rs.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
			}
			return rs;
		}
		
		public Map<String, Float> getPrice(){
			String sql ="SELECT t.`country`,(CASE WHEN t.`color`='' THEN t.product_name ELSE CONCAT(t.product_name,'_',t.color) END),DATE_FORMAT(date,'%Y-%m-%d'),price "+
					" FROM amazoninfo_sale_report t  WHERE t.order_type='1' and date>=:p1  ";
			List<Object[]> 	list = saleReportDao.findBySql(sql,new Parameter(DateUtils.addMonths(new Date(),-3)));
			Map<String,Float> rs = Maps.newHashMap();

			for (Object[] objs : list) {
				if(objs[0]!=null&&objs[1]!=null&&objs[2]!=null&&objs[3]!=null){
					String name=objs[1].toString();
					String country = objs[0].toString(); 
					String date=objs[2].toString();
					Float price = ((BigDecimal)objs[3]).floatValue();
					rs.put(country+"-"+name+"-"+date, price);
				}
			}
			return rs;
		}
		
		public Map<String,Integer> getSalesByLine(String productName,Date start,Date end,String country) {
			Map<String,Integer> rs = Maps.newHashMap();
			String sql="SELECT DATE_FORMAT(date,'%Y%m%d') dates,sum(sure_sales_volume*IFNULL(t.`pack_num`,1)) FROM amazoninfo_sale_report t "+
			" WHERE order_type='1' and date>=:p1 and date<=:p2 and country=:p3 and (CASE WHEN t.`color`='' THEN t.product_name ELSE CONCAT(t.product_name,'_',t.color) end)=:p4 group by dates";
			List<Object[]> salesList=saleReportDao.findBySql(sql,new Parameter(start,end,country,productName));
			for (Object[] obj : salesList) {
				 rs.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
			}
			return rs;
		}
		
		
		public Map<String,Map<String,Integer>> getSales(List<String> nameList) {
			Date today=new Date();
			Map<String,Map<String,Integer>> rs = Maps.newHashMap();
			String sql="SELECT DATE_FORMAT(date,'%Y%m%d') dates,sum(sure_sales_volume*IFNULL(t.`pack_num`,1)),country,(CASE WHEN t.`color`='' THEN t.product_name ELSE CONCAT(t.product_name,'_',t.color) end) name FROM amazoninfo_sale_report t WHERE order_type='1' and date>=:p1 and date<=:p2 ";
			if(nameList!=null){
				sql+=" and (CASE WHEN t.`color`='' THEN t.product_name ELSE CONCAT(t.product_name,'_',t.color) end) in :p3 ";
			}
			sql+=" group by country,dates,name ";
			List<Object[]> salesList=null;
			if(nameList!=null){
				salesList=saleReportDao.findBySql(sql,new Parameter(DateUtils.addMonths(today,-1),today,nameList));
			}else{
				salesList=saleReportDao.findBySql(sql,new Parameter(DateUtils.addMonths(today,-1),today));
			}
			for (Object[] obj : salesList) {
				String date=obj[0].toString();
				String name=(obj[3]==null?"":obj[3].toString());
				Integer rank=Integer.parseInt(obj[1]==null?"0":obj[1].toString());
				String country=obj[2].toString();
				Map<String,Integer> temp=rs.get(country+"-"+name);
				if(temp==null){
					temp=Maps.newHashMap();
					rs.put(country+"-"+name, temp);
				}
				temp.put(date, rank);
			}
			return rs;
		}
		
		
		public Map<String,Map<String, Map<String,SaleReport>>> getSalesByProductName(SaleReport saleReport, Map<String, Float> rateRs){
			String currencyType = saleReport.getCurrencyType(); //统计的货币类型（EUR/USD）
			Date start = saleReport.getStart();
			Date end = saleReport.getEnd();
			String typeSql = "'%Y%m%d'";
			if("2".equals(saleReport.getSearchType())){
				if(start==null){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addWeeks(today, -19);
					end = today;
					saleReport.setStart(start);
					saleReport.setEnd(end);
				}else{
					Date end1 = DateUtils.addWeeks(start, 3);
					if(end.before(end1)){
						end = end1;
						saleReport.setEnd(end1);
					}
				}
				start = DateUtils.getMonday(start);
				end = DateUtils.getSunday(end);
				typeSql="'%x%v'";
			}else if("3".equals(saleReport.getSearchType())){
				if(start==null){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addMonths(today, -18);
					end = today;
					saleReport.setStart(start);
					saleReport.setEnd(end);
				}else{
					Date end1 = DateUtils.addMonths(start, 3);
					if(end.before(end1)){
						end = end1;
						saleReport.setEnd(end1);
					}
				}
				start = DateUtils.getFirstDayOfMonth(start);
				end = DateUtils.getLastDayOfMonth(end);
				typeSql="'%Y%m'";
			}else{
				if(start==null){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addMonths(today, -1);
					end = today;
					saleReport.setStart(start);
					saleReport.setEnd(end);
				}else{
					Date end1 = DateUtils.addDays(start, 3);
					if(end.before(end1)){
						end = end1;
						saleReport.setEnd(end1);
					}
				}
			}
			List<Object[]> list = null;
			if(StringUtils.isNotBlank(saleReport.getCountry())){
				String sql ="SELECT a.`country`,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)),round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
						MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
						MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE sum(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),(CASE when a.`product_name` is null  then 'noProductName'  else CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) end) product,DATE_FORMAT(a.`date`,"+typeSql+") dates,SUM(a.`real_sales_volume`*IFNULL(a.`pack_num`,1)), " +
						"round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
						MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) FROM amazoninfo_sale_report a  WHERE a.order_type='1' and  a.`date`>=:p1 AND a.`date`<=:p2 and country=:p3 GROUP BY a.`country`,product,dates ORDER BY a.`country`,dates DESC ";
				list = saleReportDao.findBySql(sql, new Parameter(start,end,saleReport.getCountry()));
			}else{
				String sql ="SELECT a.`country`,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)),SUM(a.`sure_sales_volume`*IFNULL(a.`pack_num`,1)),round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`sales`)*"+
						MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN sum(a.`sure_sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN sum(a.`sure_sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN sum(a.`sure_sales`)*"+
						MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`sure_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE sum(a.`sure_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2),(CASE when a.`product_name` is null  then 'noProductName'  else CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) end) product,DATE_FORMAT(a.`date`,"+typeSql+") dates,SUM(a.`real_sales_volume`*IFNULL(a.`pack_num`,1)), " +
						"round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(a.`real_sales`)*"
						+MathUtils.getRate("EUR", currencyType, rateRs)+" WHEN a.`country`='uk' THEN SUM(a.`real_sales`)*"+MathUtils.getRate("GBP", currencyType, rateRs)+" WHEN a.`country`='ca' THEN SUM(a.`real_sales`)*"
						+MathUtils.getRate("CAD", currencyType, rateRs)+" WHEN a.`country`='jp' THEN SUM(a.`real_sales`)*"+
						MathUtils.getRate("JPY", currencyType, rateRs)+" WHEN a.`country`='mx' THEN sum(a.`real_sales`)*"+MathUtils.getRate("MXN", currencyType, rateRs)+"  ELSE SUM(a.`real_sales`)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ),2) FROM amazoninfo_sale_report a  WHERE a.order_type='1' and  a.`date`>=:p1 AND a.`date`<=:p2  GROUP BY a.`country`,product,dates ORDER BY a.`country`,dates DESC ";
				list = saleReportDao.findBySql(sql, new Parameter(start,end));
			}
			Map<String,Map<String, Map<String,SaleReport>>> rs = Maps.newHashMap();
			for (Object[] objs : list) {
				String country = objs[0].toString(); 
				Integer salesVolume = ((BigDecimal)(objs[1]==null?new BigDecimal(0):objs[1])).intValue();
				Integer sureSalesVolume = ((BigDecimal)(objs[2]==null?new BigDecimal(0):objs[2])).intValue();
				Float sales = ((BigDecimal)(objs[3]==null?new BigDecimal(0):objs[3])).floatValue();
				Float sureSales = ((BigDecimal)(objs[4]==null?new BigDecimal(0):objs[4])).floatValue();
				String productName = (objs[5]==null)?"":objs[5].toString(); 
				String date = objs[6].toString(); 
				if("2".equals(saleReport.getSearchType())){
					Integer i = Integer.parseInt(date.substring(4));
					if(i==53){
						Integer year = Integer.parseInt(date.substring(0,4));
						date =  (year+1)+"01";
					}else if (date.contains("2016")){
						i = i+1;
						date = "2016"+(i<10?("0"+i):i);
					}
				}
				Integer realSalesVolume = ((BigDecimal)(objs[7]==null?new BigDecimal(0):objs[7])).intValue();
				Float realSales = ((BigDecimal)(objs[8]==null?new BigDecimal(0):objs[8])).floatValue();
				Map<String, Map<String,SaleReport>> datas = rs.get(country);
				if(datas==null){
					datas = Maps.newLinkedHashMap();
					rs.put(country, datas);
				}
				Map<String, SaleReport> sale=datas.get(date);
				if(sale==null){
					sale=Maps.newLinkedHashMap();
					datas.put(date, sale);
				}
				sale.put(productName,  new SaleReport(sales,sureSales,realSales,salesVolume,sureSalesVolume,realSalesVolume));
				
				//欧洲汇总
				if ("de,fr,it,es,uk".contains(country)) {
					Map<String, Map<String,SaleReport>> datas2 = rs.get("eu");
					if(datas2==null){
						datas2 = Maps.newLinkedHashMap();
						rs.put("eu", datas2);
					}
					Map<String, SaleReport>  dateMap = datas2.get(date);
					if(dateMap==null){
						dateMap = Maps.newLinkedHashMap();
						datas2.put(date, dateMap);
					}
					SaleReport report=dateMap.get(productName);
					if(report==null){
						report = new SaleReport(0f,0f,0f,0,0,0);
						dateMap.put(productName, report);
					}
					report.setSales(sales+report.getSales());
					report.setSalesVolume(salesVolume+report.getSalesVolume());
					report.setSureSales(sureSales+report.getSureSales());
					report.setSureSalesVolume(sureSalesVolume+report.getSureSalesVolume());
					report.setRealSales(realSales+report.getRealSales());
					report.setRealSalesVolume(realSalesVolume+report.getRealSalesVolume());
				}
				
				//英语国家汇总
				if ("com,ca,uk".contains(country)) {
					Map<String, Map<String,SaleReport>> datas2 = rs.get("en");
					if(datas2==null){
						datas2 = Maps.newLinkedHashMap();
						rs.put("en", datas2);
					}
					Map<String, SaleReport>  dateMap = datas2.get(date);
					if(dateMap==null){
						dateMap = Maps.newLinkedHashMap();
						datas2.put(date, dateMap);
					}
					SaleReport report=dateMap.get(productName);
					if(report==null){
						report = new SaleReport(0f,0f,0f,0,0,0);
						dateMap.put(productName, report);
					}
					report.setSales(sales+report.getSales());
					report.setSalesVolume(salesVolume+report.getSalesVolume());
					report.setSureSales(sureSales+report.getSureSales());
					report.setSureSalesVolume(sureSalesVolume+report.getSureSalesVolume());
					report.setRealSales(realSales+report.getRealSales());
					report.setRealSalesVolume(realSalesVolume+report.getRealSalesVolume());
				}
				
				//非英语国家汇总
				if ("de,fr,it,es,jp".contains(country)) {
					Map<String, Map<String,SaleReport>> datas2 = rs.get("unEn");
					if(datas2==null){
						datas2 = Maps.newLinkedHashMap();
						rs.put("unEn", datas2);
					}
					Map<String, SaleReport>  dateMap = datas2.get(date);
					if(dateMap==null){
						dateMap = Maps.newLinkedHashMap();
						datas2.put(date, dateMap);
					}
					SaleReport report=dateMap.get(productName);
					if(report==null){
						report = new SaleReport(0f,0f,0f,0,0,0);
						dateMap.put(productName, report);
					}
					report.setSales(sales+report.getSales());
					report.setSalesVolume(salesVolume+report.getSalesVolume());
					report.setSureSales(sureSales+report.getSureSales());
					report.setSureSalesVolume(sureSalesVolume+report.getSureSalesVolume());
					report.setRealSales(realSales+report.getRealSales());
					report.setRealSalesVolume(realSalesVolume+report.getRealSalesVolume());
				}
			}
			return rs;
		}
		
		@Transactional(readOnly = false)
		public void updateInitAttr(String attr,Set<Integer> id){
			String attrUpdateSql="update amazoninfo_sale_report set product_attr=:p1 where id in :p2 ";
			saleReportDao.updateBySql(attrUpdateSql,new Parameter(attr,id));
		}
		
		
		public Map<Date,Map<String,Set<Integer>>> findSalesAttr(){
			Map<Date,Map<String,Set<Integer>>> map=Maps.newLinkedHashMap();
			String sql="SELECT t.id,(CASE WHEN p.`is_sale`='4' THEN '淘汰' WHEN p.`is_new`='1' THEN '新品' ELSE p.`is_sale` END) attr,t.`date` "+
				    " FROM amazoninfo_sale_report t JOIN psi_product_in_stock p "+
				    " ON t.`date`=p.`data_date` AND t.`country`=p.`country` AND p.`product_name`=CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT('_',t.`color`) ELSE '' END) where t.`date`>='2016-06-01' and t.`date`<'2017-08-21' order by t.id desc ";
			List<Object[]> list=saleReportDao.findBySql(sql);
			for (Object[] obj: list) {
				Integer id=Integer.parseInt(obj[0].toString());
				String attr=obj[1].toString();
				attr = DictUtils.getDictLabel(attr, "product_position", attr);
				//if(!"普通".equals(attr)){
					Date date=(Date)obj[2];
					Map<String,Set<Integer>> temp=map.get(date);
					if(temp==null){
						temp=Maps.newLinkedHashMap();
						map.put(date,temp);
					}
					Set<Integer> sets=temp.get(attr);
					if(sets==null){
						sets=Sets.newHashSet();
						temp.put(attr, sets);
					}
					sets.add(id);
				//}
			}
			return map;
		}
		
		
		@Transactional(readOnly = false)
		public void updateIsNullAttr(){
			 String sql="SELECT t.id,(CASE WHEN p.`is_sale`='4' THEN '淘汰' WHEN p.`is_new`='1' THEN '新品' ELSE p.`is_sale END) attr FROM amazoninfo_sale_report t JOIN psi_product_in_stock p  "+
		                " ON t.`date`=DATE_ADD(p.`data_date`,INTERVAL -1 DAY)  AND t.`country`=p.`country` AND p.`product_name`=CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT('_',t.`color`) ELSE '' END) "+
		                " WHERE  t.`date`>='2017-01-01' AND t.`product_attr` IS NULL "+
						" AND t.product_name IS NOT NULL AND t.product_name!='' AND t.`order_type`='1' "+
						" AND t.product_name!='Inateck Old' AND t.product_name!='Inateck other' ";
			 List<Object[]> list=saleReportDao.findBySql(sql);
			 String attrUpdateSql="update amazoninfo_sale_report set product_attr=:p1 where id = :p2 ";
			 for (Object[] obj: list) {
				 saleReportDao.updateBySql(attrUpdateSql,new Parameter(DictUtils.getDictLabel(obj[1].toString(), "product_position", obj[1].toString()),Integer.parseInt(obj[0].toString())));
			 }
		}
		
	
		
		@Transactional(readOnly = false)
		public void updateInitProfitAttr(String attr,Set<Integer> id){
			String attrUpdateSql="update amazoninfo_sale_profit set product_attr=:p1 where id in :p2 ";
			saleReportDao.updateBySql(attrUpdateSql,new Parameter(attr,id));
		}
		
		@Transactional(readOnly = false)
		public void updateAttr(){
			String temp="SELECT t.id FROM amazoninfo_sale_profit t  JOIN psi_product_in_stock p ON t.`day`=DATE_FORMAT(p.`data_date`,'%Y%m%d') AND t.`country`=p.`country` AND p.`product_name`=t.`product_name` "+
                       " WHERE t.`day`>='20170101' AND t.`product_attr`='淘汰' AND t.`product_attr`!=(CASE WHEN p.`is_sale`='4' THEN '淘汰' WHEN p.`is_new`='1' THEN '新品' WHEN p.`is_sale`='1' THEN '爆款' WHEN p.`is_sale`='2' THEN '利润款' WHEN p.`is_sale`='3' THEN '主力' ELSE '普通' END)";
			List<String> idList=saleReportDao.findBySql(temp);
			String sql="UPDATE amazoninfo_sale_profit SET product_attr='普通' WHERE id IN :p1";	
			saleReportDao.updateBySql(sql, new Parameter(idList));
			
			
			String temp2="SELECT id FROM amazoninfo_report_month_type t 	JOIN ( "+
					" SELECT country,product_name,DATE_FORMAT(p.`data_date`,'%Y%m') dates, "+
					" SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT (CASE WHEN p.`is_sale`='4' THEN '淘汰' WHEN p.`is_new`='1' THEN '新品'  WHEN p.`is_sale`='1' THEN '爆款' WHEN p.`is_sale`='2' THEN '利润款' WHEN p.`is_sale`='3' THEN '主力' ELSE '普通' END) ORDER BY FIELD((CASE WHEN p.`is_sale`='4' THEN '淘汰' WHEN p.`is_new`='1' THEN '新品'  WHEN p.`is_sale`='1' THEN '爆款' WHEN p.`is_sale`='2' THEN '利润款' WHEN p.`is_sale`='3' THEN '主力' ELSE '普通' END),'淘汰','新品','爆款','利润款','主力','普通')),',',1) attr "+
					" FROM psi_product_in_stock p WHERE p.`data_date`>='2017-01-01' GROUP BY country,product_name,dates ) p "+
					" ON t.`month`=p.dates AND .t.`country`=p.`country` AND p.`product_name`=t.`product_name` "+
					" WHERE t.`month`>='201701' AND t.`product_attr`='淘汰' AND t.`product_attr`!=p.attr ";
								
			List<String> idList2=saleReportDao.findBySql(temp2);
			String sql2="UPDATE amazoninfo_report_month_type SET product_attr='普通' WHERE id IN :p1";	
			saleReportDao.updateBySql(sql2, new Parameter(idList2));
		}
		
		@Transactional(readOnly = false)
		public void updateIsNullAttr2(){
			 String sql="SELECT t.id,(CASE WHEN p.`is_sale`='4' THEN '淘汰' WHEN p.`is_new`='1' THEN '新品' ELSE p.`is_sale` END) attr FROM amazoninfo_sale_profit t JOIN psi_product_in_stock p  "+
		                " ON t.`date`=DATE_FORMAT(DATE_ADD(p.`data_date`,INTERVAL -1 DAY))  AND t.`country`=p.`country` AND p.`product_name`=t.`product_name` "+
		                " WHERE  t.`date`>='20170101' AND t.`product_attr` IS NULL "+
						" AND t.product_name IS NOT NULL AND t.product_name!='' AND t.`order_type`='1' "+
						" AND t.product_name!='Inateck Old' AND t.product_name!='Inateck other' ";
			 List<Object[]> list=saleReportDao.findBySql(sql);
			 String attrUpdateSql="update amazoninfo_sale_profit set product_attr=:p1 where id = :p2 ";
			 for (Object[] obj: list) {
				 saleReportDao.updateBySql(attrUpdateSql,new Parameter(DictUtils.getDictLabel(obj[1].toString(), "product_position", obj[1].toString()),Integer.parseInt(obj[0].toString())));
			 }
		}
		
		
		public Map<String,Map<String,Set<Integer>>> findProfitAttr(){
			Map<String,Map<String,Set<Integer>>>  map=Maps.newLinkedHashMap();
			String sql="SELECT t.id,(CASE WHEN p.`is_sale`='4' THEN '淘汰' WHEN p.`is_new`='1' THEN '新品' ELSE p.`is_sale` END) attr,t.`day` "+
					" FROM amazoninfo_sale_profit t JOIN psi_product_in_stock p "+
					" ON t.`day`=DATE_FORMAT(p.`data_date`,'%Y%m%d') AND t.`country`=p.`country` AND p.`product_name`=t.`product_name` "+
					" WHERE t.`day`>='20160601' and t.`day`<'20170821' order by t.id desc ";
			
			List<Object[]> list=saleReportDao.findBySql(sql);
			for (Object[] obj: list) {
				Integer id=Integer.parseInt(obj[0].toString());
				String attr=obj[1].toString();
				attr = DictUtils.getDictLabel(attr, "product_position", attr);
				//if(!"普通".equals(attr)){
					String date=obj[2].toString();
					Map<String,Set<Integer>> temp=map.get(date);
					if(temp==null){
						temp=Maps.newLinkedHashMap();
						map.put(date,temp);
					}
					Set<Integer> sets=temp.get(attr);
					if(sets==null){
						sets=Sets.newHashSet();
						temp.put(attr, sets);
					}
					sets.add(id);
				//}
			}
			return map;
		}
		
		
		@Transactional(readOnly = false)
		public void updateInitMonthProfitAttr(String attr,Set<Integer> id){
			String attrUpdateSql="update amazoninfo_report_month_type set product_attr=:p1 where id in :p2 ";
			saleReportDao.updateBySql(attrUpdateSql,new Parameter(attr,id));
		}
		
		
		public Map<String,Map<String,Set<Integer>>> findMonthProfitAttr(){
			Map<String,Map<String,Set<Integer>>>  map=Maps.newLinkedHashMap();
			String sql="SELECT distinct t.id,(CASE WHEN p.`is_sale`='4' THEN '淘汰' WHEN p.`is_new`='1' THEN '新品' ELSE p.`is_sale` END) attr,t.`month` "+
					" FROM amazoninfo_report_month_type t JOIN psi_product_in_stock p "+
					" ON t.`month`=DATE_FORMAT(p.`data_date`,'%Y%m') AND t.`country`=p.`country` AND p.`product_name`=t.`product_name` "+
					" WHERE t.`month`>='201701' and t.`month`<='201708' order by t.id desc";
			
			List<Object[]> list=saleReportDao.findBySql(sql);
			for (Object[] obj: list) {
				Integer id=Integer.parseInt(obj[0].toString());
				String attr=obj[1].toString();
				attr = DictUtils.getDictLabel(attr, "product_position", attr);
				//if(!"普通".equals(attr)){
					String date=obj[2].toString();
					Map<String,Set<Integer>> temp=map.get(date);
					if(temp==null){
						temp=Maps.newLinkedHashMap();
						map.put(date,temp);
					}
					Set<Integer> sets=temp.get(attr);
					if(sets==null){
						sets=Sets.newHashSet();
						temp.put(attr, sets);
					}
					sets.add(id);
				//}
			}
			return map;
		}
		
		
		
		
		@Transactional(readOnly = false)
		public void updateDate(String orderId){
			String sql="SELECT a.`amazon_order_id`,r.`purchase_date` FROM amazoninfo_order r JOIN "+
			  " amazoninfo_outbound_order  a ON r.`amazon_order_id`=a.`amazon_order_id` "+
			  " WHERE a.`amazon_order_id` in :p1 and a.`order_type`='Paypal_Refund' and r.`purchase_date`!=a.create_date ";
			List<Object[]> list=saleReportDao.findBySql(sql,new Parameter(orderId));
			String updateSql="update amazoninfo_outbound_order set create_date=:p1 where amazon_order_id=:p2";
			for (Object[] obj: list) {
				saleReportDao.updateBySql(updateSql,new Parameter(obj[1],obj[0]));
			}
		}
		
		
		public Map<String,Map<String,Integer>> findOrderNum(SaleReport saleReport){
			Date start = saleReport.getStart();
			Date end = saleReport.getEnd();
			String typeSql = "'%Y%m%d'";
			if("2".equals(saleReport.getSearchType())){
				if(start==null){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addWeeks(today, -19);
					end = today;
					saleReport.setStart(start);
					saleReport.setEnd(end);
				}else{
					Date end1 = DateUtils.addWeeks(start, 3);
					if(end.before(end1)){
						end = end1;
						saleReport.setEnd(end1);
					}
				}
				start = DateUtils.getMonday(start);
				end = DateUtils.getSunday(end);
				typeSql="'%x%v'";
			}else if("3".equals(saleReport.getSearchType())){
				if(start==null){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addMonths(today, -18);
					end = today;
					saleReport.setStart(start);
					saleReport.setEnd(end);
				}else{
					Date end1 = DateUtils.addMonths(start, 3);
					if(end.before(end1)){
						end = end1;
						saleReport.setEnd(end1);
					}
				}
				start = DateUtils.getFirstDayOfMonth(start);
				end = DateUtils.getLastDayOfMonth(end);
				typeSql="'%Y%m'";
			}else{
				if(start==null){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addMonths(today, -1);
					end = today;
					saleReport.setStart(start);
					saleReport.setEnd(end);
				}else{
					Date end1 = DateUtils.addDays(start, 3);
					if(end.before(end1)){
						end = end1;
						saleReport.setEnd(end1);
					}
				}
			}
			Map<String,Map<String,Integer>> map=Maps.newHashMap();
			String sql="SELECT REPLACE(REPLACE(REPLACE(r.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') AS country,DATE_FORMAT(r.`purchase_date`,"+typeSql+") dates,COUNT(*) "+
					" FROM amazoninfo_order r WHERE r.`purchase_date`>=:p1 and order_status!='Canceled' and r.`purchase_date`<=:p2   "+
					" GROUP BY r.`sales_channel`,dates";
			List<Object[]> list=saleReportDao.findBySql(sql,new Parameter(start,DateUtils.addDays(end,1)));
			for (Object[] obj: list) {
				Map<String,Integer> temp=map.get(obj[0].toString());
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(obj[0].toString(),temp);
				}
				String date = obj[1].toString(); 
				if("2".equals(saleReport.getSearchType())){
					Integer i = Integer.parseInt(date.substring(4));
					if(i==53){
						Integer year = Integer.parseInt(date.substring(0,4));
						date =  (year+1)+"01";
					}else if (date.contains("2016")){
						i = i+1;
						date = "2016"+(i<10?("0"+i):i);
					}
				}
				temp.put(date,obj[2]==null?0:Integer.parseInt(obj[2].toString()));
				
				
				Map<String,Integer> totalTemp=map.get("total");
				if(totalTemp==null){
					totalTemp=Maps.newHashMap();
					map.put("total",totalTemp);
				}
				Integer num=(totalTemp.get(date)==null?0:totalTemp.get(date));
				totalTemp.put(date,num+(obj[2]==null?0:Integer.parseInt(obj[2].toString())));
				
				
				if("de,fr,it,es,uk".contains(obj[0].toString())){
					totalTemp=map.get("eu");
					if(totalTemp==null){
						totalTemp=Maps.newHashMap();
						map.put("eu",totalTemp);
					}
					num=(totalTemp.get(date)==null?0:totalTemp.get(date));
					totalTemp.put(date,num+(obj[2]==null?0:Integer.parseInt(obj[2].toString())));
				}
				
				if("uk,ca,com".contains(obj[0].toString())||obj[0].toString().contains("com")){
					totalTemp=map.get("en");
					if(totalTemp==null){
						totalTemp=Maps.newHashMap();
						map.put("en",totalTemp);
					}
					num=(totalTemp.get(date)==null?0:totalTemp.get(date));
					totalTemp.put(date,num+(obj[2]==null?0:Integer.parseInt(obj[2].toString())));
				}
			}
			return map;
		}
		
}
