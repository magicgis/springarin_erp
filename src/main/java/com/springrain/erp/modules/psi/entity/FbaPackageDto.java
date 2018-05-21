package com.springrain.erp.modules.psi.entity;

import java.math.BigDecimal;

public class FbaPackageDto {
	private String productName;
	private String sku;
	private String fnsku;
	private Integer unitsPackage;
	private Integer shipedUnits;
	private Double packages;
	private BigDecimal  length;
	private BigDecimal  width;
	private BigDecimal  height;
	private BigDecimal  singleSize;
	private String  singleStandard;
	private BigDecimal  doubleSize;
	private String  doubleStandard;
	
	private String  unionStandard;
	private String  unionPackages;
	private Integer  boxNum;
	
	
	
	
	public FbaPackageDto() {
		super();
	}
	public FbaPackageDto(String productName, Integer unitsPackage,
			Integer shipedUnits, Double packages, BigDecimal length,
			BigDecimal width, BigDecimal height, BigDecimal singleSize,
			String singleStandard, BigDecimal doubleSize,
			String doubleStandard, String unionStandard, String unionPackages,
			Integer boxNum,String sku,String fnsku) {
		super();
		this.productName = productName;
		this.unitsPackage = unitsPackage;
		this.shipedUnits = shipedUnits;
		this.packages = packages;
		this.length = length;
		this.width = width;
		this.height = height;
		this.singleSize = singleSize;
		this.singleStandard = singleStandard;
		this.doubleSize = doubleSize;
		this.doubleStandard = doubleStandard;
		this.unionStandard = unionStandard;
		this.unionPackages = unionPackages;
		this.boxNum = boxNum;
		this.sku=sku;
		this.fnsku=fnsku;
	}
	
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Integer getUnitsPackage() {
		return unitsPackage;
	}
	public void setUnitsPackage(Integer unitsPackage) {
		this.unitsPackage = unitsPackage;
	}
	public Integer getShipedUnits() {
		return shipedUnits;
	}
	public void setShipedUnits(Integer shipedUnits) {
		this.shipedUnits = shipedUnits;
	}
	public Double getPackages() {
		return packages;
	}
	public void setPackages(Double packages) {
		this.packages = packages;
	}
	public BigDecimal getLength() {
		return length;
	}
	public void setLength(BigDecimal length) {
		this.length = length;
	}
	public BigDecimal getWidth() {
		return width;
	}
	public void setWidth(BigDecimal width) {
		this.width = width;
	}
	public BigDecimal getHeight() {
		return height;
	}
	public void setHeight(BigDecimal height) {
		this.height = height;
	}
	public BigDecimal getSingleSize() {
		return singleSize;
	}
	public void setSingleSize(BigDecimal singleSize) {
		this.singleSize = singleSize;
	}
	public String getSingleStandard() {
		return singleStandard;
	}
	public void setSingleStandard(String singleStandard) {
		this.singleStandard = singleStandard;
	}
	public BigDecimal getDoubleSize() {
		return doubleSize;
	}
	public void setDoubleSize(BigDecimal doubleSize) {
		this.doubleSize = doubleSize;
	}
	public String getDoubleStandard() {
		return doubleStandard;
	}
	public void setDoubleStandard(String doubleStandard) {
		this.doubleStandard = doubleStandard;
	}
	public String getUnionStandard() {
		return unionStandard;
	}
	public void setUnionStandard(String unionStandard) {
		this.unionStandard = unionStandard;
	}
	public String getUnionPackages() {
		return unionPackages;
	}
	public void setUnionPackages(String unionPackages) {
		this.unionPackages = unionPackages;
	}
	public Integer getBoxNum() {
		return boxNum;
	}
	public void setBoxNum(Integer boxNum) {
		this.boxNum = boxNum;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getFnsku() {
		return fnsku;
	}
	public void setFnsku(String fnsku) {
		this.fnsku = fnsku;
	}
	
}
