/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.modules.sys.entity.User;

/**
 * 待发货表Entity
 * @author Michael
 * @version 2015-01-15
 */
@Entity
@Table(name = "psi_transport_order_replace")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiTransportOrderReplace {
	
	private 	Integer 	id;
	private     String      shipperInfo;        // 发货信息
	private 	String 		replaceSta; 		// 代发货物流状态
	private 	String 		carrier;            // 飞机/船舶号
	
	private 	String 		transportNo; 		// 运单号
	private 	String 		orgin; 				// 起运港口
	private 	String 		destination; 		// 目的港口
	private 	Integer 	boxNumber; 			// 箱数
	private 	Float 		weight; 			// 重量
	private 	Float 		volume; 			// 体积
	
	private 	Date 		etdDate; 			// 离港日期
	private 	Date 		etaDate; 			// 到港日期
	private 	Date 		arrivalDate;    	// 到货日期
	private 	String 		ladingBillNo;   	// 物流单号
	private 	String 		remark;         	// 备注
	private 	String 		suffixName; 		// 后缀名
	
	private 	String 		localPath; 	    	// local花费凭证
	private 	String 		dapPath; 			// dap花费凭证
	private 	String 		tranPath; 			// tran花费凭证
	private 	String 		otherPath; 			// other花费凭证
	private 	String 		insurancePath; 		// insurance花费凭证
	private 	String 		taxPath; 			// tax花费凭证
	

	private 	PsiSupplier vendor1; 			// 承运商1
	private 	PsiSupplier vendor2; 			// 承运商2
	private 	PsiSupplier vendor3; 			// 承运商3
	private 	PsiSupplier vendor4; 			// 承运商4
	private 	PsiSupplier vendor5; 			// 承运商4
	private 	PsiSupplier vendor6; 			// 承运商4
	
	
	private 	Float 		localAmount; 		// 本地花费
	private 	Float 		dapAmount; 			// 国外花费
	private 	Float 		tranAmount; 		// 国内运输费用
	private 	Float 		dutyTaxes; 			// 进口税
	private 	Float 		taxTaxes; 			// 关税
	private 	Float 		insuranceAmount;	// 保费
	private 	Float 		otherAmount;		// 其他费用
	
	private 	String 		currency1; 			// 货币种类1
	private 	String 		currency2; 			// 货币种类2
	private 	String 		currency3; 			// 货币种类3
	private 	String 		currency4; 			// 货币种类4
	private 	String 		currency5; 			// 货币种类5
	private 	String 		currency6; 			// 货币种类6
	
	private     Float       rate1;          	// 汇率1
	private     Float       rate2;          	// 汇率2
	private     Float       rate3;          	// 汇率3
	private     Float       rate4;          	// 汇率4

	private 	Float 		totalAmount;		// 总费用
	
	private 	Float 		localAmountIn; 	// 本地花费
	private 	Float 		dapAmountIn; 		// 国外花费
	private 	Float 		tranAmountIn; 		// 国内运输费用
	private 	Float 		dutyTaxesIn; 		// 进口税
	private 	Float 		taxTaxesIn; 		// 关税
	private 	Float 		insuranceAmountIn;	// 保费
	private 	Float 		otherAmountIn;		// 其他费用
	
	
	
	private 	String 		currencyIn1; 		// 货币种类1
	private 	String 		currencyIn2; 		// 货币种类2
	private 	String 		currencyIn3; 		// 货币种类3
	private 	String 		currencyIn4; 		// 货币种类4
	private 	String 		currencyIn5; 		// 货币种类5
	private 	String 		currencyIn6; 		// 货币种类6
	
	private     Float       rateIn1;          	// 汇率1
	private     Float       rateIn2;          	// 汇率2
	private     Float       rateIn3;          	// 汇率3
	private     Float       rateIn4;          	// 汇率4
	
	
	private     User        createUser;         // 创建人
	private     Date        createDate;         // 创建时间
	private     User        cancelUser;         // 取消人
	private     Date        cancelDate;         // 创建时间
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne()
	@JoinColumn(name = "create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public String getReplaceSta() {
		return replaceSta;
	}
	public void setReplaceSta(String replaceSta) {
		this.replaceSta = replaceSta;
	}
	public Float getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}
	@ManyToOne()
	@JoinColumn(name = "cancel_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCancelUser() {
		return cancelUser;
	}

	public void setCancelUser(User cancelUser) {
		this.cancelUser = cancelUser;
	}
	
	
	public String getShipperInfo() {
		return shipperInfo;
	}
	public void setShipperInfo(String shipperInfo) {
		this.shipperInfo = shipperInfo;
	}
	public String getTransportNo() {
		return transportNo;
	}
	public void setTransportNo(String transportNo) {
		this.transportNo = transportNo;
	}
	public String getOrgin() {
		return orgin;
	}
	public void setOrgin(String orgin) {
		this.orgin = orgin;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public Integer getBoxNumber() {
		return boxNumber;
	}
	
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getCancelDate() {
		return cancelDate;
	}
	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getSuffixName() {
		return suffixName;
	}
	public void setSuffixName(String suffixName) {
		this.suffixName = suffixName;
	}
	public void setBoxNumber(Integer boxNumber) {
		this.boxNumber = boxNumber;
	}
	public Float getWeight() {
		return weight;
	}
	public void setWeight(Float weight) {
		this.weight = weight;
	}
	public Float getVolume() {
		return volume;
	}
	public void setVolume(Float volume) {
		this.volume = volume;
	}
	public Date getEtdDate() {
		return etdDate;
	}
	public void setEtdDate(Date etdDate) {
		this.etdDate = etdDate;
	}
	public Date getEtaDate() {
		return etaDate;
	}
	public void setEtaDate(Date etaDate) {
		this.etaDate = etaDate;
	}
	public Date getArrivalDate() {
		return arrivalDate;
	}
	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}
	public String getLadingBillNo() {
		return ladingBillNo;
	}
	public void setLadingBillNo(String ladingBillNo) {
		this.ladingBillNo = ladingBillNo;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public String getDapPath() {
		return dapPath;
	}
	public void setDapPath(String dapPath) {
		this.dapPath = dapPath;
	}
	public String getTranPath() {
		return tranPath;
	}
	public void setTranPath(String tranPath) {
		this.tranPath = tranPath;
	}
	public String getOtherPath() {
		return otherPath;
	}
	public void setOtherPath(String otherPath) {
		this.otherPath = otherPath;
	}
	public String getInsurancePath() {
		return insurancePath;
	}
	public void setInsurancePath(String insurancePath) {
		this.insurancePath = insurancePath;
	}
	public String getTaxPath() {
		return taxPath;
	}
	public void setTaxPath(String taxPath) {
		this.taxPath = taxPath;
	}
	
	@ManyToOne()
	@JoinColumn(name = "vendor1")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplier getVendor1() {
		return vendor1;
	}
	public void setVendor1(PsiSupplier vendor1) {
		this.vendor1 = vendor1;
	}
	
	@ManyToOne()
	@JoinColumn(name = "vendor2")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplier getVendor2() {
		return vendor2;
	}
	public void setVendor2(PsiSupplier vendor2) {
		this.vendor2 = vendor2;
	}
	@ManyToOne()
	@JoinColumn(name = "vendor3")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplier getVendor3() {
		return vendor3;
	}
	public void setVendor3(PsiSupplier vendor3) {
		this.vendor3 = vendor3;
	}
	
	@ManyToOne()
	@JoinColumn(name = "vendor4")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplier getVendor4() {
		return vendor4;
	}
	public void setVendor4(PsiSupplier vendor4) {
		this.vendor4 = vendor4;
	}
	@ManyToOne()
	@JoinColumn(name = "vendor5")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplier getVendor5() {
		return vendor5;
	}
	public void setVendor5(PsiSupplier vendor5) {
		this.vendor5 = vendor5;
	}
	
	@ManyToOne()
	@JoinColumn(name = "vendor6")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplier getVendor6() {
		return vendor6;
	}
	public void setVendor6(PsiSupplier vendor6) {
		this.vendor6 = vendor6;
	}
	public Float getLocalAmount() {
		return localAmount;
	}
	public void setLocalAmount(Float localAmount) {
		this.localAmount = localAmount;
	}
	public Float getDapAmount() {
		return dapAmount;
	}
	public void setDapAmount(Float dapAmount) {
		this.dapAmount = dapAmount;
	}
	public Float getTranAmount() {
		return tranAmount;
	}
	public void setTranAmount(Float tranAmount) {
		this.tranAmount = tranAmount;
	}
	public Float getDutyTaxes() {
		return dutyTaxes;
	}
	public void setDutyTaxes(Float dutyTaxes) {
		this.dutyTaxes = dutyTaxes;
	}
	public Float getTaxTaxes() {
		return taxTaxes;
	}
	public void setTaxTaxes(Float taxTaxes) {
		this.taxTaxes = taxTaxes;
	}
	public Float getInsuranceAmount() {
		return insuranceAmount;
	}
	public void setInsuranceAmount(Float insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}
	public Float getOtherAmount() {
		return otherAmount;
	}
	public void setOtherAmount(Float otherAmount) {
		this.otherAmount = otherAmount;
	}
	public String getCurrency1() {
		return currency1;
	}
	public void setCurrency1(String currency1) {
		this.currency1 = currency1;
	}
	public String getCurrency2() {
		return currency2;
	}
	public void setCurrency2(String currency2) {
		this.currency2 = currency2;
	}
	public String getCurrency3() {
		return currency3;
	}
	public void setCurrency3(String currency3) {
		this.currency3 = currency3;
	}
	public String getCurrency4() {
		return currency4;
	}
	public void setCurrency4(String currency4) {
		this.currency4 = currency4;
	}
	public String getCurrency5() {
		return currency5;
	}
	public void setCurrency5(String currency5) {
		this.currency5 = currency5;
	}
	public String getCurrency6() {
		return currency6;
	}
	public void setCurrency6(String currency6) {
		this.currency6 = currency6;
	}
	public Float getRate1() {
		return rate1;
	}
	public void setRate1(Float rate1) {
		this.rate1 = rate1;
	}
	public Float getRate2() {
		return rate2;
	}
	public void setRate2(Float rate2) {
		this.rate2 = rate2;
	}
	public Float getRate3() {
		return rate3;
	}
	public void setRate3(Float rate3) {
		this.rate3 = rate3;
	}
	public Float getRate4() {
		return rate4;
	}
	public void setRate4(Float rate4) {
		this.rate4 = rate4;
	}
	public Float getLocalAmountIn() {
		return localAmountIn;
	}
	public void setLocalAmountIn(Float localAmountIn) {
		this.localAmountIn = localAmountIn;
	}
	public Float getDapAmountIn() {
		return dapAmountIn;
	}
	public void setDapAmountIn(Float dapAmountIn) {
		this.dapAmountIn = dapAmountIn;
	}
	public Float getTranAmountIn() {
		return tranAmountIn;
	}
	public void setTranAmountIn(Float tranAmountIn) {
		this.tranAmountIn = tranAmountIn;
	}
	public Float getDutyTaxesIn() {
		return dutyTaxesIn;
	}
	public void setDutyTaxesIn(Float dutyTaxesIn) {
		this.dutyTaxesIn = dutyTaxesIn;
	}
	public Float getTaxTaxesIn() {
		return taxTaxesIn;
	}
	public void setTaxTaxesIn(Float taxTaxesIn) {
		this.taxTaxesIn = taxTaxesIn;
	}
	public Float getInsuranceAmountIn() {
		return insuranceAmountIn;
	}
	public void setInsuranceAmountIn(Float insuranceAmountIn) {
		this.insuranceAmountIn = insuranceAmountIn;
	}
	public Float getOtherAmountIn() {
		return otherAmountIn;
	}
	public void setOtherAmountIn(Float otherAmountIn) {
		this.otherAmountIn = otherAmountIn;
	}
	public String getCurrencyIn1() {
		return currencyIn1;
	}
	public void setCurrencyIn1(String currencyIn1) {
		this.currencyIn1 = currencyIn1;
	}
	public String getCurrencyIn2() {
		return currencyIn2;
	}
	public void setCurrencyIn2(String currencyIn2) {
		this.currencyIn2 = currencyIn2;
	}
	public String getCurrencyIn3() {
		return currencyIn3;
	}
	public void setCurrencyIn3(String currencyIn3) {
		this.currencyIn3 = currencyIn3;
	}
	public String getCurrencyIn4() {
		return currencyIn4;
	}
	public void setCurrencyIn4(String currencyIn4) {
		this.currencyIn4 = currencyIn4;
	}
	public String getCurrencyIn5() {
		return currencyIn5;
	}
	public void setCurrencyIn5(String currencyIn5) {
		this.currencyIn5 = currencyIn5;
	}
	public String getCurrencyIn6() {
		return currencyIn6;
	}
	public String getCarrier() {
		return carrier;
	}
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	public void setCurrencyIn6(String currencyIn6) {
		this.currencyIn6 = currencyIn6;
	}
	public Float getRateIn1() {
		return rateIn1;
	}
	public void setRateIn1(Float rateIn1) {
		this.rateIn1 = rateIn1;
	}
	public Float getRateIn2() {
		return rateIn2;
	}
	public void setRateIn2(Float rateIn2) {
		this.rateIn2 = rateIn2;
	}
	public Float getRateIn3() {
		return rateIn3;
	}
	public void setRateIn3(Float rateIn3) {
		this.rateIn3 = rateIn3;
	}
	public Float getRateIn4() {
		return rateIn4;
	}
	public void setRateIn4(Float rateIn4) {
		this.rateIn4 = rateIn4;
	}
	
	public void setLocalPathAppend(String localPath){
		if(StringUtils.isNotEmpty(this.localPath)){
			this.localPath=this.localPath+","+localPath;
		}else{
			this.localPath = localPath;
		}
	}
	
	public void setDapPathAppend(String dapPath){
		if(StringUtils.isNotEmpty(this.dapPath)){
			this.dapPath=this.dapPath+","+dapPath;
		}else{
			this.dapPath = dapPath;
		}
	}
	
	public void setTranPathAppend(String tranPath){
		if(StringUtils.isNotEmpty(this.tranPath)){
			this.tranPath=this.tranPath+","+tranPath;
		}else{
			this.tranPath = tranPath;
		}
	}
	
	public void setOtherPathAppend(String otherPath){
		if(StringUtils.isNotEmpty(this.otherPath)){
			this.otherPath=this.otherPath+","+otherPath;
		}else{
			this.otherPath = otherPath;
		}
	}

	public void setInsurancePathAppend(String insurancePath){
		if(StringUtils.isNotEmpty(this.insurancePath)){
			this.insurancePath=this.insurancePath+","+insurancePath;
		}else{
			this.insurancePath = insurancePath;
		}
	}
	
	public void setTaxPathAppend(String taxPath){
		if(StringUtils.isNotEmpty(this.taxPath)){
			this.taxPath=this.taxPath+","+taxPath;
		}else{
			this.taxPath = taxPath;
		}
	}
	

}
