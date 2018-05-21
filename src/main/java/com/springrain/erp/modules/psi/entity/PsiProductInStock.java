package com.springrain.erp.modules.psi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 进销存产品历史库存
 */
@Entity
@Table(name = "psi_product_in_stock")
@DynamicInsert
@DynamicUpdate
public class PsiProductInStock {
	private Integer id;
	
	private String productName;	//产品名称（带颜色）
	private String country;	//国家
	private Integer producting = 0;	//在产数量
	private Integer cn = 0;	//中国仓
	private Integer transit = 0;	//在途数量
	private Integer overseas = 0;	//海外仓(实)
	private Integer day31Sales = 0;	//31日销量
	private Integer totalStock = 0;	//总库存(含fba)
	private double daySales = 0;	//采购期预日销
	private double monthSales = 0;	//销售期预月销
	private double safeInventory = 0;	//安全库存量
	private Integer safeDay = 0;	//安全库存可销天数
	private double orderPoint = 0;	//下单点
	private double balance = 0;	//结余数
	private double inventoryDay = 0;	//库存可销天数
	private Integer orderQuantity = 0;	//下单量
	private Integer airReplenishment;	//空运补货量
	private Integer period;	//周期
	private Date dataDate;
	
	private User lastUpdateBy;
	private String barcode;
	private String sku;
	private Float price;	//美金价格
	private Float rmbPrice;//人民币价格
	private String isSale;	//是否在售0：淘汰 1：在售
	private String isNew;	//是否新品 0：普通 1：新品
	private String isMain;	//是否主力 0：普通 1：主力
	private double inventorySaleMonth;	//库销比(库存可销月数)
	
	private Integer cnLc;  //中国仓(理诚)
	
	private String  saleUser;//运营
	private String  cameraman;//摄影师
	private String  purchaseUser;//采购经理
	private String  customer;//客服
	private String  merchandiser;//跟单员
	private String  productManager;//产品经理
	
	//365/(生产运输期加缓冲期加安全库存天数)   欧洲的取整个平台的安全库存天数     淘汰品不算  新品算上架2个月以上的     加拿和US的标准一样  JP的和EU的标准一样  
	private Float turnoverStandard;//周转率标准
	
	private Integer recall = 0;	//召回在途数(请求数-完成数-取消数)
	
	public Float getTurnoverStandard() {
		return turnoverStandard;
	}

	public void setTurnoverStandard(Float turnoverStandard) {
		this.turnoverStandard = turnoverStandard;
	}

	public String getProductManager() {
		return productManager;
	}

	public void setProductManager(String productManager) {
		this.productManager = productManager;
	}

	public String getSaleUser() {
		return saleUser;
	}

	public void setSaleUser(String saleUser) {
		this.saleUser = saleUser;
	}

	public String getCameraman() {
		return cameraman;
	}

	public void setCameraman(String cameraman) {
		this.cameraman = cameraman;
	}

	public String getPurchaseUser() {
		return purchaseUser;
	}

	public void setPurchaseUser(String purchaseUser) {
		this.purchaseUser = purchaseUser;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getMerchandiser() {
		return merchandiser;
	}

	public void setMerchandiser(String merchandiser) {
		this.merchandiser = merchandiser;
	}

	public PsiProductInStock() {
		super();
	}

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

	public Integer getProducting() {
		return producting;
	}

	public void setProducting(Integer producting) {
		this.producting = producting;
	}

	public Integer getCn() {
		return cn;
	}

	public void setCn(Integer cn) {
		this.cn = cn;
	}

	public Integer getTransit() {
		return transit;
	}

	public void setTransit(Integer transit) {
		this.transit = transit;
	}

	@Column(name="day31_sales")
	public Integer getDay31Sales() {
		return day31Sales;
	}

	public void setDay31Sales(Integer day31Sales) {
		this.day31Sales = day31Sales;
	}

	public Integer getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(Integer totalStock) {
		this.totalStock = totalStock;
	}

	public double getDaySales() {
		return daySales;
	}

	public void setDaySales(double daySales) {
		this.daySales = daySales;
	}

	public double getMonthSales() {
		return monthSales;
	}

	public void setMonthSales(double monthSales) {
		this.monthSales = monthSales;
	}

	public double getSafeInventory() {
		return safeInventory;
	}

	public void setSafeInventory(double safeInventory) {
		this.safeInventory = safeInventory;
	}

	public Integer getSafeDay() {
		return safeDay;
	}

	public void setSafeDay(Integer safeDay) {
		this.safeDay = safeDay;
	}

	public double getOrderPoint() {
		return orderPoint;
	}

	public void setOrderPoint(double orderPoint) {
		this.orderPoint = orderPoint;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getInventoryDay() {
		return inventoryDay;
	}

	public void setInventoryDay(double inventoryDay) {
		this.inventoryDay = inventoryDay;
	}

	public Integer getOrderQuantity() {
		return orderQuantity;
	}

	public void setOrderQuantity(Integer orderQuantity) {
		this.orderQuantity = orderQuantity;
	}

	public Integer getAirReplenishment() {
		return airReplenishment;
	}

	public void setAirReplenishment(Integer airReplenishment) {
		this.airReplenishment = airReplenishment;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getDataDate() {
		return dataDate;
	}

	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}
	
	public Integer getOverseas() {
		return overseas;
	}

	public void setOverseas(Integer overseas) {
		this.overseas = overseas;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getLastUpdateBy() {
		return lastUpdateBy;
	}

	public void setLastUpdateBy(User lastUpdateBy) {
		this.lastUpdateBy = lastUpdateBy;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}
	
	public Float getRmbPrice() {
		return rmbPrice;
	}

	public void setRmbPrice(Float rmbPrice) {
		this.rmbPrice = rmbPrice;
	}

	public String getIsSale() {
		return isSale;
	}

	public void setIsSale(String isSale) {
		this.isSale = isSale;
	}

	public String getIsNew() {
		return isNew;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}

	public String getIsMain() {
		return isMain;
	}

	public void setIsMain(String isMain) {
		this.isMain = isMain;
	}

	public double getInventorySaleMonth() {
		return inventorySaleMonth;
	}

	public void setInventorySaleMonth(double inventorySaleMonth) {
		this.inventorySaleMonth = inventorySaleMonth;
	}

	public Integer getCnLc() {
		return cnLc;
	}

	public void setCnLc(Integer cnLc) {
		this.cnLc = cnLc;
	}

	public Integer getRecall() {
		return recall;
	}

	public void setRecall(Integer recall) {
		this.recall = recall;
	}
	
}
