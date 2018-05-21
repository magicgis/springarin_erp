/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.collect.Maps;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.entity.PsiProductTransportRate;

/**
 * 产品价格管理Entity
 * @author Tim
 * @version 2015-12-02
 */
@Entity
@Table(name = "amazoninfo_product_price")
public class ProductPrice implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id; 		// 编号
	private String productName; 	// 名称
	private String sku;
	private Float cost;
	private Float fba;
	private Integer commissionPcent = 0;
	private Float tranGw;
	private Float tariffPcent ;
	private Date date;
	private String country;
	private String type = "0"; //0：正常;1:cross;2:本地
	
	//价格均为美金价格
	private Float amzPrice;
	private Float amzPriceBySky;
	private Float amzPriceBySea;
	private Float localPrice;
		
	public static Map<String, Float> sky ;
	public static Map<String, Float> sea ;
	public static Map<String, Float> express ;
	
	
	public static Map<String, Float> tranFee ;
	
	private String crossPrice;
	
	static{
		sky = Maps.newHashMap();
		sea = Maps.newHashMap();
		express = Maps.newHashMap();
		tranFee = Maps.newHashMap();
		
		sky.put("eu", 24f);
		sky.put("de", 21f);
		sky.put("fr", 24f);
		sky.put("it", 24f);
		sky.put("uk", 24f);
		sky.put("es", 25f);
		sky.put("ca", 33f);
		sky.put("jp", 20f);
		sky.put("com", 26f);
		sky.put("com1", 26f);
		sky.put("com2", 26f);
		sky.put("com3", 26f);
		sky.put("mx",30f);
		
		sea.put("eu", 5f);
		sea.put("de", 4f);
		sea.put("fr", 8f);
		sea.put("it", 8f);
		sea.put("uk", 8f);
		sea.put("es", 9f);
		sea.put("ca", 32f);
		sea.put("jp", 13f);
		sea.put("com", 5f);
		sea.put("com1", 5f);
		sea.put("com2", 5f);
		sea.put("com3", 5f);
		sea.put("mx", 30f);
		
		express.put("eu", 27f);
		express.put("de", 25f);
		express.put("fr", 29f);
		express.put("it", 29f);
		express.put("uk", 29f);
		express.put("es", 30f);
		express.put("ca", 28f);
		express.put("jp", 17f);
		express.put("com",28f);
		express.put("com1",28f);
		express.put("com2",28f);
		express.put("com3",28f);
		express.put("mx",30f);
		
		tranFee.put("de", 11f);
		tranFee.put("fr", 14f);
		tranFee.put("it", 14f);
		tranFee.put("uk", 14f);
		tranFee.put("es", 15f);
		tranFee.put("ca", 32f);
		tranFee.put("jp", 17f);
		tranFee.put("com", 17f);
		tranFee.put("com1", 17f);
		tranFee.put("com2", 17f);
		tranFee.put("com3", 17f);
		tranFee.put("mx",30f);
		
	}
	
	public ProductPrice() {
		super();
	}

	public ProductPrice(String productName, String sku, Float cost, Float fba,
			Integer commissionPcent, Float tranGw, Float tariffPcent,
			Date date, String country) {
		super();
		this.productName = productName;
		this.sku = sku;
		this.cost = cost;
		this.fba = fba;
		this.commissionPcent = commissionPcent;
		this.tranGw = tranGw;
		this.tariffPcent = tariffPcent;
		this.date = date;
		this.country = country;
	}


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Transient
	public String getCrossPrice() {
		return crossPrice;
	}

	public void setCrossPrice(String crossPrice) {
		this.crossPrice = crossPrice;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Float getCost() {
		return cost;
	}

	public void setCost(Float cost) {
		this.cost = cost;
	}

	public Float getFba() {
		return fba;
	}

	public void setFba(Float fba) {
		this.fba = fba;
	}

	public Integer getCommissionPcent() {
		return commissionPcent;
	}

	public void setCommissionPcent(Integer commissionPcent) {
		this.commissionPcent = commissionPcent;
	}

	public Float getTranGw() {
		return tranGw;
	}

	public void setTranGw(Float tranGw) {
		this.tranGw = tranGw;
	}

	public Float getTariffPcent() {
		return tariffPcent;
	}

	public void setTariffPcent(Float tariffPcent) {
		this.tariffPcent = tariffPcent;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Float getAmzPrice() {
		return amzPrice;
	}

	public void setAmzPrice(Float amzPrice) {
		this.amzPrice = amzPrice;
	}

	public Float getAmzPriceBySky() {
		return amzPriceBySky;
	}

	public void setAmzPriceBySky(Float amzPriceBySky) {
		this.amzPriceBySky = amzPriceBySky;
	}

	public Float getAmzPriceBySea() {
		return amzPriceBySea;
	}

	public void setAmzPriceBySea(Float amzPriceBySea) {
		this.amzPriceBySea = amzPriceBySea;
	}

	public Float getLocalPrice() {
		return localPrice;
	}

	public void setLocalPrice(Float localPrice) {
		this.localPrice = localPrice;
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Transient
    public void count(Float rmbToUsd,Map<String, PsiProductTransportRate> tranPrice,Map<String, PsiProductTransportRate> totalTranPrice){
		float vat = 0 ;
		String temp = country.toUpperCase();
		if("UK".equals(temp)){
			temp = "GB";
		}
		if("COM".equals(temp)){
			temp = "US";
		}
		CountryCode vatCode = CountryCode.valueOf(temp);
		if(vatCode!=null){
			vat = vatCode.getVat()/100f;
		}
		Float seaFee = 0f;
		Float skyFee = 0f;
		Float avgFee = 0f;
		String key = "";
		if("uk,it,es,fr,de".contains(country)){
			key = "EU";
		}else if("jp".equals(country)){
			key = "JP";
		}else{
			key = "US";
		}
		if(tranPrice!=null){
			//EU、JP、US
			if(tranPrice.get(key)!=null){
				skyFee = tranPrice.get(key).getAirPrice();
				if(!"ca".equals(country)){
					seaFee = tranPrice.get(key).getSeaPrice();
					avgFee = tranPrice.get(key).getAvgPrice();
				}else{
					seaFee = skyFee;
					avgFee = skyFee;
				}
				//当按产品单个计算出来的时候，价格是单个单品的价格，当产品没得时，按照近期运费重量价格，所以要判断乘以重量
				if(StringUtils.isEmpty(tranPrice.get(key).getProductName())){
					if(seaFee!=null){
						seaFee = seaFee*tranGw;
					}
					if(skyFee!=null){
						skyFee = skyFee*tranGw;
					}
					if(avgFee!=null){
						avgFee = avgFee*tranGw;
					}
				}
			}
		}
		
		if((seaFee==null||seaFee==0f)&&totalTranPrice!=null&&totalTranPrice.get(key)!=null&&totalTranPrice.get(key).getSeaPrice()!=null){
			seaFee = totalTranPrice.get(key).getSeaPrice()*tranGw;
		}
		
		if((skyFee==null||skyFee==0f)&&totalTranPrice!=null&&totalTranPrice.get(key)!=null&&totalTranPrice.get(key).getAirPrice()!=null){
			skyFee = totalTranPrice.get(key).getAirPrice()*tranGw;
		}
		
		if((avgFee==null||avgFee==0f)&&totalTranPrice!=null&&totalTranPrice.get(key)!=null&&totalTranPrice.get(key).getAvgPrice()!=null){
			avgFee = totalTranPrice.get(key).getAvgPrice()*tranGw;
		}
		
		
		if(seaFee==null||seaFee==0f){
			seaFee = sea.get(country)*tranGw;
		}
		if(skyFee==null||skyFee==0f){
			skyFee = sky.get(country)*tranGw;
		}
		if(avgFee==null||avgFee==0f){
			avgFee = tranFee.get(country)*tranGw;
		}
    	this.localPrice = (cost.floatValue()*(1+(tariffPcent==null?0:tariffPcent.floatValue()/100))+rmbToUsd*avgFee)*(1+vat);
    	this.amzPrice = (cost.floatValue()*(1+(tariffPcent==null?0:tariffPcent.floatValue()/100))+rmbToUsd*avgFee+fba)/((1f/(1+vat))-(commissionPcent/100f));
    	
    	this.amzPriceBySky = (cost.floatValue()*(1+(tariffPcent==null?0:tariffPcent.floatValue()/100))+rmbToUsd*skyFee+fba)/((1f/(1+vat))-(commissionPcent/100f));
    	this.amzPriceBySea = (cost.floatValue()*(1+(tariffPcent==null?0:tariffPcent.floatValue()/100))+rmbToUsd*seaFee+fba)/((1f/(1+vat))-(commissionPcent/100f));
    	
    	if(sku.toLowerCase().contains("local")){
    		this.type = "2";
    	}else if("fr,de,it,es,uk".contains(country)){
    		if(!sku.toLowerCase().contains("-"+country)){
    			if("de".equals(country)){
    				if(sku.toLowerCase().contains("-uk")||sku.toLowerCase().contains("-fr")||sku.toLowerCase().contains("-es")||sku.toLowerCase().contains("-it")){
    					this.type = "1";
    				} 
    			}else{
    				this.type = "1";
    			}
    		}
    	}
    }
	
}


