package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.modules.psi.scheduler.PsiConfig;

@Entity
@Table(name = "psi_product_variance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProductSalesInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id; 	

	private PsiProduct product;
	
	private String productName;
	
	private String country;
	
	private Integer day31Sales;
	
	private Integer realDay31Sales;
	
	private Integer dayPeriodSales;
	
	private Integer realDayPeriodSales;
	
	private Double variance;
	
	private String samplingData;
	
	private Double periodSqrt;
	
	private Integer period;
	
	private Double forecastPreiodAvg;
	
	private Double forecastAfterPreiodSalesByMonth;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Transient
	public PsiProduct getProduct() {
		return product;
	}
	
	@Column(name="real_day31_sales")
	public Integer getRealDay31Sales() {
		return realDay31Sales;
	}

	public void setRealDay31Sales(Integer realDay31Sales) {
		this.realDay31Sales = realDay31Sales;
	}

	public Integer getRealDayPeriodSales() {
		return realDayPeriodSales;
	}

	public void setRealDayPeriodSales(Integer realDayPeriodSales) {
		this.realDayPeriodSales = realDayPeriodSales;
	}

	public void setProduct(PsiProduct product,Integer skyOrSea) {
		this.product = product;
		int produce = product.getProducePeriod()==null?0:product.getProducePeriod();
		String temp = country;
		if("eu".contains(temp) || "eunouk".equals(temp)){
			temp = "de";
		}
		if ("com1".equals(temp)) {
			temp = "com";
		}
		PsiConfig config =  PsiConfig.get(temp);
		int day = 0 ;
		if(skyOrSea==null||1==skyOrSea){
			day = config.getTransportBySea();
		}else{
			day = config.getTransportBySky();
		}
		this.period = produce +day;
		//获取周期预测数据
	}

	public void setData(Map<Date,Integer> data,Map<Date,Integer> realData,Date today,Map<String,Float> forecastData) {
		List<Integer> dayPeriod = Lists.newArrayList();
		int i = 0 ;
		int period = getPeriod(); 
		i = period;
		int j = i;
		while(i>0){
			if(j-i<=period){
				dayPeriod.add(0);
			}
			i--;
		}
		Date today_1 = null;
		int hour = new Date().getHours();
		if(hour>7){
			if("de,fr,it,es,uk,jp,eu,eunouk".contains(country)){
				today_1 = DateUtils.addHours(today, -1);
			}else{
				if(hour>=13){
					today_1 = DateUtils.addHours(today, -1);
				}else{
					today_1 = DateUtils.addHours(today, -2);
				}
			}
		}else{
			today_1 = DateUtils.addHours(today, -2);
		}
		
		if("de,fr,it,es,uk,jp,eu,eunouk".contains(country)){
			today_1 = DateUtils.addHours(today, -1);
		}
		this.day31Sales = 0;
		this.dayPeriodSales = 0;
		this.realDay31Sales = 0;
		this.realDayPeriodSales = 0;
		int iii = 0;
		for(Map.Entry<Date,Integer> entry:data.entrySet()){
			Date date =entry.getKey();
			if(date.equals(today)){
				continue;
			}
			int sale = data.get(date);
			int realSale = realData.get(date);
			if(date.after(today_1)){
				continue;
			}
			//排除12月的样本数据
			/*if(date.getMonth()==11){
				iii++;
				continue;
			}*/
			int nDay = (int) ((today_1.getTime() - DateUtils.addDays(date,iii).getTime()) / (24 * 60 * 60 * 1000));
			if(nDay>=31&&nDay>=period){
				break;
			}
			if(nDay<31){
				this.day31Sales =this.day31Sales +sale;
				this.realDay31Sales =this.realDay31Sales +realSale;
			}
			if(nDay<period){
				//方差取样
				dayPeriod.set(nDay,sale);
				this.dayPeriodSales =this.dayPeriodSales+sale;
				this.realDayPeriodSales =this.realDayPeriodSales+realSale;
			}
		}
		//去噪点，算方差
		int avgPeriodSale = Math.round(((float)this.dayPeriodSales/period))*5;
		if(avgPeriodSale==0){
			avgPeriodSale = 5;
		}
		List<Double> sales = Lists.newArrayList();
		for (Integer day : dayPeriod) {
			if(day<=avgPeriodSale||day<=10){
				sales.add((double)day);
			}
		}
		this.samplingData = sales.toString().replace(".0","").replace(" ","");
		this.variance = MathUtils.getStandardDiviation(sales.toArray(new Double[0]));
		this.periodSqrt = Math.sqrt(period);
		
		if(forecastData!=null){
			this.forecastPreiodAvg = 0d;
			this.forecastAfterPreiodSalesByMonth = 0d;
			for (int k = 1; k <= this.period+31; k++) {
				//按周
				//Float temp =  forecastData.get(DateUtils.getDate(DateUtils.getMonday(DateUtils.addDays(today_1,k)),"yyyyMMdd"));
				//按月
				Float temp =  forecastData.get(DateUtils.getDate(DateUtils.getLastDayOfMonth(DateUtils.addDays(today_1,k)),"yyyyMMdd"));
				int ii = k-30;
				while(temp==null){
					Date tempDate = DateUtils.addDays(today_1,ii);
					if(tempDate.before(DateUtils.getFirstDayOfMonth(today))){
						break;
					}
					temp =  forecastData.get(DateUtils.getDate(DateUtils.getLastDayOfMonth(tempDate),"yyyyMMdd"));
					ii=ii-30;
				}
				temp = (temp==null?0f:temp);
				if(k<=this.period){
					forecastPreiodAvg +=temp;
				}else{
					forecastAfterPreiodSalesByMonth += temp;
				}
			}
			forecastPreiodAvg = forecastPreiodAvg/this.period;
			BigDecimal tempd = new BigDecimal(forecastPreiodAvg);
			tempd = tempd.setScale(2, BigDecimal.ROUND_HALF_UP);
			forecastPreiodAvg = tempd.doubleValue();
		}
	}

	@Column(name="day31_sales")
	public Integer getDay31Sales() {
		return day31Sales;
	}

	public void setDay31Sales(Integer day31Sales) {
		this.day31Sales = day31Sales;
	}

	public Integer getDayPeriodSales() {
		return dayPeriodSales;
	}

	public void setDayPeriodSales(Integer dayPeriodSales) {
		this.dayPeriodSales = dayPeriodSales;
	}

	public Double getVariance() {
		return variance;
	}

	public void setVariance(Double variance) {
		this.variance = variance;
	}

	public Double getPeriodSqrt() {
		return periodSqrt;
	}

	public void setPeriodSqrt(Double periodSqrt) {
		this.periodSqrt = periodSqrt;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public String getSamplingData() {
		return samplingData;
	}

	public void setSamplingData(String samplingData) {
		this.samplingData = samplingData;
	}


	public Double getForecastPreiodAvg() {
		return forecastPreiodAvg;
	}

	public void setForecastPreiodAvg(Double forecastPreiodAvg) {
		this.forecastPreiodAvg = forecastPreiodAvg;
	}

	public Double getForecastAfterPreiodSalesByMonth() {
		return forecastAfterPreiodSalesByMonth;
	}

	public void setForecastAfterPreiodSalesByMonth(
			Double forecastAfterPreiodSalesByMonth) {
		this.forecastAfterPreiodSalesByMonth = forecastAfterPreiodSalesByMonth;
	}

}
