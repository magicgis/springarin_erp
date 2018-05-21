package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 产品销售利润统计Entity
 */
@Entity
@Table(name = "amazoninfo_sale_profit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SaleProfit implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id; 		// 编号
	private String day;		//日期字符串
	private String country;		//国家
	private String productName;	//产品名称
	private String type;		//类型
	private String line;		//产品线
	private Float sales;		//销售额
	private Integer salesVolume;//销量
	private Integer feeQuantity;//实际费用数量
	private Float salesNoTax;	//税后收入
	private Float refund;		//退款
	private Float amazonFee;	//亚马逊佣金
	private Float otherFee;		//杂费
	private Float transportFee; //运输费
	private Float buyCost;		//采购成本(单价)
	private Float profits;		//利润
	private String end;
	private Integer returnNum;	//退货数量
	
	private Integer supportNum;	//替代货数量(亚马逊后台创建的都算替代)
	private Float supportCost;	//替代货成本(运费+采购成本)
	private Float supportAmazonFee;	//替代货亚马逊费用
	private Integer reviewNum;	//评测单数量
	private Float reviewCost;	//评测单成本(运费+采购成本)
	private Float reviewAmazonFee;	//评测单亚马逊费用
	
	private Float adInEventSales; //站内event广告销售额
	private Integer adInEventSalesVolume; //站内event广告销量
	private Float adInEventFee; //站内event广告费用
	private Float adInProfitSales; //站内Profit广告销售额
	private Integer adInProfitSalesVolume; //站内Profit广告销量
	private Float adInProfitFee; //站内Profit广告费用
	
	private Float adOutEventSales; //站外event广告销售额
	private Integer adOutEventSalesVolume; //站外event广告销量
	private Float adOutEventFee; //站外event广告费用
	private Float adOutProfitSales; //站外Profit广告销售额
	private Integer adOutProfitSalesVolume; //站外Profit广告销量
	private Float adOutProfitFee; //站外Profit广告费用
	
	private Float adAmsSales; //AMS广告销售额
	private Integer adAmsSalesVolume; //AMS广告销量
	private Float adAmsFee; //AMS广告费用
	
	private Integer recallNum; //召回数量
	private Float recallCost; //召回成本(运费+采购)
	private Float recallFee; //召回费用
	private Integer marketNum; //市场推广的订单（B2B）
	private Float marketSales;//B2B销售额
	private Float marketProfit;//B2B利润
	
	private Float tariff = 0f;	//关税
	private Float storageFee;	//仓储费
	private Float longStorageFee = 0f;	//长期仓储费
	
	private Integer dealSalesVolume = 0; //闪促销量
	private Float dealFee = 0f; //闪促费用
	private Float dealProfit = 0f; //闪促盈亏
	private String productAttr;
	private Float moldFee = 0f; //模具费
	private String accountName;

	private Float expressFee = 0f; //自发货邮费
	private Float vineFee = 0f; //vine项目费用
	private Integer vineNum = 0; //vine项目数量
	private Float vineCost = 0f; //vine项目成本
	
	
	public SaleProfit() {
		super();
	}

	public SaleProfit(Integer id){
		this();
		this.id = id;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Float getSales() {
		return sales;
	}

	public void setSales(Float sales) {
		this.sales = sales;
	}

	public Integer getSalesVolume() {
		return salesVolume;
	}

	public void setSalesVolume(Integer salesVolume) {
		this.salesVolume = salesVolume;
	}

	public Integer getFeeQuantity() {
		return feeQuantity;
	}

	public void setFeeQuantity(Integer feeQuantity) {
		this.feeQuantity = feeQuantity;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public Float getSalesNoTax() {
		return salesNoTax;
	}

	public void setSalesNoTax(Float salesNoTax) {
		this.salesNoTax = salesNoTax;
	}

	public Float getRefund() {
		return refund;
	}

	public void setRefund(Float refund) {
		this.refund = refund;
	}

	public Float getAmazonFee() {
		return amazonFee;
	}

	public void setAmazonFee(Float amazonFee) {
		this.amazonFee = amazonFee;
	}

	public Float getOtherFee() {
		return otherFee;
	}

	public void setOtherFee(Float otherFee) {
		this.otherFee = otherFee;
	}

	public Float getTransportFee() {
		return transportFee;
	}

	public void setTransportFee(Float transportFee) {
		this.transportFee = transportFee;
	}

	public Float getBuyCost() {
		return buyCost;
	}

	public void setBuyCost(Float buyCost) {
		this.buyCost = buyCost;
	}

	public Float getProfits() {
		return profits;
	}

	public void setProfits(Float profits) {
		this.profits = profits;
	}

	@Transient
	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public Integer getReturnNum() {
		return returnNum;
	}

	public void setReturnNum(Integer returnNum) {
		this.returnNum = returnNum;
	}

	public Integer getSupportNum() {
		return supportNum;
	}

	public void setSupportNum(Integer supportNum) {
		this.supportNum = supportNum;
	}

	public Integer getReviewNum() {
		return reviewNum;
	}

	public void setReviewNum(Integer reviewNum) {
		this.reviewNum = reviewNum;
	}

	public Float getSupportCost() {
		return supportCost;
	}

	public void setSupportCost(Float supportCost) {
		this.supportCost = supportCost;
	}

	public Float getSupportAmazonFee() {
		return supportAmazonFee;
	}

	public void setSupportAmazonFee(Float supportAmazonFee) {
		this.supportAmazonFee = supportAmazonFee;
	}

	public Float getReviewCost() {
		return reviewCost;
	}

	public void setReviewCost(Float reviewCost) {
		this.reviewCost = reviewCost;
	}

	public Float getReviewAmazonFee() {
		return reviewAmazonFee;
	}

	public void setReviewAmazonFee(Float reviewAmazonFee) {
		this.reviewAmazonFee = reviewAmazonFee;
	}

	public Float getAdInEventSales() {
		return adInEventSales;
	}

	public void setAdInEventSales(Float adInEventSales) {
		this.adInEventSales = adInEventSales;
	}

	public Integer getAdInEventSalesVolume() {
		return adInEventSalesVolume;
	}

	public void setAdInEventSalesVolume(Integer adInEventSalesVolume) {
		this.adInEventSalesVolume = adInEventSalesVolume;
	}

	public Float getAdInEventFee() {
		return adInEventFee;
	}

	public void setAdInEventFee(Float adInEventFee) {
		this.adInEventFee = adInEventFee;
	}

	public Float getAdInProfitSales() {
		return adInProfitSales;
	}

	public void setAdInProfitSales(Float adInProfitSales) {
		this.adInProfitSales = adInProfitSales;
	}

	public Integer getAdInProfitSalesVolume() {
		return adInProfitSalesVolume;
	}

	public void setAdInProfitSalesVolume(Integer adInProfitSalesVolume) {
		this.adInProfitSalesVolume = adInProfitSalesVolume;
	}

	public Float getAdInProfitFee() {
		return adInProfitFee;
	}

	public void setAdInProfitFee(Float adInProfitFee) {
		this.adInProfitFee = adInProfitFee;
	}

	public Float getAdOutEventSales() {
		return adOutEventSales;
	}

	public void setAdOutEventSales(Float adOutEventSales) {
		this.adOutEventSales = adOutEventSales;
	}

	public Integer getAdOutEventSalesVolume() {
		return adOutEventSalesVolume;
	}

	public void setAdOutEventSalesVolume(Integer adOutEventSalesVolume) {
		this.adOutEventSalesVolume = adOutEventSalesVolume;
	}

	public Float getAdOutEventFee() {
		return adOutEventFee;
	}

	public void setAdOutEventFee(Float adOutEventFee) {
		this.adOutEventFee = adOutEventFee;
	}

	public Float getAdOutProfitSales() {
		return adOutProfitSales;
	}

	public void setAdOutProfitSales(Float adOutProfitSales) {
		this.adOutProfitSales = adOutProfitSales;
	}

	public Integer getAdOutProfitSalesVolume() {
		return adOutProfitSalesVolume;
	}

	public void setAdOutProfitSalesVolume(Integer adOutProfitSalesVolume) {
		this.adOutProfitSalesVolume = adOutProfitSalesVolume;
	}

	public Float getAdOutProfitFee() {
		return adOutProfitFee;
	}

	public void setAdOutProfitFee(Float adOutProfitFee) {
		this.adOutProfitFee = adOutProfitFee;
	}

	public Float getAdAmsSales() {
		return adAmsSales;
	}

	public void setAdAmsSales(Float adAmsSales) {
		this.adAmsSales = adAmsSales;
	}

	public Integer getAdAmsSalesVolume() {
		return adAmsSalesVolume;
	}

	public void setAdAmsSalesVolume(Integer adAmsSalesVolume) {
		this.adAmsSalesVolume = adAmsSalesVolume;
	}

	public Float getAdAmsFee() {
		return adAmsFee;
	}

	public void setAdAmsFee(Float adAmsFee) {
		this.adAmsFee = adAmsFee;
	}

	public Integer getRecallNum() {
		return recallNum;
	}

	public void setRecallNum(Integer recallNum) {
		this.recallNum = recallNum;
	}

	public Float getRecallCost() {
		return recallCost;
	}

	public void setRecallCost(Float recallCost) {
		this.recallCost = recallCost;
	}

	public Float getRecallFee() {
		return recallFee;
	}

	public void setRecallFee(Float recallFee) {
		this.recallFee = recallFee;
	}

	public Integer getMarketNum() {
		return marketNum;
	}

	public void setMarketNum(Integer marketNum) {
		this.marketNum = marketNum;
	}

	public Float getMarketSales() {
		return marketSales;
	}

	public void setMarketSales(Float marketSales) {
		this.marketSales = marketSales;
	}

	public Float getMarketProfit() {
		return marketProfit;
	}

	public void setMarketProfit(Float marketProfit) {
		this.marketProfit = marketProfit;
	}

	public Float getTariff() {
		return tariff;
	}

	public void setTariff(Float tariff) {
		this.tariff = tariff;
	}

	public Float getStorageFee() {
		return storageFee;
	}

	public void setStorageFee(Float storageFee) {
		this.storageFee = storageFee;
	}

	@Transient
	public Float getLongStorageFee() {
		return longStorageFee;
	}

	@Transient
	public void setLongStorageFee(Float longStorageFee) {
		this.longStorageFee = longStorageFee;
	}

	public Integer getDealSalesVolume() {
		return dealSalesVolume;
	}

	public void setDealSalesVolume(Integer dealSalesVolume) {
		this.dealSalesVolume = dealSalesVolume;
	}

	public Float getDealFee() {
		return dealFee;
	}

	public void setDealFee(Float dealFee) {
		this.dealFee = dealFee;
	}

	public Float getDealProfit() {
		return dealProfit;
	}

	public void setDealProfit(Float dealProfit) {
		this.dealProfit = dealProfit;
	}

	//平均售价
	@Transient
	public Float getAvgSales() {
		if (salesVolume != null && salesVolume > 0) {
			return sales/salesVolume;
		}
		return 0f;
	}

	//利润率
	@Transient
	public Float getProfitRate() {
		if (sales != null && sales > 0) {
			return profits/sales;
		}
		return 0f;
	}

	@Transient
	public Float getAdProfit() {
		//广告费用总计(负数)
		float adFee = adOutEventFee+adOutProfitFee+adInEventFee+adInProfitFee+adAmsFee;
		int addQty = adInEventSalesVolume+adInProfitSalesVolume+adOutEventSalesVolume+adOutProfitSalesVolume+adAmsSalesVolume;
		if (salesVolume > 0) {
			return (profits-adFee)/salesVolume*addQty + adFee;
		}
		return adFee;
	}

	public String getProductAttr() {
		return productAttr;
	}

	public void setProductAttr(String productAttr) {
		this.productAttr = productAttr;
	}

	public Float getMoldFee() {
		return moldFee;
	}

	public void setMoldFee(Float moldFee) {
		this.moldFee = moldFee;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Float getExpressFee() {
		return expressFee;
	}

	public void setExpressFee(Float expressFee) {
		this.expressFee = expressFee;
	}

	public Float getVineFee() {
		return vineFee;
	}

	public void setVineFee(Float vineFee) {
		this.vineFee = vineFee;
	}

	public Integer getVineNum() {
		return vineNum;
	}

	public void setVineNum(Integer vineNum) {
		this.vineNum = vineNum;
	}

	public Float getVineCost() {
		return vineCost;
	}

	public void setVineCost(Float vineCost) {
		this.vineCost = vineCost;
	}
	
}


