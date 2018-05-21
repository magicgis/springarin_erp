package com.springrain.erp.modules.amazoninfo.entity;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

/**
 * 广告报表Entity
 * 
 * @author Tim
 * @version 2015-03-03
 */
public class AdvertisingOtherAsinDto {

	private String key;

	private Integer weekOtherSkuUnitsOrdered;
	private Float weekOtherSkuUnitsSales;
	private Float weekOtherSkuUnitsLirun;

	private Integer weekParentSkuUnitsOrdered;
	private Float weekParentSkuUnitsSales;
	private Float weekParentSkuUnitsLirun;
	
	private List<OtherAsin> otherAsins = Lists.newArrayList();
	
	public void addOtherAsin(OtherAsin otherAsin) {
		otherAsins.add(otherAsin);
	}
	
	public void initData(Map<String,String> relationship,Map<String, Float> price, float vat, String country, Map<String, Integer> commissionMap, Map<String, String> asinNameMap){
		//初始化数据
		this.weekOtherSkuUnitsOrdered = 0;
		this.weekOtherSkuUnitsSales = 0f;
		this.weekOtherSkuUnitsLirun = 0f;
		this.weekParentSkuUnitsOrdered = 0;
		this.weekParentSkuUnitsSales = 0f;
		this.weekParentSkuUnitsLirun = 0f;
		for (OtherAsin otherAsin : otherAsins) {
			Float cb = price.get(otherAsin.getAsin());
			if(cb!=null){
				this.weekOtherSkuUnitsOrdered +=otherAsin.getUnitsOrdered();
				this.weekOtherSkuUnitsSales += otherAsin.getSales();
				// 利润去除增值税和亚马逊佣金
				int commission = 0;	//佣金比
				try {
					commission = commissionMap.get(asinNameMap.get(otherAsin.getAsin()) + "_" + key);
				} catch (NullPointerException e) {}
				Float salePrice = otherAsin.getSales();
				Float cbPrice = otherAsin.getUnitsOrdered()*cb;
				float lirun = (salePrice-cbPrice)/(1+vat)-(salePrice-cbPrice)*commission/100f;
				this.weekOtherSkuUnitsLirun += lirun;
				String asins = relationship.get(otherAsin.getMyAsin());
				if(asins!=null){
					if(asins.contains(otherAsin.getAsin())){
						this.weekParentSkuUnitsOrdered +=otherAsin.getUnitsOrdered();
						this.weekParentSkuUnitsSales += otherAsin.getSales();
						this.weekParentSkuUnitsLirun += lirun;
					}
				}
			}
		}
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getWeekOtherSkuUnitsOrdered() {
		return weekOtherSkuUnitsOrdered;
	}

	public void setWeekOtherSkuUnitsOrdered(Integer weekOtherSkuUnitsOrdered) {
		this.weekOtherSkuUnitsOrdered = weekOtherSkuUnitsOrdered;
	}

	public Float getWeekOtherSkuUnitsSales() {
		return weekOtherSkuUnitsSales;
	}

	public void setWeekOtherSkuUnitsSales(Float weekOtherSkuUnitsSales) {
		this.weekOtherSkuUnitsSales = weekOtherSkuUnitsSales;
	}

	public Float getWeekOtherSkuUnitsLirun() {
		return weekOtherSkuUnitsLirun;
	}

	public void setWeekOtherSkuUnitsLirun(Float weekOtherSkuUnitsLirun) {
		this.weekOtherSkuUnitsLirun = weekOtherSkuUnitsLirun;
	}

	public Integer getWeekParentSkuUnitsOrdered() {
		return weekParentSkuUnitsOrdered;
	}

	public void setWeekParentSkuUnitsOrdered(Integer weekParentSkuUnitsOrdered) {
		this.weekParentSkuUnitsOrdered = weekParentSkuUnitsOrdered;
	}

	public Float getWeekParentSkuUnitsSales() {
		return weekParentSkuUnitsSales;
	}

	public void setWeekParentSkuUnitsSales(Float weekParentSkuUnitsSales) {
		this.weekParentSkuUnitsSales = weekParentSkuUnitsSales;
	}

	public Float getWeekParentSkuUnitsLirun() {
		return weekParentSkuUnitsLirun;
	}

	public void setWeekParentSkuUnitsLirun(Float weekParentSkuUnitsLirun) {
		this.weekParentSkuUnitsLirun = weekParentSkuUnitsLirun;
	}

    public class OtherAsin {

		private String myAsin;

		private String asin;

		private Integer unitsOrdered;

		private Float sales;

		public String getMyAsin() {
			return myAsin;
		}

		public void setMyAsin(String myAsin) {
			this.myAsin = myAsin;
		}

		public String getAsin() {
			return asin;
		}

		public void setAsin(String asin) {
			this.asin = asin;
		}

		public Integer getUnitsOrdered() {
			return unitsOrdered;
		}

		public void setUnitsOrdered(Integer unitsOrdered) {
			this.unitsOrdered = unitsOrdered;
		}

		public Float getSales() {
			return sales;
		}

		public void setSales(Float sales) {
			this.sales = sales;
		}

		public OtherAsin(String myAsin, String asin, Integer unitsOrdered,
				Float sales) {
			super();
			this.myAsin = myAsin;
			this.asin = asin;
			this.unitsOrdered = unitsOrdered;
			this.sales = sales;
		}

	}
}
