package com.springrain.erp.modules.psi.scheduler;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductAttribute;
import com.springrain.erp.modules.psi.service.FbaInboundService;
import com.springrain.erp.modules.psi.service.PsiMarketingPlanService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductService;

@Component
public class PurchaseMaxStockMonitor {
	
	private final static Logger logger = LoggerFactory.getLogger(PurchaseMaxStockMonitor.class);
	@Autowired
	private PsiProductService           productService;
	@Autowired
	private PsiProductAttributeService  psiProductAttributeService;
	@Autowired
	private FbaInboundService           inboundService;
	@Autowired
	private PsiMarketingPlanService     marketPlanService;
	private static Map<String,Float> inventoryRateMap = Maps.newHashMap();
	static{
		inventoryRateMap.put("01", 0.93f);
		inventoryRateMap.put("02", 0.99f);
		inventoryRateMap.put("03", 0.85f);
		inventoryRateMap.put("04", 0.79f);
		inventoryRateMap.put("05", 0.76f);
		inventoryRateMap.put("06", 0.8f);
		inventoryRateMap.put("07", 1.37f);
		inventoryRateMap.put("08", 1.17f);
		inventoryRateMap.put("09", 1.07f);
		inventoryRateMap.put("10", 1.01f);
		inventoryRateMap.put("11", 1.07f);
		inventoryRateMap.put("12", 1.19f);
	}
	
	public void exeMaxMonitor() throws IOException, ParseException{
			logger.info("每周四自动生成fba贴 start");
			try{
				inboundService.genFbaInbound();
			}catch(Exception ex){
				logger.error("每周四自动生成fba贴异常：", ex);
			}
			logger.info("每周四自动生成fba贴  end");
			
			logger.info("采购最大下单量监控 start");
			try{
			 	this.updateMaxInventory();
			}catch(Exception ex){
				logger.error("采购最大下单量监控异常：", ex);
			}
			logger.info("采购最大下单量监控 end");
			
			/*营销计划改动后不需要预警
			 new Thread(){
				public void run(){
					logger.info("营销计划预警 start");
					marketPlanService.exeWarn();
					logger.info("营销计划预警 end");
				}
			}.start();*/
			
		
	}
	
	private void updateMaxInventory(){
		try{
		//获取产品的上架时间
		Map<String,Date> addDateMap= this.productService.getMasterProductAddedTime();
		List<String> noSaleOrNewPros = this.productService.getNoSaleAndNewPros();   
		Map<String,Set<String>> resMap=this.productService.get90_180Sales(noSaleOrNewPros);
		
		Date date = new Date();
		String month = DateUtils.getMonth();
		Float rate =inventoryRateMap.get(month);
		if(resMap!=null&&resMap.size()>0){
			for(Map.Entry<String,Set<String>> resEntry:resMap.entrySet()){
				String proName = resEntry.getKey();
				try{
					Set<String> colorSet = resEntry.getValue();
					for(String colorInfo:colorSet){
						String[] keys = colorInfo.split(","); 
						String  color=keys[0];
						String productNameColor=proName;  
						if(StringUtils.isNotEmpty(color)){
							productNameColor+="_"+color;
						}
						if(addDateMap.get(productNameColor)!=null){
							long spaceDay=DateUtils.spaceDays(addDateMap.get(productNameColor), date);
							Integer maxQuantity=0;
							//上架日期大于一年的,取最近半年的月平均销量
							if(spaceDay>360){
								maxQuantity=(int) ((Integer.parseInt(keys[2])/6)*5*rate);
							//上架日期大于半年的，取最近3个月的月平均
							}else if(spaceDay>180){
								maxQuantity=(int) ((Integer.parseInt(keys[1])/3)*5*rate);
							}else{
								continue;
							}
							if(maxQuantity>0){
								this.saveMaxInventory(proName, color, maxQuantity);
							}
						}
					}
				}catch(Exception ex){
					logger.error("采购最大下单量监控2："+ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
		logger.info("采购最大下单量监控 end");
	}catch(Exception ex){
		logger.error("采购最大下单量监控："+ex.getMessage());
		ex.printStackTrace();
	}
	}
	
	private void saveMaxInventory(String proName,String color,Integer maxQuantity){
		try{
			PsiProductAttribute maxInventory= this.psiProductAttributeService.get(proName, color);
			if(maxInventory==null){
				maxInventory=new PsiProductAttribute(proName, color, maxQuantity, new Date());
				PsiProduct product = productService.findProductByProductName(proName);
				if (product != null) {
					maxInventory.setProduct(product);
					maxInventory.setDelFlag("0");
				}
			}else{
				maxInventory.setQuantity(maxQuantity);
				maxInventory.setCreateDate(new Date());
			}
			if("0".equals(maxInventory.getDelFlag())){
				psiProductAttributeService.save(maxInventory);
			}
		}catch(Exception ex){
			logger.error("采购最大下单量监控1："+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		PurchaseMaxStockMonitor monitor = applicationContext.getBean(PurchaseMaxStockMonitor.class);
		monitor.exeMaxMonitor();
		applicationContext.close();
	}
	
	
}
