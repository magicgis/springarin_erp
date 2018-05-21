package com.springrain.erp.modules.amazoninfo.entity;

import java.util.Date;
import java.util.List;


/**
 * 站外促销分析Dto
 */
public class OutsidePromotionDto {
	private 	Integer 	inventoryQuantity;
	private 	Integer		session;
	private 	Float		conversion;	         //转化率
	private 	Integer 	saleQuantity;        //销量
	private 	Integer 	saleQuantityPro;    //(促销)销量
	private 	Float 		price;               //单价
	private 	Float 		saleAmount;          //销售额
	private 	Float 		saleAmountPro;       //(促销)销售额
	private 	Float 		profitsAmount;       //利润
	private 	Float 		promoAmount;         //促销预算
	private     Date        dataDate;            //数据日期
	private     String      dataDateStr;         //数据日期
	private 	List<AmazonCatalogRank> 		ranks;
	
	private 	List<Integer> 		sessions;
	private 	List<Float> 		conversions;
	private 	List<Integer> 		saleQuantitys;
	private 	List<Integer> 		saleQuantityPros;
	
	public Integer getInventoryQuantity() {
		return inventoryQuantity;
	}
	public void setInventoryQuantity(Integer inventoryQuantity) {
		this.inventoryQuantity = inventoryQuantity;
	}
	
	
	public List<AmazonCatalogRank> getRanks() {
		return ranks;
	}
	public void setRanks(List<AmazonCatalogRank> ranks) {
		this.ranks = ranks;
	}
	public Integer getSession() {
		return session;
	}
	public void setSession(Integer session) {
		this.session = session;
	}
	
	public String getDataDateStr() {
		return dataDateStr;
	}
	public void setDataDateStr(String dataDateStr) {
		this.dataDateStr = dataDateStr;
	}
	public Float getConversion() {
		return conversion;
	}
	public void setConversion(Float conversion) {
		this.conversion = conversion;
	}
	public Integer getSaleQuantity() {
		return saleQuantity;
	}
	public void setSaleQuantity(Integer saleQuantity) {
		this.saleQuantity = saleQuantity;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public Float getSaleAmount() {
		return saleAmount;
	}
	public void setSaleAmount(Float saleAmount) {
		this.saleAmount = saleAmount;
	}
	public Float getProfitsAmount() {
		return profitsAmount;
	}
	public void setProfitsAmount(Float profitsAmount) {
		this.profitsAmount = profitsAmount;
	}
	public Float getPromoAmount() {
		return promoAmount;
	}
	public void setPromoAmount(Float promoAmount) {
		this.promoAmount = promoAmount;
	}
	
	
	public Integer getSaleQuantityPro() {
		return saleQuantityPro;
	}
	public void setSaleQuantityPro(Integer saleQuantityPro) {
		this.saleQuantityPro = saleQuantityPro;
	}
	public Float getSaleAmountPro() {
		return saleAmountPro;
	}
	public void setSaleAmountPro(Float saleAmountPro) {
		this.saleAmountPro = saleAmountPro;
	}
	public Date getDataDate() {
		return dataDate;
	}
	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}
	
	public List<Integer> getSessions() {
		return sessions;
	}
	public void setSessions(List<Integer> sessions) {
		this.sessions = sessions;
	}
	public List<Float> getConversions() {
		return conversions;
	}
	public void setConversions(List<Float> conversions) {
		this.conversions = conversions;
	}
	public List<Integer> getSaleQuantitys() {
		return saleQuantitys;
	}
	public void setSaleQuantitys(List<Integer> saleQuantitys) {
		this.saleQuantitys = saleQuantitys;
	}
	public List<Integer> getSaleQuantityPros() {
		return saleQuantityPros;
	}
	public void setSaleQuantityPros(List<Integer> saleQuantityPros) {
		this.saleQuantityPros = saleQuantityPros;
	}
	
	
	
	public void getOutsidePromotionDto(){}
	public OutsidePromotionDto(Integer inventoryQuantity, List<AmazonCatalogRank> ranks,Integer session, Float conversion, Integer saleQuantity,
			Float price,Float saleAmount, Float profitsAmount,	Float promoAmount,Date dataDate,Integer saleQuantityPro,Float saleAmountPro,String dataDateStr
			,List<Integer> sessions,List<Float> conversions,List<Integer> saleQuantityPros,List<Integer> saleQuantitys) {
		super();
		this.inventoryQuantity = inventoryQuantity;
		this.ranks = ranks;
		this.session = session;
		this.conversion = conversion;
		this.saleQuantity = saleQuantity;
		this.price = price;
		this.saleAmount = saleAmount;
		this.profitsAmount = profitsAmount;
		this.promoAmount = promoAmount;
		this.dataDate=dataDate;
		this.saleQuantityPro=saleQuantityPro;
		this.saleAmountPro=saleAmountPro;
		this.dataDateStr=dataDateStr;
		this.sessions=sessions;
		this.conversions=conversions;
		this.saleQuantitys=saleQuantitys;
		this.saleQuantityPros=saleQuantityPros;
	};
	
	

}
