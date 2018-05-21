/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 产品销量销售额Entity
 * @author Tim
 * @version 2015-06-01
 */
@Entity
@Table(name = "amazoninfo_sale_report")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SaleReport implements Serializable,Comparable<SaleReport> {
	
	private static final long serialVersionUID = 1L;
	private Integer id; 		// 编号
	private String productName; 	// 名称
	private String color;
	private Float sales;
	private Float sureSales;
	private Float realSales;
	private Integer salesVolume;
	private Integer sureSalesVolume;
	private Integer realSalesVolume;
	private String sku;
	private String country;
	private Date date;
	
	private String searchType="1";//日/周/年
	private Date start;
	private Date end;
	private String productType;
	private String groupName;
	private String currencyType="EUR";//统计的货币类型，默认为欧元（暂时支持欧元和美元两种货币，即EUR、USD）
	
	private String orderType;//1 amazon 2 vendor 3 ebay 4.unline(排除官网和check24) 5.官网  6.check24  7:other
	
	private Integer feeQuantity;
	private Float fee;
	private Float feeOther;
	private Float salesNoTax;
	private Float refund;
	
	private Integer maxOrder;
	private Integer promotionsOrder;
	private Integer flashSalesOrder;
	
	private Integer freeOrder;
	private Integer adsOrder;
	private Integer amsOrder;
	
	private String classType;
	private Float avgFreight;	//单品实时运费(欧元),系统(psi_product_avg_price)无数据时记录为-1
	private Float price;		//产品当天的保本价
	private Integer returnNum;	//退货数量
	
	private Integer reviewVolume;
	private Integer supportVolume;
	
	private Integer outsideOrder;
	private Integer realOrder;
	
	private Integer businessOrder;
	
	private String productAttr;
	private Integer packNum;//sku对应产品打包数(为兼容business项目,普通sku为1个，business项目sku如84-BCST-31-BK-US_pack50表示50个)
	
	private String accountName;
	
	private Integer coupon;
	
	public Integer getCoupon() {
		return coupon;
	}

	public void setCoupon(Integer coupon) {
		this.coupon = coupon;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Integer getRealOrder() {
		return realOrder;
	}

	public void setRealOrder(Integer realOrder) {
		this.realOrder = realOrder;
	}

	public Integer getOutsideOrder() {
		return outsideOrder;
	}

	public void setOutsideOrder(Integer outsideOrder) {
		this.outsideOrder = outsideOrder;
	}

	@Transient
	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public Integer getFreeOrder() {
		return freeOrder;
	}

	public void setFreeOrder(Integer freeOrder) {
		this.freeOrder = freeOrder;
	}

	public Integer getAdsOrder() {
		return adsOrder;
	}

	public void setAdsOrder(Integer adsOrder) {
		this.adsOrder = adsOrder;
	}

	public Integer getMaxOrder() {
		return maxOrder;
	}

	public void setMaxOrder(Integer maxOrder) {
		this.maxOrder = maxOrder;
	}

	public Integer getPromotionsOrder() {
		return promotionsOrder;
	}

	public void setPromotionsOrder(Integer promotionsOrder) {
		this.promotionsOrder = promotionsOrder;
	}

	public Integer getFlashSalesOrder() {
		return flashSalesOrder;
	}

	public void setFlashSalesOrder(Integer flashSalesOrder) {
		this.flashSalesOrder = flashSalesOrder;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public SaleReport() {
		super();
	}

	public SaleReport(Integer id){
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
	
	public Float getRefund() {
		return refund;
	}

	public void setRefund(Float refund) {
		this.refund = refund;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
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

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	

	public Float getSureSales() {
		return sureSales;
	}

	public void setSureSales(Float sureSales) {
		this.sureSales = sureSales;
	}

	public Integer getSureSalesVolume() {
		return sureSalesVolume;
	}

	public void setSureSalesVolume(Integer sureSalesVolume) {
		this.sureSalesVolume = sureSalesVolume;
	}

	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Column(updatable=false)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Transient
	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	
	@Transient
	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	@Transient
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Transient
	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}
	
	@Transient
	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}
	
	@Transient
	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	public Integer getFeeQuantity() {
		return feeQuantity;
	}

	public void setFeeQuantity(Integer feeQuantity) {
		this.feeQuantity = feeQuantity;
	}

	public Float getFee() {
		return fee;
	}

	public void setFee(Float fee) {
		this.fee = fee;
	}

	public Float getFeeOther() {
		return feeOther;
	}

	public void setFeeOther(Float feeOther) {
		this.feeOther = feeOther;
	}

	public Float getSalesNoTax() {
		return salesNoTax;
	}

	public void setSalesNoTax(Float salesNoTax) {
		this.salesNoTax = salesNoTax;
	}

	public SaleReport(Float sales, Float sureSales,Float realSales, Integer salesVolume,
			Integer sureSalesVolume,Integer realSalesVolume) {
		super();
		this.sales = sales;
		this.sureSales = sureSales;
		this.realSales = realSales;
		this.salesVolume = salesVolume;
		this.sureSalesVolume = sureSalesVolume;
		this.realSalesVolume = realSalesVolume;
	}
	
	public SaleReport(Float sales, Float sureSales, Integer salesVolume,
			Integer sureSalesVolume) {
		super();
		this.sales = sales;
		this.sureSales = sureSales;
		this.salesVolume = salesVolume;
		this.sureSalesVolume = sureSalesVolume;
	}
	
	

	public SaleReport(Float sales, Float sureSales, Integer salesVolume, 
			Integer sureSalesVolume, Integer maxOrder, Integer promotionsOrder,
			Integer flashSalesOrder, Integer freeOrder, Integer adsOrder) {
		super();
		this.sales = sales;
		this.sureSales = sureSales;
		this.salesVolume = salesVolume;
		this.sureSalesVolume = sureSalesVolume;
		this.maxOrder = maxOrder;
		this.promotionsOrder = promotionsOrder;
		this.flashSalesOrder = flashSalesOrder;
		this.freeOrder = freeOrder;
		this.adsOrder = adsOrder;
	}
	
	public SaleReport(Float sales, Float sureSales, Integer salesVolume, 
			Integer sureSalesVolume, Integer maxOrder, Integer promotionsOrder,
			Integer flashSalesOrder, Integer freeOrder, Integer adsOrder,String classType) {
		super();
		this.sales = sales;
		this.sureSales = sureSales;
		this.salesVolume = salesVolume;
		this.sureSalesVolume = sureSalesVolume;
		this.maxOrder = maxOrder;
		this.promotionsOrder = promotionsOrder;
		this.flashSalesOrder = flashSalesOrder;
		this.freeOrder = freeOrder;
		this.adsOrder = adsOrder;
		this.classType=classType;
	}
	
	public SaleReport(Float sales, Float sureSales, Integer salesVolume, 
			Integer sureSalesVolume, Integer maxOrder, Integer promotionsOrder,
			Integer flashSalesOrder, Integer freeOrder, Integer adsOrder,String classType,Integer reviewVolume,Integer supportVolume,Integer amsOrder,Integer outsideOrder) {
		super();
		this.sales = sales;
		this.sureSales = sureSales;
		this.salesVolume = salesVolume;
		this.sureSalesVolume = sureSalesVolume;
		this.maxOrder = maxOrder;
		this.promotionsOrder = promotionsOrder;
		this.flashSalesOrder = flashSalesOrder;
		this.freeOrder = freeOrder;
		this.adsOrder = adsOrder;
		this.classType=classType;
		this.reviewVolume = reviewVolume;
		this.supportVolume =supportVolume;
		this.amsOrder=amsOrder;
		this.outsideOrder=outsideOrder;
	}


	public SaleReport(Float sales, Float sureSales, Integer salesVolume,
			Integer sureSalesVolume, Integer maxOrder, Integer promotionsOrder,
			Integer flashSalesOrder) {
		super();
		this.sales = sales;
		this.sureSales = sureSales;
		this.salesVolume = salesVolume;
		this.sureSalesVolume = sureSalesVolume;
		this.maxOrder = maxOrder;
		this.promotionsOrder = promotionsOrder;
		this.flashSalesOrder = flashSalesOrder;
	}

	@Override
	public int compareTo(SaleReport o) {
		if(sales==null&&o.sales==null){
			return 0;
		}else if(sales==null||o.sales==null){
			return sales==null?1:-1;
		}
		return -sales.compareTo(o.getSales());
	}

	public Float getRealSales() {
		return realSales;
	}

	public void setRealSales(Float realSales) {
		this.realSales = realSales;
	}

	public Integer getRealSalesVolume() {
		return realSalesVolume;
	}

	public void setRealSalesVolume(Integer realSalesVolume) {
		this.realSalesVolume = realSalesVolume;
	}

	public Float getAvgFreight() {
		return avgFreight;
	}

	public void setAvgFreight(Float avgFreight) {
		this.avgFreight = avgFreight;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Integer getReturnNum() {
		return returnNum;
	}

	public void setReturnNum(Integer returnNum) {
		this.returnNum = returnNum;
	}

	public Integer getReviewVolume() {
		return reviewVolume;
	}

	public void setReviewVolume(Integer reviewVolume) {
		this.reviewVolume = reviewVolume;
	}

	public Integer getSupportVolume() {
		return supportVolume;
	}

	public void setSupportVolume(Integer supportVolume) {
		this.supportVolume = supportVolume;
	}

	public Integer getAmsOrder() {
		return amsOrder;
	}

	public void setAmsOrder(Integer amsOrder) {
		this.amsOrder = amsOrder;
	}

	public Integer getBusinessOrder() {
		return businessOrder;
	}

	public void setBusinessOrder(Integer businessOrder) {
		this.businessOrder = businessOrder;
	}

	public String getProductAttr() {
		return productAttr;
	}

	public void setProductAttr(String productAttr) {
		this.productAttr = productAttr;
	}

	public Integer getPackNum() {
		return packNum;
	}

	public void setPackNum(Integer packNum) {
		this.packNum = packNum;
	}

}


