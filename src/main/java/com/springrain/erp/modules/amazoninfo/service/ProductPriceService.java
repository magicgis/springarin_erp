/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
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
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.ProductPriceDao;
import com.springrain.erp.modules.amazoninfo.entity.ProductPrice;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTieredPriceService;

/**
 * 产品价格管理Service
 * @author Tim
 * @version 2015-12-02
 */
@Component
@Transactional(readOnly = true)
public class ProductPriceService extends BaseService {

	@Autowired
	private ProductPriceDao productPriceDao;
	
	@Autowired
	private PsiProductService productService;
	
	@Autowired
	private PsiProductTieredPriceService psiProductTieredPriceService;
	
	public ProductPrice get(String id) {
		return productPriceDao.get(id);
	}
	
	
	@Transactional(readOnly = false)
	public void save(ProductPrice productPrice) {
		productPriceDao.save(productPrice);
	}
	
	@Transactional(readOnly = false)
	public void save(List<ProductPrice> productPrices) {
		for (ProductPrice productPrice2 : productPrices) {
			productPriceDao.save(productPrice2);
		}
	}
	
	
	@Transactional(readOnly = false)
	public void saveYestDay() {
		String sql = "INSERT INTO amazoninfo_product_price (country,DATE,sku,product_name,cost,fba,commission_pcent,tran_gw,tariff_pcent,TYPE,amz_price,amz_price_by_sky,amz_price_by_sea,local_price) SELECT country,CURDATE(),sku,product_name,cost,fba,commission_pcent,tran_gw,tariff_pcent,TYPE,amz_price,amz_price_by_sky,amz_price_by_sea,local_price FROM amazoninfo_product_price WHERE DATE= DATE_ADD(CURDATE(), INTERVAL -1 DAY)";
		productPriceDao.updateBySql(sql, null);
	}
	
	public ProductPrice find(ProductPrice productPrice) {
		DetachedCriteria dc = productPriceDao.createDetachedCriteria();
		dc.add(Restrictions.eq("sku",productPrice.getSku()));
		dc.add(Restrictions.eq("country",productPrice.getCountry()));
		dc.add(Restrictions.eq("date",productPrice.getDate()));
		List<ProductPrice> rs =  productPriceDao.find(dc);
		if(rs.size()>0){
			ProductPrice price =   rs.get(0);
			price.setAmzPrice(productPrice.getAmzPrice());
			price.setAmzPriceBySea(productPrice.getAmzPriceBySea());
			price.setAmzPriceBySky(productPrice.getAmzPriceBySky());
			price.setCommissionPcent(productPrice.getCommissionPcent());
			price.setCost(productPrice.getCost());
			price.setCrossPrice(productPrice.getCrossPrice());
			price.setFba(productPrice.getFba());
			price.setLocalPrice(productPrice.getLocalPrice());
			price.setTariffPcent(productPrice.getTariffPcent());
			price.setType(productPrice.getType());
			price.setTranGw(productPrice.getTranGw());
			
			return price;
		}else{
			return productPrice;
		}
	}
	
	
	public Map<String,List<ProductPrice>> findAllProducPrice(){
		Map<String,List<ProductPrice>> rs = Maps.newHashMap();
		String sql = "SELECT MAX(a.`date`) FROM amazoninfo_product_price a ";
		List<Object> list =  productPriceDao.findBySql(sql);
		String temp = "FORMAT(CASE WHEN a.`country` = 'com' THEN a.`amz_price` WHEN a.`country` IN ('de','fr','it','es') THEN a.`amz_price`*:p2 "+
				" WHEN a.`country` ='ca' THEN a.`amz_price`*:p3 WHEN a.`country` ='uk' THEN a.`amz_price`*:p4 ELSE a.`amz_price`*:p5 END,2)";
		if(list.size()>0){
			sql = "SELECT a.`country`,a.`product_name`,round(SUM(a.`cost`)/count(1),2) AS cost ," +
					"								   round(SUM(CASE WHEN a.`type` = 0 THEN a.`fba` ELSE 0 END)/SUM(CASE WHEN a.`type` = 0 THEN 1 ELSE 0 END),2) AS fbaFee," +
					"                                  round(SUM(a.`commission_pcent`)/count(1),0) AS commission," +
					"                                  round(SUM(a.`tariff_pcent`)/count(1),2) AS tariff," +
					"                                  round(SUM(CASE WHEN a.`type` = 0 THEN a.`amz_price` ELSE 0 END)/SUM(CASE WHEN a.`type` = 0 THEN 1 ELSE 0 END),2) AS amz_price," +
					"                                  round(SUM(CASE WHEN a.`type` = 0 THEN a.`amz_price_by_sky` ELSE 0 END)/SUM(CASE WHEN a.`type` = 0 THEN 1 ELSE 0 END),2) AS sky ," +
					"                                  round(SUM(CASE WHEN a.`type` = 0 THEN a.`amz_price_by_sea` ELSE 0 END)/SUM(CASE WHEN a.`type` = 0 THEN 1 ELSE 0 END),2) AS sea ," +
					"                                  round(SUM(CASE WHEN a.`type` = 2 THEN a.`local_price` ELSE 0 END)/SUM(CASE WHEN a.`type` = 2 THEN 1 ELSE 0 END),2) AS localPrice ," +
					"                                  GROUP_CONCAT( CASE WHEN a.`type` = 1 THEN CONCAT(a.`sku`,':',"+temp+",'<br/>') ELSE '' END) AS crossPrice  FROM amazoninfo_product_price a  WHERE a.`date` =:p1 GROUP BY a.`country`,a.`product_name` ORDER BY amz_price DESC ";
			List<Object[]> list1 =  productPriceDao.findBySql(sql,new Parameter(list.get(0),1/MathUtils.getRate("EUR", "USD", null),1/MathUtils.getRate("CAD", "USD", null),1/MathUtils.getRate("GBP", "USD", null),1/MathUtils.getRate("JPY", "USD", null)));
			for (Object[] objs : list1) {
				String country = objs[0].toString();
				//保本价按汇率换算成各国货币单位
				String cType = "USD";
				if ("de,fr,it,es".contains(country)) {
					cType = "EUR";
				} else if ("jp".equals(country)) {
					cType = "JPY";
				} else if ("ca".equals(country)) {
					cType = "CAD";
				} else if ("uk".equals(country)) {
					cType = "GBP";
				}
				//个货币单位兑美元汇率的倒数即美元兑指定货币单位汇率
				float rate = 1/MathUtils.getRate(cType, "USD", null);
				String productName = objs[1].toString();
				Float cost = Float.parseFloat(objs[2]==null?"0":objs[2].toString());
				Float fba = Float.parseFloat(objs[3]==null?"0":objs[3].toString());
				Integer commission = Math.round(Float.parseFloat(objs[4]==null?"0":objs[4].toString()));
				Float tariff = Float.parseFloat(objs[5]==null?"0":objs[5].toString());
				Float amzPrice = Float.parseFloat(objs[6]==null?"0":objs[6].toString()) * rate;	//亚马逊保本价
				Float skyPrice = Float.parseFloat(objs[7]==null?"0":objs[7].toString()) * rate;	//亚马逊空运保本价
				Float seaPrice = Float.parseFloat(objs[8]==null?"0":objs[8].toString()) * rate;	//亚马逊海运保本价
				Float localPrice = Float.parseFloat(objs[9]==null?"0":objs[9].toString()) * rate;	//本地帖保本价
				String crossPrice = objs[10].toString().replace(",,",",");	//欧洲共享保本价
				if(crossPrice.startsWith(",")){
					crossPrice = crossPrice.substring(1);
				}
				if (crossPrice.endsWith(",")){
					crossPrice = crossPrice.substring(0,crossPrice.length()-1);
				}
				List<ProductPrice> products = rs.get(country);
				if(products==null){
					products = Lists.newArrayList();
					rs.put(country, products);
				}
				ProductPrice product = new ProductPrice(productName, null, cost, fba, commission, null, tariff, null, country);
				product.setCrossPrice(crossPrice);
				product.setAmzPrice(amzPrice);
				product.setAmzPriceBySky(skyPrice);
				product.setAmzPriceBySea(seaPrice);
				product.setLocalPrice(localPrice);
				products.add(product);
			}
		}
		return 	rs;
	}
	
