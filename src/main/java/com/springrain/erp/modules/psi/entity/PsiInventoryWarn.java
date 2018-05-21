package com.springrain.erp.modules.psi.entity;

public class PsiInventoryWarn {
	
	private String productName;//产品
	private String createUserEmail;//邮箱
	private Integer moq;//MOQ
	private String country;//国家
	private Integer producting;//产
	private Integer chinaQuantity;//中国仓
	private Integer transportting;//途
	private Integer seaQuantity;//海外仓
	private Integer fbaTotal;//FBA总
	private Integer total;//总库存
	private Integer saleDay;//库存可销天
	private Integer preiod;//周期
	private Integer day31Sales;//31日销
	private Integer safeSales;//量(安全库存)
	private Integer safeDay;//天(安全库存)
	private Integer point;//下单点
	private Integer forecastAfterPreiodSales;//销售期预月销
	private Integer balance;//结余
	private Integer orderSales;//下单量
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getCreateUserEmail() {
		return createUserEmail;
	}
	public void setCreateUserEmail(String createUserEmail) {
		this.createUserEmail = createUserEmail;
	}
	public Integer getMoq() {
		return moq;
	}
	public void setMoq(Integer moq) {
		this.moq = moq;
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
	public Integer getChinaQuantity() {
		return chinaQuantity;
	}
	public void setChinaQuantity(Integer chinaQuantity) {
		this.chinaQuantity = chinaQuantity;
	}
	public Integer getTransportting() {
		return transportting;
	}
	public void setTransportting(Integer transportting) {
		this.transportting = transportting;
	}
	public Integer getSeaQuantity() {
		return seaQuantity;
	}
	public void setSeaQuantity(Integer seaQuantity) {
		this.seaQuantity = seaQuantity;
	}
	public Integer getFbaTotal() {
		return fbaTotal;
	}
	public void setFbaTotal(Integer fbaTotal) {
		this.fbaTotal = fbaTotal;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getSaleDay() {
		return saleDay;
	}
	public void setSaleDay(Integer saleDay) {
		this.saleDay = saleDay;
	}
	public Integer getPreiod() {
		return preiod;
	}
	public void setPreiod(Integer preiod) {
		this.preiod = preiod;
	}
	public Integer getDay31Sales() {
		return day31Sales;
	}
	public void setDay31Sales(Integer day31Sales) {
		this.day31Sales = day31Sales;
	}
	public Integer getSafeSales() {
		return safeSales;
	}
	public void setSafeSales(Integer safeSales) {
		this.safeSales = safeSales;
	}
	public Integer getSafeDay() {
		return safeDay;
	}
	public void setSafeDay(Integer safeDay) {
		this.safeDay = safeDay;
	}
	public Integer getPoint() {
		return point;
	}
	public void setPoint(Integer point) {
		this.point = point;
	}
	public Integer getForecastAfterPreiodSales() {
		return forecastAfterPreiodSales;
	}
	public void setForecastAfterPreiodSales(Integer forecastAfterPreiodSales) {
		this.forecastAfterPreiodSales = forecastAfterPreiodSales;
	}
	public Integer getBalance() {
		return balance;
	}
	public void setBalance(Integer balance) {
		this.balance = balance;
	}
	public Integer getOrderSales() {
		return orderSales;
	}
	public void setOrderSales(Integer orderSales) {
		this.orderSales = orderSales;
	}
	
	@Override
	public String toString() {
		return "PsiInventoryWarn [productName=" + productName
				+ ", createUserEmail=" + createUserEmail + ", moq=" + moq
				+ ", country=" + country + ", producting=" + producting
				+ ", chinaQuantity=" + chinaQuantity + ", transportting="
				+ transportting + ", seaQuantity=" + seaQuantity
				+ ", fbaTotal=" + fbaTotal + ", total=" + total + ", saleDay="
				+ saleDay + ", preiod=" + preiod + ", day31Sales=" + day31Sales
				+ ", safeSales=" + safeSales + ", safeDay=" + safeDay
				+ ", point=" + point + ", forecastAfterPreiodSales="
				+ forecastAfterPreiodSales + ", balance=" + balance
				+ ", orderSales=" + orderSales + "]";
	}
	
}
