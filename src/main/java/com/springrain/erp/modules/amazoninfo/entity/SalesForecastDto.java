package com.springrain.erp.modules.amazoninfo.entity;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public class SalesForecastDto {

	private String  productName;

	private Map<String,Map<String, Integer>> realData = Maps.newHashMap();//国家 [周 数据]
	
	private Map<String,Map<String, SalesForecast>> data = Maps.newLinkedHashMap();//国家 [周 数据]
	
	private Map<String,Map<String, Integer>> forecastData = Maps.newHashMap();//国家 [周 数据]

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Map<String, Map<String, Integer>> getRealData() {
		return realData;
	}

	public void setRealData(Map<String, Map<String, Integer>> realData) {
		this.realData = realData;
	}

	public Map<String, Map<String, SalesForecast>> getData() {
		return data;
	}

	public void setData(Map<String, Map<String, SalesForecast>> data) {
		this.data = data;
	}

	public SalesForecastDto() {
		super();
	}

	public SalesForecastDto(String productName) {
		super();
		this.productName = productName;
	}
	
	public void initForecastData(Map<String,Map<String, Integer>> tranTime,MapVo inventorys,List<String> dates){
		for (String country : data.keySet()) {
			if("fr,de,ebay,it,es".contains(country)){
				continue;
			}
			Map<String, SalesForecast> forecastByCountry = data.get(country);
			int i = 0;
			Map<String, Integer> rs = Maps.newHashMap();
			for (String date : dates.subList(8,dates.size()-1)) {
				Integer inventory = 0;
				if(inventorys.getInventorys().get(productName)!=null){
					inventory = inventorys.getInventorys().get(productName).get(country);
					inventory = (inventory==null?0:inventory);
				}
				int index = tranTime.get(productName).get(country)+i;
				int j = 1;
				int count = 0;
				for (String date1 : dates.subList(8,dates.size()-1)) {
					if(forecastByCountry.get(date1)!=null){
						count +=(forecastByCountry.get(date1).getQuantityForecast()==null?0:forecastByCountry.get(date1).getQuantityForecast());
					}
					j++;
					if(j>index){
						break;
					}
				}
				rs.put(date, inventory-count);
				i++;
				if(i==8){
					break;
				}
			}
			forecastData.put(country, rs);
		}
	}

	public Map<String, Map<String, Integer>> getForecastData() {
		return forecastData;
	}
}