	public List<Object[]> findAllProducSalePrice(Set<String> products){
		if(products==null){
			String sql = "SELECT * FROM amazoninfo_product_sale_price";
			return  productPriceDao.findBySql(sql,null);
		}else if(products.size()==0){
			return null;
		}else{
			String sql = "SELECT * FROM amazoninfo_product_sale_price where product_name in (:p1)";
			return  productPriceDao.findBySql(sql,new Parameter(products));
		}
	}
	
	public Map<String,Float> findAllProducSalePrice(){
		Map<String,Map<String,Object>> purchasePriceMap=psiProductTieredPriceService.getMoqCNYPriceBaseMoqNoSupplier();//CNY
		Map<String,Float> map=Maps.newHashMap();
		String sql = "SELECT product_name,de_price,fr_price,it_price,es_price,uk_price,com_price,ca_price,jp_price,mx_price FROM amazoninfo_product_sale_price ";
		List<Object[]> list=productPriceDao.findBySql(sql,null);
		for (Object[] obj: list) {
			Float dePrice=0f;
			Float frPrice=0f;
			Float itPrice=0f;
			Float esPrice=0f;
			Float ukPrice=0f;
			Float comPrice=0f;
			Float caPrice=0f;
			Float jpPrice=0f;
			Float mxPrice=0f;
			String name=obj[0].toString();
			if(obj[1]!=null){
				dePrice=Float.parseFloat(obj[1].toString());
				map.put(name+"_de",dePrice);
			}else{
				if(purchasePriceMap.get(name)!=null&&purchasePriceMap.get(name).get("price")!=null&&((BigDecimal)purchasePriceMap.get(name).get("price")).floatValue()>0){
					dePrice=((BigDecimal)purchasePriceMap.get(name).get("price")).floatValue()*2;
					dePrice=dePrice/AmazonProduct2Service.getRateConfig().get("USD/CNY")/AmazonProduct2Service.getRateConfig().get("EUR/USD");
					map.put(name+"_de",dePrice);
				}
			}
			if(obj[2]!=null){
				frPrice=Float.parseFloat(obj[2].toString());
				map.put(name+"_fr",frPrice);
			}else{
				if(dePrice!=null&&dePrice>0){
					map.put(name+"_fr",dePrice);
				}
			}
			if(obj[3]!=null){
				itPrice=Float.parseFloat(obj[3].toString());
				map.put(name+"_it",itPrice);
			}else{
				if(dePrice!=null&&dePrice>0){
					map.put(name+"_it",dePrice);
				}
			}
			if(obj[4]!=null){
				esPrice=Float.parseFloat(obj[4].toString());
				map.put(name+"_es",esPrice);
			}else{
				if(dePrice!=null&&dePrice>0){
					map.put(name+"_es",dePrice);
				}
			}
			if(obj[5]!=null){
				ukPrice=Float.parseFloat(obj[5].toString());
				map.put(name+"_uk",ukPrice);
			}else{
				if(dePrice!=null&&dePrice>0){
					ukPrice=dePrice;//EUR
					ukPrice=ukPrice*AmazonProduct2Service.getRateConfig().get("EUR/USD")*AmazonProduct2Service.getRateConfig().get("USD/GBP");
					map.put(name+"_uk",ukPrice);
				}
			}
			if(obj[6]!=null){
				comPrice=Float.parseFloat(obj[6].toString());
				map.put(name+"_com",comPrice);
			}else{
				if(purchasePriceMap.get(name)!=null&&purchasePriceMap.get(name).get("price")!=null&&((BigDecimal)purchasePriceMap.get(name).get("price")).floatValue()>0){
					comPrice=((BigDecimal)purchasePriceMap.get(name).get("price")).floatValue()*2;
					comPrice=comPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY");
					map.put(name+"_com",comPrice);
				}
			}
			if(obj[7]!=null){
				caPrice=Float.parseFloat(obj[7].toString());
				map.put(name+"_ca",caPrice);
			}else{
				if(purchasePriceMap.get(name)!=null&&purchasePriceMap.get(name).get("price")!=null&&((BigDecimal)purchasePriceMap.get(name).get("price")).floatValue()>0){
					caPrice=((BigDecimal)purchasePriceMap.get(name).get("price")).floatValue()*2;
					caPrice=caPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY")*AmazonProduct2Service.getRateConfig().get("USD/CAD");
					map.put(name+"_ca",caPrice);
				}
			}
			if(obj[8]!=null){
				jpPrice=Float.parseFloat(obj[8].toString());
				map.put(name+"_jp",jpPrice);
			}else{
				if(purchasePriceMap.get(name)!=null&&purchasePriceMap.get(name).get("price")!=null&&((BigDecimal)purchasePriceMap.get(name).get("price")).floatValue()>0){
					jpPrice=((BigDecimal)purchasePriceMap.get(name).get("price")).floatValue()*2;
					jpPrice=jpPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY")*AmazonProduct2Service.getRateConfig().get("USD/JPY");
					map.put(name+"_jp",jpPrice);
				}
			}
			
			if(obj[9]!=null){
				mxPrice=Float.parseFloat(obj[9].toString());
				map.put(name+"_mx",mxPrice);
			}else{
				if(purchasePriceMap.get(name)!=null&&purchasePriceMap.get(name).get("price")!=null&&((BigDecimal)purchasePriceMap.get(name).get("price")).floatValue()>0){
					mxPrice=((BigDecimal)purchasePriceMap.get(name).get("price")).floatValue()*2;
					mxPrice=mxPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY")*AmazonProduct2Service.getRateConfig().get("USD/MXN");
					map.put(name+"_mx",jpPrice);
				}
			}
		}
		return map;
	}
	
