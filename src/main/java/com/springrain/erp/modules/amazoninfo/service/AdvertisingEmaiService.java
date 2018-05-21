package com.springrain.erp.modules.amazoninfo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bouncycastle.crypto.Mac;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.dao.AdvertisingDao;

/**
 * 广告预警Service
 * @author sally
 * date 2017-10-27
 */
@Component
@Transactional(readOnly = true)
public class AdvertisingEmaiService extends BaseService{

    @Autowired
    private AdvertisingDao advertisingDao;

    /**
     * 查出广告中所有国家
     * @return
     */
    public List<String> findCountry(){
        String findCountry = "SELECT DISTINCT(country),1 FROM  amazoninfo_aws_adversting WHERE data_date=DATE_SUB(CURDATE(),INTERVAL 1 DAY)"
                +" UNION SELECT  DISTINCT(country),1 FROM amazoninfo_advertising adv WHERE adv.data_date=(CAST((CAST(SYSDATE()AS DATE) - INTERVAL 1 DAY)AS DATETIME)) ";
         List<Object[]> countryList = advertisingDao.findBySql(findCountry);
         List<String> listCountry = new ArrayList<String>();
         for(Object[] countrys : countryList){
             listCountry.add(countrys[0].toString());
         }
         return listCountry;
    }
    /**
     * 查出所有产品最低价
     * @return
     */
    public Map<String,String> findMinPriceMap(){
        String allMinSku = " SELECT p.country,p.sku,s.product_name,s.`color`,MIN(p.prices) FROM "
                +" (SELECT *,(CASE is_fba WHEN '1' THEN sale_price  ELSE (CASE country WHEN 'de' THEN sale_price+3.99 WHEN 'jp' THEN sale_price+800 WHEN 'com' THEN sale_price+7 ELSE sale_price END) END) prices"
                +"  FROM amazoninfo_product2 WHERE active='1' AND sale_price IS NOT NULL ) p,"
                +"  psi_sku s WHERE s.sku=p.sku AND s.country=p.country AND p.`active`='1' AND s.`del_flag`='0' GROUP BY s.product_name,p.country,s.`color`";
        List<Object[]> allMinSkuList = advertisingDao.findBySql(allMinSku);
        Map<String,String> map = new HashMap<String, String>();//所有价格map
        for(Object[] all : allMinSkuList){
            if(all[4]!=null){
                String color = all[3]!=null?all[3].toString():"";
                map.put(all[2].toString()+"_"+all[0].toString()+"_"+color,all[4].toString());
            }
        }
        return map;
    }
    
    public List<String> findAllMinSku(String productName,String country,String color,String price){
        String sql = " SELECT DISTINCT(s.sku),1 FROM "
                +" (SELECT *,(CASE is_fba WHEN '1' THEN sale_price  ELSE (CASE country WHEN 'de' THEN sale_price+3.99 WHEN 'jp' THEN sale_price+800 WHEN 'com' THEN sale_price+7 ELSE sale_price END) END) prices"
                +" FROM amazoninfo_product2 WHERE active='1' AND sale_price IS NOT NULL ) p,"
                +" psi_sku s WHERE s.`sku`=p.`sku` AND s.`country`=p.country  AND"
                +" s.product_name=:p1 AND s.country=:p2 AND s.color=:p3   AND p.`prices`=:p4 ";
        List<Object[]> list = advertisingDao.findBySql(sql,new Parameter(productName,country,color,price));
        List<String> listAll = new ArrayList<String>();
        for(Object[] obj : list){
            listAll.add(obj[0].toString());
        }
        return listAll;
    }
    /**
     * 查出所有广告计划
     */
    public List<Object[]> findAllPlanAdv(){
        String sql = " SELECT p1.product_name,p1.color_code,p2.country_code "
                +" FROM psi_marketing_plan_item p1,psi_marketing_plan p2 WHERE p1.marketing_plan_id=p2.id  "
                +" AND (p2.end_week>=YEARWEEK(CURDATE()) OR p2.end_week='' OR p2.end_week IS NULL)  AND p2.type='1' AND p2.sta='3'  GROUP BY p1.product_name,p1.color_code,p2.country_code  ORDER BY p2.country_code ";
        List<Object[]> findBySql = advertisingDao.findBySql(sql);
        return findBySql;
    }
    
    /**
     * 查询该计划是否打了广告
     * @param country
     * @param productName
     * @param color
     * @return
     */
    public List<Object[]> findIsExistInAdv(String country,String productName,String color){
        String advFlag = " SELECT distinct(s.sku),1 FROM psi_sku s,(SELECT *,(CASE product_name WHEN LOCATE('_',product_name)=0 THEN SUBSTRING_INDEX(product_name, '_', 1) ELSE product_name END)  pro_name," 
                +" (CASE product_name WHEN LOCATE('_',product_name)=0 THEN SUBSTRING_INDEX(product_name, '_', -1) ELSE '' END) color"
                +" FROM  amazoninfo_aws_adversting WHERE data_date=DATE_SUB(CURDATE(),INTERVAL 1 DAY)  GROUP BY product_name,country) a2 WHERE s.del_flag='0' AND s.`product_name`=a2.pro_name AND s.`color`=a2.color AND s.`country`=a2.country AND s.country=:p1 AND s.product_name=:p2 AND s.color=:p3"
                +" UNION"
                +" SELECT  distinct(sku.sku),1 FROM psi_sku sku,amazoninfo_advertising adv WHERE sku.sku=adv.sku AND sku.del_flag='0' "
                +" AND sku.country=adv.country AND adv.data_date=DATE_SUB(CURDATE(),INTERVAL 1 DAY) AND sku.country=:p1 AND sku.product_name=:p2 AND sku.color=:p3 ";

        List<Object[]> advFlagList = advertisingDao.findBySql(advFlag,new Parameter(country,productName,color));
        return advFlagList;
    }
    
    /**
     * 查询所有产品
     */
    public Map<String,Set<String>> getAllProduct(){
        String advFlag = "   SELECT (CASE s.color WHEN '' THEN s.`product_name` ELSE CONCAT(s.product_name,'_',s.`color`) END) a,s.`country` FROM amazoninfo_product2 p,psi_sku s WHERE p.sku=s.`sku` AND p.country=s.`country` AND p.active='1' AND s.`del_flag`='0'  AND s.`product_name` <> 'Inateck other'";
        List<Object[]> advFlagList = advertisingDao.findBySql(advFlag);
        Map<String,Set<String>> map = Maps.newHashMap();
        for(Object[] obj : advFlagList){
            if(obj[0]!=null){
                String productName = obj[0].toString();
                String country = obj[1].toString();
                Set<String> list = map.get(productName);
                if(list == null){
                    list = Sets.newHashSet();
                }
                list.add(country);
                map.put(productName, list);
            }
        }
        return map;
    }
}

