package com.springrain.erp.modules.amazoninfo.entity;

public class CategoryDto {
	
	private String brand;
	private Float sales;
	private Integer quantity;
	private Float price;
	private String asin;
	private Float qtyRate;
	private Float salesRate;
	private String catalog;
	private Float totalSales;
	
	private String price1;
	private String price2;
	private String price3;
	private String price4;
	private Integer num;
	private Integer brandNum;
	
	
	public Integer getBrandNum() {
		return brandNum;
	}
	public void setBrandNum(Integer brandNum) {
		this.brandNum = brandNum;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getPrice1() {
		return price1;
	}
	public void setPrice1(String price1) {
		this.price1 = price1;
	}
	public String getPrice2() {
		return price2;
	}
	public void setPrice2(String price2) {
		this.price2 = price2;
	}
	public String getPrice3() {
		return price3;
	}
	public void setPrice3(String price3) {
		this.price3 = price3;
	}
	public String getPrice4() {
		return price4;
	}
	public void setPrice4(String price4) {
		this.price4 = price4;
	}
	
	public Float getTotalSales() {
		return totalSales;
	}
	public void setTotalSales(Float totalSales) {
		this.totalSales = totalSales;
	}
	public String getCatalog() {
		return catalog;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public Float getSales() {
		return sales;
	}
	public void setSales(Float sales) {
		this.sales = sales;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getAsin() {
		return asin;
	}
	public void setAsin(String asin) {
		this.asin = asin;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public Float getQtyRate() {
		return qtyRate;
	}
	public void setQtyRate(Float qtyRate) {
		this.qtyRate = qtyRate;
	}
	public Float getSalesRate() {
		return salesRate;
	}
	public void setSalesRate(Float salesRate) {
		this.salesRate = salesRate;
	}
}