	public Map<String,Float> findAllProducSalePrice(String currencyType){
		Map<String,Float> rs=Maps.newHashMap();
		String sql = "SELECT product_name,de_price*"+MathUtils.getRate("EUR", currencyType,null)+", "+
				     " fr_price*"+MathUtils.getRate("EUR", currencyType,null)+", "+
				     " it_price*"+MathUtils.getRate("EUR", currencyType,null)+", "+
				     " es_price*"+MathUtils.getRate("EUR", currencyType,null)+", "+
				     " uk_price*"+MathUtils.getRate("GBP", currencyType,null)+", "+
				     " com_price*"+MathUtils.getRate("USD", currencyType,null)+", "+
				     " ca_price*"+MathUtils.getRate("CAD", currencyType,null)+", "+
				     " jp_price*"+MathUtils.getRate("JPY", currencyType,null)+", "+
				     " mx_price*"+MathUtils.getRate("MXN", currencyType,null)+" "+
		             " FROM amazoninfo_product_sale_price ";
		List<Object[]> list=productPriceDao.findBySql(sql,null);
		for (Object[] obj: list) {
			String name=obj[0].toString();
			int euNum=0;
			int totalNum=0;
			int enNum=0;
			Float totalPrice=0f;
			Float euPrice=0f;
			Float enPrice=0f;
			Float dePrice=0f;
			Float frPrice=0f;
			Float itPrice=0f;
			Float esPrice=0f;
			Float ukPrice=0f;
			Float comPrice=0f;
			Float caPrice=0f;
			Float jpPrice=0f;
			Float mxPrice=0f;
			if(obj[1]!=null){
				dePrice=Float.parseFloat(obj[1].toString());
				totalPrice+=dePrice;
				euPrice+=dePrice;
				rs.put(name+"_de",dePrice);
				totalNum+=1;
				euNum+=1;
			}
			if(obj[2]!=null){
				frPrice=Float.parseFloat(obj[2].toString());
				totalPrice+=frPrice;
				euPrice+=frPrice;
				rs.put(name+"_fr",frPrice);
				totalNum+=1;
				euNum+=1;
			}
			if(obj[3]!=null){
				itPrice=Float.parseFloat(obj[3].toString());
				totalPrice+=itPrice;
				euPrice+=itPrice;
				rs.put(name+"_it",itPrice);
				totalNum+=1;
				euNum+=1;
			}
			if(obj[4]!=null){
				esPrice=Float.parseFloat(obj[4].toString());
				totalPrice+=esPrice;
				euPrice+=esPrice;
				rs.put(name+"_es",esPrice);
				totalNum+=1;
				euNum+=1;
			}
			if(obj[5]!=null){
				ukPrice=Float.parseFloat(obj[5].toString());
				totalPrice+=ukPrice;
				euPrice+=ukPrice;
				enPrice+=ukPrice;
				rs.put(name+"_uk",ukPrice);
				totalNum+=1;
				euNum+=1;
				enNum+=1;
			}
			if(obj[6]!=null){
				comPrice=Float.parseFloat(obj[6].toString());
				totalPrice+=comPrice;
				enPrice+=comPrice;
				rs.put(name+"_com",comPrice);
				totalNum+=1;
				enNum+=1;
			}
			if(obj[7]!=null){
				caPrice=Float.parseFloat(obj[7].toString());
				totalPrice+=caPrice;
				enPrice+=caPrice;
				rs.put(name+"_ca",caPrice);
				totalNum+=1;
				enNum+=1;
			}
			if(obj[8]!=null){
				jpPrice=Float.parseFloat(obj[8].toString());
				totalPrice+=jpPrice;
				rs.put(name+"_jp",jpPrice);
				totalNum+=1;
			}
			if(obj[9]!=null){
				mxPrice=Float.parseFloat(obj[9].toString());
				totalPrice+=mxPrice;
				rs.put(name+"_mx",mxPrice);
				totalNum+=1;
			}
			rs.put(name+"_total", totalPrice/totalNum);
			if(euNum>0){
				rs.put(name+"_eu", euPrice/euNum);
			}
			if(enNum>0){
				rs.put(name+"_en", enPrice/enNum);
			}
		}
		return rs;
	}
	
