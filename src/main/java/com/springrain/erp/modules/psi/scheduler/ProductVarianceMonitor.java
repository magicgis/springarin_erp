package com.springrain.erp.modules.psi.scheduler;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.entity.ProductSalesInfo;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.service.ProductSalesInfoService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.web.PsiInventoryController;

public class ProductVarianceMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ProductVarianceMonitor.class);
	
	@Autowired
	private PsiInventoryService psiInventoryService;
	
	@Autowired
	private PsiProductService psiProductService;
	
	@Autowired
	private ProductSalesInfoService salesInfoService;
	
	@Autowired
	private PsiProductAttributeService psiProductAttributeService;
	
	public void countVariance() {
		LOGGER.info("开始计算各国销售数据方差...");
		List<Object[]> list = psiInventoryService.getAllProductSalesInfo();
		Map<String,Map<String,Integer>> skyOrSea = psiProductAttributeService.findtransportType();
		
		List<PsiProduct> productList = psiProductService.find();
		Map<String, PsiProduct> productMap = Maps.newHashMap();
		for (PsiProduct psiProduct : productList) {
			productMap.put(psiProduct.getName(), psiProduct);
		}
		
		//名字 国家 日期 个数
		String key = "";
		String productName = ""; 
		String country = ""; 
		Map<Date,Integer> data = null;
		Map<Date,Integer> realData = null;
		List<ProductSalesInfo> rs = Lists.newArrayList();
		Date today = new Date();
		today.setHours(0);
		today.setMinutes(0);
		today.setSeconds(0);
		Map<String, Map<String, Float>>  forecastDatas = psiInventoryService.getForecastByMonthSalesData();
		for (Object[] objs : list) {
			String tempProductName = objs[0].toString(); 
			String tempCountry = objs[1].toString(); 
			tempCountry = tempCountry.substring(tempCountry.lastIndexOf(".")+1);
			String tempKey = tempProductName+"_"+tempCountry;
			Date date = (Date)objs[2];
			Integer sale = ((BigDecimal)objs[3]).intValue();
			Integer realSale = objs[4]==null?((BigDecimal)objs[3]).intValue():((BigDecimal)objs[4]).intValue();
			if(tempKey.equals(key)){
				data.put(date,sale);
				realData.put(date,realSale);
			}else{
				//完结前面那个
				if(StringUtils.isNotEmpty(key)){
					ProductSalesInfo prInfo = new ProductSalesInfo();
					prInfo.setProductName(productName);
					prInfo.setCountry(country);
					PsiProduct psiProduct = matchProduct(productMap,productName);
					if(psiProduct!=null){
						Integer tranType = 1;
						if (skyOrSea.get(productName)!=null && skyOrSea.get(productName).get(country)!=null) {
							tranType = skyOrSea.get(productName).get(country);
						}
						prInfo.setProduct(psiProduct, tranType);
						prInfo.setData(data,realData,today,forecastDatas.get(productName+"_"+country));
						rs.add(prInfo);	
					}
				}
				data = Maps.newLinkedHashMap();
				data.put(date,sale);
				realData = Maps.newLinkedHashMap();
				realData.put(date,realSale);
				key = tempKey;
				productName = tempProductName;
				country = tempCountry;
			}
		}
		if(StringUtils.isNotEmpty(key)){
			ProductSalesInfo prInfo = new ProductSalesInfo();
			prInfo.setProductName(productName);
			prInfo.setCountry(country);
			PsiProduct psiProduct = matchProduct(productMap,productName);
			if(psiProduct!=null){
				Integer tranType = 1;
				if (skyOrSea.get(productName)!=null && skyOrSea.get(productName).get(country)!=null) {
					tranType = skyOrSea.get(productName).get(country);
				}
				prInfo.setProduct(psiProduct, tranType);
				prInfo.setData(data,realData,today,forecastDatas.get(productName+"_"+country));
				rs.add(prInfo);	
			}
		}
		list = psiInventoryService.getAllProductSalesInfoByEuro();
		key = "";
		productName = ""; 
		//名字 日期 数量
		for (Object[] objs : list) {
			String tempProductName = objs[0].toString(); 
			String tempKey = tempProductName;
			Date date = (Date)objs[1];
			Integer sale = ((BigDecimal)objs[2]).intValue();
			Integer realSale = objs[3]==null?((BigDecimal)objs[2]).intValue():((BigDecimal)objs[3]).intValue();
			if(tempKey.equals(key)){
				data.put(date,sale);
				realData.put(date,realSale);
			}else{
				//完结前面那个
				if(StringUtils.isNotEmpty(key)){
					ProductSalesInfo prInfo = new ProductSalesInfo();
					prInfo.setProductName(productName);
					prInfo.setCountry("eu");
					PsiProduct psiProduct = matchProduct(productMap,productName);
					if(psiProduct!=null){
						Integer tranType = 1;
						if (skyOrSea.get(productName)!=null && skyOrSea.get(productName).get("eu")!=null) {
							tranType = skyOrSea.get(productName).get("eu");
						}
						prInfo.setProduct(psiProduct,tranType);
						prInfo.setData(data, realData,today,forecastDatas.get(productName+"_eu"));
						rs.add(prInfo);	
					}
				}
				data = Maps.newLinkedHashMap();
				data.put(date,sale);
				realData = Maps.newLinkedHashMap();
				realData.put(date,realSale);
				key = tempKey;
				productName = tempProductName;
			}
		}
		if(StringUtils.isNotEmpty(key)){
			ProductSalesInfo prInfo = new ProductSalesInfo();
			prInfo.setProductName(productName);
			prInfo.setCountry("eu");
			PsiProduct psiProduct = matchProduct(productMap,productName);
			if(psiProduct!=null){
				Integer tranType = 1;
				if (skyOrSea.get(productName)!=null && skyOrSea.get(productName).get("eu")!=null) {
					tranType = skyOrSea.get(productName).get("eu");
				}
				prInfo.setProduct(psiProduct,tranType);
				prInfo.setData(data,realData,today,forecastDatas.get(productName+"_eu"));
				rs.add(prInfo);	
			}
		}
		//欧洲UK以外国家
		list = psiInventoryService.getAllProductSalesInfoByEuroNoUk();
		key = "";
		productName = ""; 
		//名字 日期 数量
		for (Object[] objs : list) {
			String tempProductName = objs[0].toString(); 
			String tempKey = tempProductName;
			Date date = (Date)objs[1];
			Integer sale = ((BigDecimal)objs[2]).intValue();
			Integer realSale = ((BigDecimal)objs[3]).intValue();
			if(tempKey.equals(key)){
				data.put(date,sale);
				realData.put(date,realSale);
			}else{
				//完结前面那个
				if(StringUtils.isNotEmpty(key)){
					ProductSalesInfo prInfo = new ProductSalesInfo();
					prInfo.setProductName(productName);
					prInfo.setCountry("eunouk");
					PsiProduct psiProduct = matchProduct(productMap,productName);
					if(psiProduct!=null){
						Integer tranType = 1;
						if (skyOrSea.get(productName)!=null && skyOrSea.get(productName).get("eu")!=null) {
							tranType = skyOrSea.get(productName).get("eu");
						}
						prInfo.setProduct(psiProduct,tranType);
						prInfo.setData(data, realData,today,forecastDatas.get(productName+"_eunouk"));
						rs.add(prInfo);	
					}
				}
				data = Maps.newLinkedHashMap();
				data.put(date,sale);
				realData = Maps.newLinkedHashMap();
				realData.put(date,realSale);
				key = tempKey;
				productName = tempProductName;
			}
		}
		if(StringUtils.isNotEmpty(key)){
			ProductSalesInfo prInfo = new ProductSalesInfo();
			prInfo.setProductName(productName);
			prInfo.setCountry("eunouk");
			PsiProduct psiProduct = matchProduct(productMap,productName);
			if(psiProduct!=null){
				Integer tranType = 1;
				if (skyOrSea.get(productName)!=null && skyOrSea.get(productName).get("eu")!=null) {
					tranType = skyOrSea.get(productName).get("eu");
				}
				prInfo.setProduct(psiProduct,tranType);
				prInfo.setData(data,realData,today,forecastDatas.get(productName+"_eunouk"));
				rs.add(prInfo);	
			}
		}
		if(rs.size()>0){
			//标记正在更新方差数据,限制单品页面读取方差
			PsiInventoryController.fangChaFlag = true;
			try {
				salesInfoService.save(rs);
			} catch (Exception e) {
				LOGGER.error("更新方差数据失败", e);
			}
			PsiInventoryController.fangChaFlag = false;
		}
		LOGGER.info("保存各国方差结束!");
	}

	private PsiProduct matchProduct(Map<String, PsiProduct> productMap, String productName) {
		String temp = productName;
		if(productName.indexOf("_")>0){
			temp = productName.substring(0,productName.lastIndexOf("_"));
		}
		return productMap.get(temp);
	}
	
}
