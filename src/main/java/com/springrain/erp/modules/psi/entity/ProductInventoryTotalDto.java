package com.springrain.erp.modules.psi.entity;


/**
 * 产品下单计算相关信息汇总
 */
public class ProductInventoryTotalDto  {
	
	private Integer producting = 0; 	// 在产数据
	private Integer cn = 0; 	// 中国仓
	private Integer transportting = 0; 	// 在途数据
	private Integer overseas = 0; 		// 海外仓
	private Integer fbas = 0; 			// fba库存
	private Integer total = 0; 			// 总库存
	private String adjust;			// 0:不可调  1 可调（不需要下单并且在产或中国仓有货）
	
	private double daySaleForecast = 0;	//采购期预日销
	private double daySale31 = 0;	//31日销计算日销
	private double saleForecast = 0;//预测数据计算销售期预月销
	private double saleDay31Sales = 0;//31日销计算销售期预月销
	private double xiadanForecast = 0;	//判断是否下单的销量
	private double xiadanDay31Sales = 0;	//判断是否下单的销量
	private double balanceForecast = 0;	//预测数据计算结余
	private double balanceDay31Sales = 0;	//31日销计算结余

	private Integer packQuantity=0; 	// 装箱个数
	private Integer adjustNum=0; 		// 可调剂数量
	

	public ProductInventoryTotalDto() {
		super();
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

	public Integer getTransportting() {
		return transportting;
	}

	public void setTransportting(Integer transportting) {
		this.transportting = transportting;
	}

	public Integer getOverseas() {
		return overseas;
	}

	public void setOverseas(Integer overseas) {
		this.overseas = overseas;
	}

	public Integer getFbas() {
		return fbas;
	}

	public void setFbas(Integer fbas) {
		this.fbas = fbas;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public String getAdjust() {
		return adjust;
	}

	public void setAdjust(String adjust) {
		this.adjust = adjust;
	}

	public double getSaleForecast() {
		return saleForecast;
	}

	public void setSaleForecast(double saleForecast) {
		this.saleForecast = saleForecast;
	}

	public double getSaleDay31Sales() {
		return saleDay31Sales;
	}

	public void setSaleDay31Sales(double saleDay31Sales) {
		this.saleDay31Sales = saleDay31Sales;
	}

	public double getXiadanForecast() {
		return xiadanForecast;
	}

	public void setXiadanForecast(double xiadanForecast) {
		this.xiadanForecast = xiadanForecast;
	}

	public double getXiadanDay31Sales() {
		return xiadanDay31Sales;
	}

	public void setXiadanDay31Sales(double xiadanDay31Sales) {
		this.xiadanDay31Sales = xiadanDay31Sales;
	}

	public double getBalanceForecast() {
		return balanceForecast;
	}

	public void setBalanceForecast(double balanceForecast) {
		this.balanceForecast = balanceForecast;
	}

	public double getBalanceDay31Sales() {
		return balanceDay31Sales;
	}

	public void setBalanceDay31Sales(double balanceDay31Sales) {
		this.balanceDay31Sales = balanceDay31Sales;
	}

	public double getDaySaleForecast() {
		return daySaleForecast;
	}

	public void setDaySaleForecast(double daySaleForecast) {
		this.daySaleForecast = daySaleForecast;
	}

	public double getDaySale31() {
		return daySale31;
	}

	public void setDaySale31(double daySale31) {
		this.daySale31 = daySale31;
	}

	public Integer getPackQuantity() {
		return packQuantity;
	}

	public void setPackQuantity(Integer packQuantity) {
		this.packQuantity = packQuantity;
	}

	public Integer getAdjustNum() {
		return adjustNum;
	}

	public void setAdjustNum(Integer adjustNum) {
		this.adjustNum = adjustNum;
	}

	
}