	public List<Object[]> getProductPrice(Date date){
		String sql="SELECT  p.`product_name`,p.sku,SUM(CASE WHEN p.country = 'de' THEN p.commission_pcent ELSE 0 END ) deCost, "+
				" SUM(CASE WHEN p.country = 'fr' THEN p.commission_pcent ELSE 0 END ) frCost, "+
				" SUM(CASE WHEN p.country = 'uk' THEN p.commission_pcent ELSE 0 END ) ukCost,  "+
				" SUM(CASE WHEN p.country = 'es' THEN p.commission_pcent ELSE 0 END ) esCost, "+
				" SUM(CASE WHEN p.country = 'it' THEN p.commission_pcent ELSE 0 END ) itCost, "+
				" SUM(CASE WHEN p.country = 'com' THEN p.commission_pcent ELSE 0 END ) comCost, "+
				" SUM(CASE WHEN p.country = 'ca' THEN p.commission_pcent ELSE 0 END ) caCost, "+
				" SUM(CASE WHEN p.country = 'jp' THEN p.commission_pcent ELSE 0 END ) jpCost, "+
				" TRUNCATE(SUM(CASE WHEN p.country = 'de' THEN p.`fba`*"+AmazonProduct2Service.getRateConfig().get("USD/EUR")+" ELSE 0 END ),2) deFba, "+
				" TRUNCATE(SUM(CASE WHEN p.country = 'fr' THEN p.`fba`*"+AmazonProduct2Service.getRateConfig().get("USD/EUR")+" ELSE 0 END ),2) frFba, "+
				" TRUNCATE(SUM(CASE WHEN p.country = 'uk' THEN p.`fba`*"+AmazonProduct2Service.getRateConfig().get("USD/GBP")+" ELSE 0 END ),2) ukFba, "+
				" TRUNCATE(SUM(CASE WHEN p.country = 'es' THEN p.`fba`*"+AmazonProduct2Service.getRateConfig().get("USD/EUR")+" ELSE 0 END ),2) esFba, "+
				" TRUNCATE(SUM(CASE WHEN p.country = 'it' THEN p.`fba`*"+AmazonProduct2Service.getRateConfig().get("USD/EUR")+" ELSE 0 END ),2) itFba, "+
				" TRUNCATE(SUM(CASE WHEN p.country = 'com' THEN p.`fba` ELSE 0 END ),2) comFba, "+
				" TRUNCATE(SUM(CASE WHEN p.country = 'ca' THEN p.`fba`*"+AmazonProduct2Service.getRateConfig().get("USD/CAD")+" ELSE 0 END ),2) caFba, "+
				" TRUNCATE(SUM(CASE WHEN p.country = 'jp' THEN p.`fba`*"+AmazonProduct2Service.getRateConfig().get("USD/JPY")+" ELSE 0 END ),2) jpFba,p.country, "+
				" SUM(CASE WHEN p.country = 'mx' THEN p.commission_pcent ELSE 0 END ) mxCost, "+
				" TRUNCATE(SUM(CASE WHEN p.country = 'mx' THEN p.`fba`*"+AmazonProduct2Service.getRateConfig().get("USD/MXN")+" ELSE 0 END ),2) mxFba "+
				" FROM amazoninfo_product_price p  WHERE p.date=:p1  AND TYPE IN ('0','1') "+
				" GROUP BY p.`product_name`,p.sku ";
		return  productPriceDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(date)));
	}
	

	public Map<String,Map<String,List<String>>> findAllProducCurrentSalePrice(){
		String sql = "SELECT "+
				 " c.name, "+
				 " GROUP_CONCAT( "+
				 "   DISTINCT  "+
				 "   CASE "+
				 "     WHEN c.country = 'de'  "+
				 "     THEN CONCAT(c.asin, ':',c.sku,':', c.price)  "+
				 "     ELSE NULL  "+
				 "   END  "+
				 "   ORDER BY c.price "+
				 " ), "+
				 " GROUP_CONCAT( "+
				 "    DISTINCT 	 "+
				 "   CASE "+
				 "     WHEN c.country = 'com'  "+
				 "     THEN CONCAT(c.asin, ':',c.sku,':', c.price)  "+
				 "     ELSE NULL  "+
				 "   END  "+
				 "   ORDER BY c.price "+
				 " ), "+
				 " GROUP_CONCAT( "+
				 "   DISTINCT  "+
				 "   CASE "+
				 "     WHEN c.country = 'uk'  "+
				 "     THEN CONCAT(c.asin, ':',c.sku,':', c.price)  "+
				 "     ELSE NULL  "+
				 "   END  "+
				 "   ORDER BY c.price "+
				 " ), "+
				 " GROUP_CONCAT( "+
				 "    DISTINCT 	 "+
				 "   CASE "+
				 "     WHEN c.country = 'fr'  "+
				 "    THEN CONCAT(c.asin, ':',c.sku,':', c.price)  "+
				 "     ELSE NULL  "+
				 "   END  "+
				 "   ORDER BY c.price "+
				 "  ), "+
				 " GROUP_CONCAT( "+
				 "    DISTINCT  "+
				 "   CASE "+
				 "     WHEN c.country = 'it'  "+
				 "     THEN CONCAT(c.asin, ':',c.sku,':', c.price)  "+
				 "     ELSE NULL  "+
				 "   END  "+
				 "   ORDER BY c.price "+
				 " ), "+
				 " GROUP_CONCAT( "+
				 "    DISTINCT  "+
				 "   CASE "+
				 "     WHEN c.country = 'es'  "+
				 "     THEN CONCAT(c.asin, ':',c.sku,':', c.price)  "+
				 "     ELSE NULL  "+
				 "   END  "+
				 "   ORDER BY c.price "+
				 " ), "+
				 " GROUP_CONCAT( "+
				 "    DISTINCT  "+
				 "   CASE "+
				 "     WHEN c.country = 'jp'  "+
				 "     THEN CONCAT(c.asin, ':',c.sku,':', c.price)  "+
				 "     ELSE NULL  "+
				 "   END  "+
				 "   ORDER BY c.price "+
				 " ), "+
				 " GROUP_CONCAT( "+
				 "    DISTINCT  "+
				 "   CASE "+
				 "     WHEN c.country = 'ca'  "+
				 "     THEN CONCAT(c.asin, ':',c.sku,':', c.price)  "+
				 "     ELSE NULL  "+
				 "   END  "+
				 "   ORDER BY c.price "+
				"  ),  "+
				 " GROUP_CONCAT( "+
				 "    DISTINCT  "+
				 "   CASE "+
				 "     WHEN c.country = 'mx'  "+
				 "     THEN CONCAT(c.asin, ':',c.sku,':', c.price)  "+
				 "     ELSE NULL  "+
				 "   END  "+
				 "   ORDER BY c.price "+
				"  )  "+
				" FROM "+
				"  (SELECT  "+
				"    bb.*, "+
				"    aa.price,  "+
				"    aa.asin "+
				"  FROM "+
				"    (SELECT  "+
				"      a.`sku`, "+
				"      a.asin, "+
				"      a.`country`, "+
				 "     a.`sale_price` AS price  "+
				 "   FROM "+
				"      amazoninfo_product2 a  "+
				"    WHERE a.`active` = '1' AND "+
				"    a.`is_fba` = '1'   "+
				 "     AND a.`sale_price` > 0) aa, "+
				 "   (SELECT  "+
				  "    CONCAT( "+
				  "      b.`product_name`, "+
				  "      CASE "+
				   "       WHEN b.`color` != '' "+ 
				   "       THEN '_'  "+
				   "       ELSE ''  "+
				   "     END, "+
				    "    b.`color` "+
				   "   ) AS NAME, "+
				  "    b.`sku`, "+
				  "    b.country  "+
				  "  FROM "+
				  "    psi_sku b  "+
				  "  WHERE b.`del_flag` = '0'  "+
				  "    AND b.`product_name` != 'Inateck other'  "+
				  "    AND b.`product_name` != 'Inateck Old') bb  "+
				"  WHERE aa.sku = bb.sku  "+
				"    AND aa.country = bb.country) c  "+
			"	GROUP BY c.name ";
		List<Object[]> list1 =  productPriceDao.findBySql(sql,null);
		Map<String,Map<String,List<String>>> rs = Maps.newHashMap();
		for (Object[] objects : list1) {
			String name = objects[0].toString();
			String de = objects[1]==null?"":objects[1].toString();
			String com =  objects[2]==null?"":objects[2].toString();
			String uk =  objects[3]==null?"":objects[3].toString();
			String fr =  objects[4]==null?"":objects[4].toString();
			String it =  objects[5]==null?"":objects[5].toString();
			String es =  objects[6]==null?"":objects[6].toString();
			String jp =  objects[7]==null?"":objects[7].toString();
			String ca =  objects[8]==null?"":objects[8].toString();
			String mx =  objects[9]==null?"":objects[9].toString();
			Map<String, List<String>> map = Maps.newHashMap();
			rs.put(name, map);
			if(StringUtils.isNotEmpty(de)){
				List<String> deList = Lists.newArrayList();
				String minPriceStr = de.split(",")[0];
				String asin = minPriceStr.split(":")[0];
				String sku = minPriceStr.split(":")[1];
				String minPrice = minPriceStr.split(":")[2];
				deList.add(minPrice);
				deList.add(sku);
				deList.add("http://www.amazon.de/dp/"+asin);
				map.put("de", deList);
			}
			if(StringUtils.isNotEmpty(com)){
				List<String> comList = Lists.newArrayList();
				String minPriceStr = com.split(",")[0];
				String asin = minPriceStr.split(":")[0];
				String sku = minPriceStr.split(":")[1];
				String minPrice = minPriceStr.split(":")[2];
				comList.add(minPrice);
				comList.add(sku);
				comList.add("http://www.amazon.com/dp/"+asin);
				map.put("com", comList);
			}
			if(StringUtils.isNotEmpty(uk)){
				List<String> ukList = Lists.newArrayList();
				String minPriceStr = uk.split(",")[0];
				String asin = minPriceStr.split(":")[0];
				String sku = minPriceStr.split(":")[1];
				String minPrice = minPriceStr.split(":")[2];
				ukList.add(minPrice);
				ukList.add(sku);
				ukList.add("http://www.amazon.co.uk/dp/"+asin);
				map.put("uk", ukList);
			}
			if(StringUtils.isNotEmpty(fr)){
				List<String> frList = Lists.newArrayList();
				String minPriceStr = fr.split(",")[0];
				String asin = minPriceStr.split(":")[0];
				String sku = minPriceStr.split(":")[1];
				String minPrice = minPriceStr.split(":")[2];
				frList.add(minPrice);
				frList.add(sku);
				frList.add("http://www.amazon.fr/dp/"+asin);
				map.put("fr", frList);
			}
			if(StringUtils.isNotEmpty(it)){
				List<String> itList = Lists.newArrayList();
				String minPriceStr = it.split(",")[0];
				String asin = minPriceStr.split(":")[0];
				String sku = minPriceStr.split(":")[1];
				String minPrice = minPriceStr.split(":")[2];
				itList.add(minPrice);
				itList.add(sku);
				itList.add("http://www.amazon.it/dp/"+asin);
				map.put("it", itList);
			}
			if(StringUtils.isNotEmpty(es)){
				List<String> esList = Lists.newArrayList();
				String minPriceStr = es.split(",")[0];
				String asin = minPriceStr.split(":")[0];
				String sku = minPriceStr.split(":")[1];
				String minPrice = minPriceStr.split(":")[2];
				esList.add(minPrice);
				esList.add(sku);
				esList.add("http://www.amazon.es/dp/"+asin);
				map.put("es", esList);
			}
			if(StringUtils.isNotEmpty(jp)){
				List<String> jpList = Lists.newArrayList();
				String minPriceStr = jp.split(",")[0];
				String asin = minPriceStr.split(":")[0];
				String sku = minPriceStr.split(":")[1];
				String minPrice = minPriceStr.split(":")[2];
				jpList.add(minPrice);
				jpList.add(sku);
				jpList.add("http://www.amazon.co.jp/dp/"+asin);
				map.put("jp", jpList);
			}
			if(StringUtils.isNotEmpty(ca)){
				List<String> caList = Lists.newArrayList();
				String minPriceStr = ca.split(",")[0];
				String asin = minPriceStr.split(":")[0];
				String sku = minPriceStr.split(":")[1];
				String minPrice = minPriceStr.split(":")[2];
				caList.add(minPrice);
				caList.add(sku);
				caList.add("http://www.amazon.ca/dp/"+asin);
				map.put("ca", caList);
			}
			if(StringUtils.isNotEmpty(mx)){
				List<String> mxList = Lists.newArrayList();
				String minPriceStr = mx.split(",")[0];
				String asin = minPriceStr.split(":")[0];
				String sku = minPriceStr.split(":")[1];
				String minPrice = minPriceStr.split(":")[2];
				mxList.add(minPrice);
				mxList.add(sku);
				mxList.add("http://www.amazon.com.mx/dp/"+asin);
				map.put("mx", mxList);
			}
		}
		return rs;
	}
	
	
	@Transactional(readOnly = false)
	public void updatePanEu(){
		// type为0 代表要么本国要么已经泛欧  type为1代表cross 2为本地贴
		String sql = "SELECT a.`product_name`,a.`country`,GROUP_CONCAT(a.`sku`) AS skustr FROM amazoninfo_product_price a WHERE a.`country` IN ('fr','it','es','uk') AND a.`type` = '1' AND a.`date` = CURDATE() GROUP BY  a.`product_name`,a.`country`  HAVING skuStr LIKE '%-DE%'";
		List<Object[]> list =  productPriceDao.findBySql(sql,null);
		sql = "SELECT a.`product_name`,a.`sku` FROM amazoninfo_pan_eu a WHERE a.`is_pan_eu` = '1'";
		List<Object[]> list1 =  productPriceDao.findBySql(sql,null);
		Map<String,String> panEu = Maps.newHashMap();
		for (Object[] objects : list1) {
			panEu.put(objects[0].toString(), objects[1].toString());
		}
		Map<String,String> powerAttr = productService.getHasPowerByName();
		
		
		sql = "UPDATE amazoninfo_product_price a SET a.`type` = '0' WHERE a.`country` = :p1 AND a.`sku` = :p2 AND a.`type` = '1' AND a.`date` = CURDATE()";
		for (Object[] objects : list) {
			String name = objects[0].toString();
			String country = objects[1].toString();
			String skuStr = objects[2].toString();
			String skuDe = panEu.get(name);
			if(skuDe!=null&&skuStr.contains(skuDe)){
				if("1".equals(powerAttr.get(name))&&"uk".equals(country)){
					continue;
				}
				productPriceDao.updateBySql(sql, new Parameter(country,skuDe));
			}
		}
	}

	public Float getPriceBySkuAndCountry(String country, String sku) {
		String sql = "SELECT MAX(a.`date`) FROM amazoninfo_product_price a ";
		List<Object> list =  productPriceDao.findBySql(sql);
		if(list.size()>0){
			sql = "SELECT a.`amz_price` FROM amazoninfo_product_price a  WHERE a.`date`=:p1 AND a.`country`=:p2 AND a.`sku`=:p3 ";
			List<Object> list1 =  productPriceDao.findBySql(sql,new Parameter(list.get(0), country, sku));
			if (list1.size() > 0) {
				return ((BigDecimal)list1.get(0)).floatValue();
			}
		}
		return null;
	}
	
	public String getPriceDetail(String country , String productName){
		String sql = "SELECT a.`fba`,a.`commission_pcent` FROM amazoninfo_product_price a WHERE a.`date` = DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND  (a.`type` = '0' OR a.`type` = '1')  AND a.`country` = :p2 AND a.`product_name` = :p1 ORDER BY a.type,a.`id` DESC LIMIT 1";
		List<Object[]> list =  productPriceDao.findBySql(sql,new Parameter(productName,country));
		if(list.size()>0){
			PsiProduct product = productService.findProductByName(productName);
			String rs = "";
			if(product!=null){
				String feeCountry = "fr,it,es,de,uk".contains(country)?"eu":country;
				
				Map<String, Float> rate = AmazonProduct2Service.getRateConfig();
				float sea = product.getTranGw()*ProductPrice.sea.get(feeCountry);
				float air = product.getAirGw()*ProductPrice.sky.get(feeCountry);
				float fee = Float.parseFloat(list.get(0)[0].toString());
				String suf = "";
				if("fr,it,es,de".contains(country)){
					sea = new BigDecimal(sea *(rate.get("JPY/EUR")/rate.get("JPY/CNY"))).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					air = new BigDecimal(air*(rate.get("JPY/EUR")/rate.get("JPY/CNY"))).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					fee = new BigDecimal(fee *rate.get("USD/EUR")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					suf = "€";
				}else if ("com".equals(country)){
					sea = new BigDecimal(sea /rate.get("USD/CNY")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					air = new BigDecimal(air/rate.get("USD/CNY")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					suf = "$";
				}else if ("ca".equals(country)){
					sea = new BigDecimal(sea *(rate.get("JPY/CAD")/rate.get("JPY/CNY"))).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					air = new BigDecimal(air*(rate.get("JPY/CAD")/rate.get("JPY/CNY"))).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					fee = new BigDecimal(fee *rate.get("USD/CAD")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();;
					suf = "CAD";
				}else if ("jp".equals(country)){
					sea = new BigDecimal(sea /rate.get("JPY/CNY")).setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();
					air = new BigDecimal(air/rate.get("JPY/CNY")).setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();
					fee = new BigDecimal(fee *rate.get("USD/JPY")).setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();
					suf = "￥";
				}else if ("uk".equals(country)){
					sea = new BigDecimal(sea *(rate.get("JPY/GBP")/rate.get("JPY/CNY"))).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					air = new BigDecimal(air*(rate.get("JPY/GBP")/rate.get("JPY/CNY"))).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					fee = new BigDecimal(fee *rate.get("USD/GBP")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					suf = "￡";
				}else if ("mx".equals(country)){
					sea = new BigDecimal(sea *(rate.get("USD/MXN")/rate.get("USD/CNY"))).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					air = new BigDecimal(air*(rate.get("USD/MXN")/rate.get("USD/CNY"))).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					fee = new BigDecimal(fee *rate.get("USD/MXN")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					suf = "MXN";
				}
				String pcent  = list.get(0)[1].toString()+"%";
				rs ="By Sea: "+sea+suf+"<br/>By Air:"+air+suf+"<br/>Fba Fee:"+fee+suf+"<br/>Commission:"+pcent;
				return rs;
			}
		}
		return "";
	}
	
	//分国家统计单品亚马逊佣金比[产品名称_国家   佣金比]
	public Map<String, Integer> findCommission(){
		Map<String, Integer> rs = Maps.newHashMap();
		String sql = "SELECT MAX(a.`date`) FROM amazoninfo_product_price a ";
		List<Object> list =  productPriceDao.findBySql(sql);
		if(list.size()>0){
			sql = "SELECT a.`country`,a.`product_name`,a.`commission_pcent`  FROM amazoninfo_product_price a  WHERE a.`date` =:p1 GROUP BY a.`country`,a.`product_name` ";
			List<Object[]> list1 =  productPriceDao.findBySql(sql,new Parameter(list.get(0)));
			for (Object[] objects : list1) {
				String country = objects[0].toString();
				String productName = objects[1].toString();
				Integer rate = Integer.parseInt(objects[2].toString());
				rs.put(productName + "_" + country, rate);
			}
		}
		return rs;
	}
	
	//获取指定产品指定国家的保本价
	public Float findPriceByCountryAndName(String country, String productName){
		if (StringUtils.isEmpty(country) || StringUtils.isEmpty(productName)) {
			return null;
		}
		if (country.startsWith("com")) {
			country = "com";
		}
		String columnName = country + "_price";
		String sql = "SELECT "+columnName+" FROM `amazoninfo_product_sale_price` WHERE product_name=:p1 LIMIT 1";
		List<Object> list =  productPriceDao.findBySql(sql, new Parameter(productName));
		if(list.size() > 0 && list.get(0) != null){
			Float price = Float.parseFloat(list.get(0).toString());
			return price;
		}
		return null;
	}
	
	
	/**
	 * 获得产品、国家的           保本价
	 */
	public Map<String,BigDecimal> getAmazonSafePrice(){
		Map<String,BigDecimal> rs = Maps.newHashMap();
		String sql="SELECT a.`product_name`,a.`de_price`,a.`uk_price`,a.`fr_price`,a.`it_price`,a.`es_price`,a.`jp_price`,a.`com_price`,a.`ca_price`,a.mx_price FROM amazoninfo_product_sale_price AS a ";
		List<Object[]> list =  productPriceDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				String productName = obj[0].toString();
				if(obj[1]!=null){
					rs.put(productName+"_de", (BigDecimal)obj[1]);
				}
				if(obj[2]!=null){
					rs.put(productName+"_uk", (BigDecimal)obj[2]);
				}
				if(obj[3]!=null){
					rs.put(productName+"_fr", (BigDecimal)obj[3]);
				}
				if(obj[4]!=null){
					rs.put(productName+"_it", (BigDecimal)obj[4]);
				}
				if(obj[5]!=null){
					rs.put(productName+"_es", (BigDecimal)obj[5]);
				}
				if(obj[6]!=null){
					rs.put(productName+"_jp", (BigDecimal)obj[6]);
				}
				if(obj[7]!=null){
					rs.put(productName+"_com", (BigDecimal)obj[7]);
				}
				if(obj[8]!=null){
					rs.put(productName+"_ca", (BigDecimal)obj[8]);
				}
				if(obj[9]!=null){
					rs.put(productName+"_mx", (BigDecimal)obj[9]);
				}
			}
		}
		return rs;
	}
	
	//获取指定SKU指定国家的最近一个月均价
	public Float findAvgPriceByCountryAndSku(String country, String sku){
		String sql = "SELECT AVG(t.`sale_price`) FROM `amazoninfo_product_history_price` t "+
				" WHERE t.`sku`=:p1 AND t.`data_date`>:p2 AND t.`country`=:p3 AND t.`sale_price`>0";
		List<Object> list =  productPriceDao.findBySql(sql, new Parameter(sku,DateUtils.addMonths(new Date(), -1), country));
		if(list.size() > 0 && list.get(0) != null){
			Float price = Float.parseFloat(list.get(0).toString());
			return price;
		}
		return null;
	}
}
