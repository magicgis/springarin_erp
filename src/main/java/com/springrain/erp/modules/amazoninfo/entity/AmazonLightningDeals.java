package com.springrain.erp.modules.amazoninfo.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "amazoninfo_lightning_deals")
public class AmazonLightningDeals{
	
	private Integer id; 		
	private String country; 	
	private String internalDesc;
	private Date start;
	private Date end;
	private String status;
	private String sku;
	private String productName;
	private Float salePrice;
	private Float dealPrice;
	private Integer dealQuantity;
	private Date updateDate;
	private Integer actualQuantity;
	private Float safePrice;
	
	private String priceInfo;
	private String quantityInfo;
	private Float dealFee;
	private Integer sale1;
	private Integer sale2;
	private Integer sale3;
	private Integer sale4;
	private Integer sale5;
	private Integer rank1;
	private Integer rank2;
	private Integer rank3;
	private Integer rank4;
	private Integer rank5;
	
	private Integer session1;
	private Integer session2;
	private Float conv1;
	private Float conv2;
	
    private String accountName;
    
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public Float getDealFee() {
		return dealFee;
	}
	public void setDealFee(Float dealFee) {
		this.dealFee = dealFee;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getInternalDesc() {
		return internalDesc;
	}
	public void setInternalDesc(String internalDesc) {
		this.internalDesc = internalDesc;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Float getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(Float salePrice) {
		this.salePrice = salePrice;
	}
	public Float getDealPrice() {
		return dealPrice;
	}
	public void setDealPrice(Float dealPrice) {
		this.dealPrice = dealPrice;
	}
	public Integer getDealQuantity() {
		return dealQuantity;
	}
	public void setDealQuantity(Integer dealQuantity) {
		this.dealQuantity = dealQuantity;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Integer getActualQuantity() {
		return actualQuantity;
	}
	public void setActualQuantity(Integer actualQuantity) {
		this.actualQuantity = actualQuantity;
	}
	public Float getSafePrice() {
		return safePrice;
	}
	public void setSafePrice(Float safePrice) {
		this.safePrice = safePrice;
	}
	
	@Transient
	public String getPriceInfo() {
		return priceInfo;
	}
	public void setPriceInfo(String priceInfo) {
		this.priceInfo = priceInfo;
	}
	@Transient
	public String getQuantityInfo() {
		return quantityInfo;
	}
	public void setQuantityInfo(String quantityInfo) {
		this.quantityInfo = quantityInfo;
	}
	public Integer getSale1() {
		return sale1;
	}
	public void setSale1(Integer sale1) {
		this.sale1 = sale1;
	}
	public Integer getSale2() {
		return sale2;
	}
	public void setSale2(Integer sale2) {
		this.sale2 = sale2;
	}
	public Integer getSale3() {
		return sale3;
	}
	public void setSale3(Integer sale3) {
		this.sale3 = sale3;
	}
	public Integer getSale4() {
		return sale4;
	}
	public void setSale4(Integer sale4) {
		this.sale4 = sale4;
	}
	public Integer getSale5() {
		return sale5;
	}
	public void setSale5(Integer sale5) {
		this.sale5 = sale5;
	}
	public Integer getRank1() {
		return rank1;
	}
	public void setRank1(Integer rank1) {
		this.rank1 = rank1;
	}
	public Integer getRank2() {
		return rank2;
	}
	public void setRank2(Integer rank2) {
		this.rank2 = rank2;
	}
	public Integer getRank3() {
		return rank3;
	}
	public void setRank3(Integer rank3) {
		this.rank3 = rank3;
	}
	public Integer getRank4() {
		return rank4;
	}
	public void setRank4(Integer rank4) {
		this.rank4 = rank4;
	}
	public Integer getRank5() {
		return rank5;
	}
	public void setRank5(Integer rank5) {
		this.rank5 = rank5;
	}
	public Integer getSession1() {
		return session1;
	}
	public void setSession1(Integer session1) {
		this.session1 = session1;
	}
	public Integer getSession2() {
		return session2;
	}
	public void setSession2(Integer session2) {
		this.session2 = session2;
	}
	public Float getConv1() {
		return conv1;
	}
	public void setConv1(Float conv1) {
		this.conv1 = conv1;
	}
	public Float getConv2() {
		return conv2;
	}
	public void setConv2(Float conv2) {
		this.conv2 = conv2;
	}
	
}


