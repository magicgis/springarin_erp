package com.springrain.erp.modules.amazoninfo.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.entity.SalesForecastByMonth;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.amazoninfo.service.SalesForecastServiceByMonth;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.entity.User;

public class SaleFroecastrByMonthMonitor {
	
	@Autowired
	private PsiProductService 		productService;
	@Autowired
	private SaleReportService 		saleReportService;
	@Autowired
	private SalesForecastServiceByMonth salesForecastServiceByMonth;
	@Autowired
	private MailManager mailManager;
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	
	private  final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	public void forecast() {
		try {
			Date curMonth = DateUtils.getLastDayOfMonth(new Date());
			Map<String, Integer> pidMap = productService.getProductNameAndPidMap();
			List<SalesForecastByMonth> byMonths = Lists.newArrayList();
			Date today = new Date();
			LOGGER.info("开始预测销量");
			//需要预测的产品,按type分组
			Map<String,List<String>> productsTpye = saleReportService.findForForecast();
			
			Map<String, SalesForecastByMonth> map = salesForecastServiceByMonth.find("3");
			Map<String, String> fanOuMap = psiProductEliminateService.findProductFanOuFlag();
			//销量分月指数
			float[] totalIndex = saleReportService.findSalesIndex(null, null);

			Date firstDayOfMonth = DateUtils.getFirstDayOfMonth(today);
			for (Map.Entry<String,List<String>> entry : productsTpye.entrySet()) {
			    String type = entry.getKey();
				List<String> products = entry.getValue();
				if(products.size()>0){
					//分类型指数
					float[] typeIndex = saleReportService.findSalesIndex(type, null);
					//获取该类型所有非新品 非淘汰品
					for (String product : products) {
						//产品分月指数,销售期不足按类型指数算,类型指数不足按总量指数算
						float[] productIndex = saleReportService.findSalesIndex(null, product);
						if (productIndex == null || productIndex.length < 12) {
							productIndex = typeIndex;
						}
						if (productIndex == null || productIndex.length < 12) {
							productIndex = totalIndex;
						}
						//查询当月推算的销售额,按当月已售日均销推算月销量
						Integer sales =  saleReportService.getCurrMonthSaleByName(product, firstDayOfMonth);
						Map<String,Float> bini = saleReportService.getPcentByName(product, fanOuMap.get(product), firstDayOfMonth);
						int i = 0;
						for (float f : productIndex){
							float forecastSales = Math.round(sales * f);
							for (Map.Entry<String,Float> entryRs : bini.entrySet()) { 
							    String country = entryRs.getKey();
							    if ("com1".equals(country)) {
									continue;
								}
								String key = product;
								key +=","+country;
								key +=","+DateUtils.formatDate(DateUtils.addMonths(curMonth, i),"yyyyMM");
								SalesForecastByMonth forecast = map.get(key);
								if(forecast==null){
									forecast = new SalesForecastByMonth();
									forecast.setCountry(country);
									forecast.setType("3");
									forecast.setCreateBy(new User("1"));
									forecast.setDataDate(DateUtils.getLastDayOfMonth(DateUtils.addMonths(curMonth, i)));
									forecast.setProductName(product);
									forecast.setProductId(pidMap.get(product));
								}
								forecast.setLastUpdateDate(today);
								forecast.setQuantityForecast(Math.round(forecastSales*entryRs.getValue()));
								byMonths.add(forecast);
							}
							i++;
						}
					}
				}
			}
			if(byMonths.size()>0){
				salesForecastServiceByMonth.save(byMonths);
			}
			LOGGER.info("预测销量完毕");
		} catch (Exception e){
			LOGGER.error("Note:销量预测异常", e);
		}
	}
	
	public void setProductService(PsiProductService productService) {
		this.productService = productService;
	}

	public void setSaleReportService(SaleReportService saleReportService) {
		this.saleReportService = saleReportService;
	}

	public void setSalesForecastServiceByMonth(
			SalesForecastServiceByMonth salesForecastServiceByMonth) {
		this.salesForecastServiceByMonth = salesForecastServiceByMonth;
	}

	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		PsiProductService a = applicationContext.getBean(PsiProductService.class);
		
		SaleReportService aa = applicationContext.getBean(SaleReportService.class);
		
		SalesForecastServiceByMonth aaa = applicationContext.getBean(SalesForecastServiceByMonth.class);
		
		SaleFroecastrByMonthMonitor m = new SaleFroecastrByMonthMonitor();
		
		m.setProductService(a);
		m.setSaleReportService(aa);
		m.setSalesForecastServiceByMonth(aaa);
		
		applicationContext.close();
		
	}
	
	
	
	
	
	
	
	
	
	
}
