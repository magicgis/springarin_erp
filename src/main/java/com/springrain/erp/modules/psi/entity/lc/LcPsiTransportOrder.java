/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.SkuComparator;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 运单表Entity
 * 
 * @author Michael
 * @version 2015-01-15
 */
@Entity
@Table(name = "lc_psi_transport_order")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPsiTransportOrder {
	
	private 	Integer 	id;
	private 	String 		transportNo; 		// 运单号
	private 	String 		model; 				// 运输方式 1：sea 0:air 2:express
	private 	String 		orgin; 				// 起运港口
	private 	String 		destination; 		// 目的港口
	private 	Stock 		fromStore; 			// 发货仓库
	private 	Stock 		toStore; 			// 目的仓库
	private 	String 		bigZone;	   	    // 大区
	private 	Integer 	boxNumber; 			// 箱数
	private 	Float 		weight; 			// 重量
	private 	Float 		volume; 			// 体积
	private 	Integer 	teu; 				// 装箱记数
	private 	Float 		localAmount; 		// 本地花费
	private 	Float 		dapAmount; 			// 国外花费
	private 	Float 		tranAmount; 		// 国内运输费用
	private 	Float 		dutyTaxes; 			// 进口税
	private 	Float 		taxTaxes; 			// 关税
	private 	Float 		insuranceAmount;	// 保费
	private 	Float 		otherAmount;		// 其他费用
	
	private 	String 		localPath; 	    	// local花费凭证
	private 	String 		dapPath; 			// dap花费凭证
	private 	String 		tranPath; 			// tran花费凭证
	private 	String 		otherPath; 			// other花费凭证
	private 	String 		insurancePath; 		// insurance花费凭证
	private 	String 		taxPath; 			// tax花费凭证

	private 	Float 		unitPrice;			// 单价
	private 	Float 		payAmount1; 		// 付款1
	private 	Float 		payAmount2; 		// 付款2
	private 	Float 		payAmount3; 		// 付款3
	private 	Float 		payAmount4;	    	// 付款4
	private 	Float 		payAmount5;	    	// 付款5
	private 	Float 		payAmount6;	    	// 付款6

	private 	PsiSupplier vendor1; 			// 承运商1
	private 	PsiSupplier vendor2; 			// 承运商2
	private 	PsiSupplier vendor3; 			// 承运商3
	private 	PsiSupplier vendor4; 			// 承运商4
	private 	PsiSupplier vendor5; 			// 承运商4
	private 	PsiSupplier vendor6; 			// 承运商4
	
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

	private 	Date 		pickUpDate; 		// pickUp日期
	private 	Date 		etdDate; 			// 离港日期
	private 	Date 		etaDate; 			// 到港日期
	private 	Date 		firstEtaDate; 	    // (第一次)到港日期
	private     Date        preEtaDate;     	// 预计到港日期
	

	private 	String 		carrier;            // 飞机/船舶号
	

	private 	Date 		deliveryDate;   	// 提货日期
	private 	Date 		arrivalDate;    	// 到货日期
	private 	String 		ladingBillNo;   	// 物流单号
	private 	String 		billNo;         	// (财务付款凭证)水单号
	//private 	String 		carrier;        	// 飞机/船舶号
	private 	String 		remark; 			// 备注
	private 	String 		transportSta;   	// 运单状态
	private 	User 		createUser;	    	// 创建人
	private 	Date 		createDate; 		// 创建时间
	private 	User 		operDeliveryUser;   // 提货人
	private 	Date 		operDeliveryDate;   // 提货时间
	private 	User 		operArrivalUser; 	// 收货操作人
	private 	Date 		operArrivalDate; 	// 收货操作时间
	private		User        cancelUser; 		// 取消人
	private		Date		cancelDate; 	 	// 取消时间
	private 	String 		oceanModel; 	 	// 海运的模式
	private 	String 		oldItemIds;		 	// 产品信息oldIds
	private     String      oldContainerIds; 	//集装箱信息oldIds
	private     String      paymentSta;      	//付款状态（0，未付，1部分付款，2已付款）
	private     String      toCountry;       	//去向国；

	
	private     Integer     planeNum=1;         //几架飞机
	private     Integer     planeIndex=0;       //收了几架飞机的货
	private     Date        fbaTiminalTime;     //fba排队等待时间
	private     Date        fbaCheckingInTime;  //fba收货开始时间
	private     Date        fbaClosedTime;      //fba收货结束时间
	private     String      shipmentId;         //shipmentid
	

	
	private 	User 		operToPortUser; 
	private 	Date 		operToPortDate; 
	
	private 	User 		operFromPortUser; 
	private 	Date 		operFromPortDate; 
	
	private 	List<LcPsiTransportOrderItem>		 items; 		 // 明细
	private 	List<LcPsiTransportOrderContainer>   containerItems; // 集装箱明细
	
	private     String      transportType;      //运输类型
	private     String      destinationDetail;  //目的地详细地址
	
	private     String      elsePath;  
	
	private     Integer 	unlineOrder;
	
	private 	Date 		operArrivalFixedDate; 	// 收货操作时间(固定不变的)
	
	private     String     fbaInboundId;         //fbaInboundId
	
	private     String      isCount;
	private 	String 		changeRecord; 	      // 收货时间变更记录
	
	
	
	private     BigDecimal  declareAmount;        // 报关金额
	private     BigDecimal  taxRefundAmount;      // 退税金额
	private     String      declareNo;            // 报关单号
	private     String      exportInvoicePath;    // 出口发票
	
	private     String      confirmPay="0";     //是否确认付款，1：确认付款
	
	
	private 	Float 		otherAmount1;		// 其他费用
	private 	Float 		otherTaxes;		    // 其他税费
	private 	Float 		payAmount7;		    // 付款7
	private     Float       rate7;          	// 汇率4
	private     PsiSupplier vendor7;            // vendor7
	private     String      currency7;          // currency7
	private     String      otherPath1;         // other1花费凭证
	
	
	
	
	private 	String      suffixName;			//PI PL后缀
	private     String      cinvoiceNo;			//发票
	private 	String 		pinvoiceNo;
	private 	String 		formatDate;
	
	private     String      tranMan;            //提货人名字
	private     String      carNo;              //车牌号
	private     String      phone;              //电话
	private     String      idCard;             //身份证
	private     String      boxNo;              //海运柜号
	private     String      flowNo;             //流水号
	
	private     String      mixFile;
	
	private     Date        exportDate;         //报关出口日期
	
	private     String      invoiceFlag;
	
	private    String 		otherRemark;	    //其他金额备注
	private    String 		otherRemark1;		//其他金额1备注
	
	public String getInvoiceFlag() {
		return invoiceFlag;
	}

	public void setInvoiceFlag(String invoiceFlag) {
		this.invoiceFlag = invoiceFlag;
	}

	public String getIsCount() {
		return isCount;
	}

	public void setIsCount(String isCount) {
		this.isCount = isCount;
	}

	public Integer getUnlineOrder() {
		return unlineOrder;
	}

	public void setUnlineOrder(Integer unlineOrder) {
		this.unlineOrder = unlineOrder;
	}

	public Float getRate7() {
		return rate7;
	}

	public void setRate7(Float rate7) {
		this.rate7 = rate7;
	}

	public Date getExportDate() {
		return exportDate;
	}

	public void setExportDate(Date exportDate) {
		this.exportDate = exportDate;
	}

	public Date getFirstEtaDate() {
		return firstEtaDate;
	}

	public void setFirstEtaDate(Date firstEtaDate) {
		this.firstEtaDate = firstEtaDate;
	}

	public String getElsePath() {
		return elsePath;
	}


	public String getConfirmPay() {
		return confirmPay;
	}

	public void setConfirmPay(String confirmPay) {
		this.confirmPay = confirmPay;
	}

	public void setElsePath(String elsePath) {
		this.elsePath = elsePath;
	}


	public String getTransportType() {
		return transportType;
	}


	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}


	@ManyToOne()
	@JoinColumn(name = "oper_from_port_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getOperFromPortUser() {
		return operFromPortUser;
	}


	public void setOperFromPortUser(User operFromPortUser) {
		this.operFromPortUser = operFromPortUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getOperFromPortDate() {
		return operFromPortDate;
	}


	public void setOperFromPortDate(Date operFromPortDate) {
		this.operFromPortDate = operFromPortDate;
	}


	@ManyToOne()
	@JoinColumn(name = "oper_to_port_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getOperToPortUser() {
		return operToPortUser;
	}


	public void setOperToPortUser(User operToPortUser) {
		this.operToPortUser = operToPortUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getOperToPortDate() {
		return operToPortDate;
	}


	public void setOperToPortDate(Date operToPortDate) {
		this.operToPortDate = operToPortDate;
	}


	public String getDestinationDetail() {
		return destinationDetail;
	}


	public void setDestinationDetail(String destinationDetail) {
		this.destinationDetail = destinationDetail;
	}


	public String getSuffixName() {
		return suffixName;
	}

	public void setSuffixName(String suffixName) {
		this.suffixName = suffixName;
	}

	@Transient
	public String getFormatDate() {
		return formatDate;
	}

	public void setFormatDate(String formatDate) {
		this.formatDate = formatDate;
	}
	
	
	public Date getPickUpDate() {
		return pickUpDate;
	}

	public void setPickUpDate(Date pickUpDate) {
		this.pickUpDate = pickUpDate;
	}

	@Transient
	public String getCinvoiceNo() {
		return cinvoiceNo;
	}

	public void setCinvoiceNo(String cinvoiceNo) {
		this.cinvoiceNo = cinvoiceNo;
	}
	
	@Transient
	public String getPinvoiceNo() {
		return pinvoiceNo;
	}

	public void setPinvoiceNo(String pinvoiceNo) {
		this.pinvoiceNo = pinvoiceNo;
	}

	

	public LcPsiTransportOrder() {
		super();
	}

	public LcPsiTransportOrder(Integer id) {
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

	public void setOtherPath1Append(String otherPath1){
		if(StringUtils.isNotEmpty(this.otherPath1)){
			this.otherPath1=this.otherPath1+","+otherPath1;
		}else{
			this.otherPath1 = otherPath1;
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

	@Transient
	public String getOldItemIds() {
		return oldItemIds;
	}

	public void setOldItemIds(String oldItemIds) {
		this.oldItemIds = oldItemIds;
	}

	public Integer getPlaneIndex() {
		return planeIndex;
	}

	public void setPlaneIndex(Integer planeIndex) {
		this.planeIndex = planeIndex;
	}

	public String getToCountry() {
		return toCountry;
	}

	public void setToCountry(String toCountry) {
		this.toCountry = toCountry;
	}

	@OneToMany(mappedBy = "transportOrder", fetch = FetchType.EAGER)
	@Where(clause = "del_flag=0")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.ALL)
	public List<LcPsiTransportOrderItem> getItems() {
		return items;
	}

	public void setItems(List<LcPsiTransportOrderItem> items) {
		this.items = items;
	}

	
	@Transient
	public List<LcPsiTransportOrderItem> getViewItems(){
		List<LcPsiTransportOrderItem> views = Lists.newArrayList();
		 views.addAll(items);
		 Collections.sort(views,new SkuComparator()); 
		 return views;
	}
	
	
	@OneToMany(mappedBy = "transportOrder", fetch = FetchType.LAZY)
	@Where(clause = "del_flag=0")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.ALL)
	public List<LcPsiTransportOrderContainer> getContainerItems() {
		return containerItems;
	}

	public void setContainerItems(List<LcPsiTransportOrderContainer> containerItems) {
		this.containerItems = containerItems;
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

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public Float getInsuranceAmount() {
		return insuranceAmount;
	}

	public void setInsuranceAmount(Float insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}

	public String getPaymentSta() {
		return paymentSta;
	}

	public void setPaymentSta(String paymentSta) {
		this.paymentSta = paymentSta;
	}

	public String getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(String shipmentId) {
		this.shipmentId = shipmentId;
	}

	@Transient
	public String getOldContainerIds() {
		return oldContainerIds;
	}

	public void setOldContainerIds(String oldContainerIds) {
		this.oldContainerIds = oldContainerIds;
	}

	public String getTransportNo() {
		return transportNo;
	}

	public void setTransportNo(String transportNo) {
		this.transportNo = transportNo;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
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

	public String getBigZone() {
		return bigZone;
	}

	public void setBigZone(String bigZone) {
		this.bigZone = bigZone;
	}

	public String getOtherRemark() {
		return otherRemark;
	}

	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
	}

	public String getOtherRemark1() {
		return otherRemark1;
	}

	public void setOtherRemark1(String otherRemark1) {
		this.otherRemark1 = otherRemark1;
	}

	@ManyToOne()
	@JoinColumn(name = "fromStore")
	@NotFound(action = NotFoundAction.IGNORE)
	public Stock getFromStore() {
		return fromStore;
	}

	public void setFromStore(Stock fromStore) {
		this.fromStore = fromStore;
	}

	@ManyToOne()
	@JoinColumn(name = "toStore")
	@NotFound(action = NotFoundAction.IGNORE)
	public Stock getToStore() {
		return toStore;
	}

	public void setToStore(Stock toStore) {
		this.toStore = toStore;
	}

	public Integer getBoxNumber() {
		return boxNumber;
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

	public Integer getTeu() {
		return teu;
	}

	public void setTeu(Integer teu) {
		this.teu = teu;
	}

	public Float getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Float unitPrice) {
		this.unitPrice = unitPrice;
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

	@Transient
	public Float getTotalAmount() {
		Float total=0f;
		if(this.localAmount!=null){
			total+=this.localAmount*this.rate1;
		}
		if(this.tranAmount!=null){
			total+=this.tranAmount*this.rate2;
		}
		if(this.dapAmount!=null){
			total+=this.dapAmount*this.rate3;
		}
		if(this.otherAmount!=null){
			total+=this.otherAmount*this.rate4;
		}
		return total;
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

	public String getOceanModel() {
		return oceanModel;
	}

	public void setOceanModel(String oceanModel) {
		this.oceanModel = oceanModel;
	}

	public String getCarrier() {
		return carrier;
	}


	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}


	public Float getPayAmount1() {
		return payAmount1;
	}

	public void setPayAmount1(Float payAmount1) {
		this.payAmount1 = payAmount1;
	}

	public Float getPayAmount2() {
		return payAmount2;
	}

	public void setPayAmount2(Float payAmount2) {
		this.payAmount2 = payAmount2;
	}

	public Float getPayAmount3() {
		return payAmount3;
	}

	public void setPayAmount3(Float payAmount3) {
		this.payAmount3 = payAmount3;
	}

	public Float getPayAmount4() {
		return payAmount4;
	}

	public void setPayAmount4(Float payAmount4) {
		this.payAmount4 = payAmount4;
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
	

	public Date getPreEtaDate() {
		return preEtaDate;
	}

	public void setPreEtaDate(Date preEtaDate) {
		this.preEtaDate = preEtaDate;
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

	public Float getOtherAmount() {
		return otherAmount;
	}

	public void setOtherAmount(Float otherAmount) {
		this.otherAmount = otherAmount;
	}

	public Float getPayAmount5() {
		return payAmount5;
	}

	public void setPayAmount5(Float payAmount5) {
		this.payAmount5 = payAmount5;
	}

	public Float getPayAmount6() {
		return payAmount6;
	}

	public void setPayAmount6(Float payAmount6) {
		this.payAmount6 = payAmount6;
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

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEtdDate() {
		return etdDate;
	}

	public void setEtdDate(Date etdDate) {
		this.etdDate = etdDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEtaDate() {
		return etaDate;
	}

	public void setEtaDate(Date etaDate) {
		this.etaDate = etaDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	@Transient
	public Integer getTransitDays() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		//在途天数
		int transitDays = DateUtils.subtraction(sdf.format(this.etdDate), sdf.format(this.etaDate));
		return transitDays;
	}


	public String getLadingBillNo() {
		return ladingBillNo;
	}

	public void setLadingBillNo(String ladingBillNo) {
		this.ladingBillNo = ladingBillNo;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}


	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTransportSta() {
		return transportSta;
	}

	public void setTransportSta(String transportSta) {
		this.transportSta = transportSta;
	}
	
	@Transient
	public Integer getTandT(){
		if(pickUpDate!=null&&operArrivalDate!=null){
			return (int) DateUtils.spaceDays(pickUpDate, operArrivalDate)+1;
		}else{
			return null;
		}
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	@Column(updatable=false)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getOperDeliveryDate() {
		return operDeliveryDate;
	}

	public void setOperDeliveryDate(Date operDeliveryDate) {
		this.operDeliveryDate = operDeliveryDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getOperArrivalDate() {
		return operArrivalDate;
	}

	public void setOperArrivalDate(Date operArrivalDate) {
		this.operArrivalDate = operArrivalDate;
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

	@ManyToOne()
	@JoinColumn(name = "oper_delivery_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getOperDeliveryUser() {
		return operDeliveryUser;
	}

	public void setOperDeliveryUser(User operDeliveryUser) {
		this.operDeliveryUser = operDeliveryUser;
	}

	@ManyToOne()
	@JoinColumn(name = "oper_arrival_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getOperArrivalUser() {
		return operArrivalUser;
	}

	public void setOperArrivalUser(User operArrivalUser) {
		this.operArrivalUser = operArrivalUser;
	}
	
	public String toJson() {
		String arrDateStr="";
		Date arrDate=this.etaDate==null?this.preEtaDate:this.etaDate;
		if(arrDate!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			arrDateStr=sdf.format(arrDate);
		}
		StringBuilder rs = new StringBuilder("");
		String reg = "[^0-9a-zA-Z\u4e00-\u9fa5]+";
		rs.append("\"boxNumber\":\"").append(this.boxNumber).append("\",\"arrDate\":\""+arrDateStr).append("\",\"remark\":\""+(StringUtils.isEmpty(remark)?"":remark.replaceAll(reg,""))).append("\",\"items\":[");
		List<LcPsiTransportOrderItem> viewItems = getViewItems();
		for (LcPsiTransportOrderItem item : viewItems) {
			rs.append(item.toString()).append(",");
		}
		String rsStr = rs.toString();
		if(rsStr.length()>1){
			rsStr = rsStr.substring(0,rsStr.length()-1);
		}
		rsStr+="]";
		return rsStr;
	}

	public Integer getPlaneNum() {
		return planeNum;
	}

	public void setPlaneNum(Integer planeNum) {
		this.planeNum = planeNum;
	}

	public Date getFbaTiminalTime() {
		return fbaTiminalTime;
	}

	public void setFbaTiminalTime(Date fbaTiminalTime) {
		this.fbaTiminalTime = fbaTiminalTime;
	}

	public Date getFbaCheckingInTime() {
		return fbaCheckingInTime;
	}

	public void setFbaCheckingInTime(Date fbaCheckingInTime) {
		this.fbaCheckingInTime = fbaCheckingInTime;
	}

	public Date getFbaClosedTime() {
		return fbaClosedTime;
	}

	public void setFbaClosedTime(Date fbaClosedTime) {
		this.fbaClosedTime = fbaClosedTime;
	}
	
	
	public Date getOperArrivalFixedDate() {
		return operArrivalFixedDate;
	}

	public void setOperArrivalFixedDate(Date operArrivalFixedDate) {
		this.operArrivalFixedDate = operArrivalFixedDate;
	}

	public String getFbaInboundId() {
		return fbaInboundId;
	}

	public void setFbaInboundId(String fbaInboundId) {
		this.fbaInboundId = fbaInboundId;
	}

	public String getChangeRecord() {
		return changeRecord;
	}

	public void setChangeRecord(String changeRecord) {
		this.changeRecord = changeRecord;
	}

	@Transient
	public boolean getCanChangeModel(){
		boolean flag = true;
		//只要一个不为空，就不能修改运输模式
		if(this.localAmount!=null||this.tranAmount!=null||this.dapAmount!=null||this.otherAmount!=null||this.otherAmount1!=null||this.insuranceAmount!=null){
			flag=false;
		}
		return flag;
	}

	public BigDecimal getDeclareAmount() {
		return declareAmount;
	}

	public void setDeclareAmount(BigDecimal declareAmount) {
		this.declareAmount = declareAmount;
	}

	public BigDecimal getTaxRefundAmount() {
		return taxRefundAmount;
	}

	public void setTaxRefundAmount(BigDecimal taxRefundAmount) {
		this.taxRefundAmount = taxRefundAmount;
	}

	public String getDeclareNo() {
		return declareNo;
	}

	public void setDeclareNo(String declareNo) {
		this.declareNo = declareNo;
	}

	public String getExportInvoicePath() {
		return exportInvoicePath;
	}

	public void setExportInvoicePath(String exportInvoicePath) {
		this.exportInvoicePath = exportInvoicePath;
	}
	
	//获得报关资料
	@Transient
	public String getExpInvoice(){
		if(StringUtils.isNotEmpty(exportInvoicePath)){
			return transportNo+"/export/"+exportInvoicePath;
		}
		return "";
	}
	@Transient
	public String getExpCD(){
		if(StringUtils.isEmpty(suffixName)){
			return null;
		}
		String arr[]=suffixName.split("-");
		if(arr.length>8&&!"CD".equals(arr[7])){
			return transportNo+"/"+transportNo+"_CD"+arr[7];
		}
		return "";
	}
	@Transient
	public String getExpCS(){
		if(StringUtils.isEmpty(suffixName)){
			return null;
		}
		String arr[]=suffixName.split("-");
		if(arr.length>9&&!"CS".equals(arr[8])){
			return transportNo+"/"+transportNo+"_CS"+arr[8];
		}
		return "";
	}
	
	/**
	 * 是否有未建FBA贴的item 0:否  1：是
	 * @return 0:不能再创建FBA贴了  1：可以
	 */
	@Transient
	public String getIsCreateFba(){
		if (items == null) {
			return "0";
		}
		for (LcPsiTransportOrderItem item : items) {
			if (item.getFbaFlag() != null && "0".equals(item.getFbaFlag()) && "0".equals(item.getOfflineSta())) {
				return "1";
			}
		}
		return "0";
	}
	
	/**
	 * 是否可以拆单
	 * @return 0:不能拆单 1:可以拆单
	 */
	@Transient
	public String getCanSplit(){
		if (items == null) {
			return "0";
		}
		for (LcPsiTransportOrderItem item : items) {
			if (item.getFbaFlag() != null && "1".equals(item.getFbaFlag())) {
				return "0";	//只要有建贴的就不允许拆单
			}
		}
		return "1";
	}
	
	@Transient
	public boolean getHasNew(){
		for(LcPsiTransportOrderItem item:items){
			if(item.getProductColorCountry().equals(item.getSku())){
				return true;
			}
		}
		return false;
	}
	
	@Transient
	public Integer getHasElectricQuantity(){
		Integer quantity=0;
		for(LcPsiTransportOrderItem item:items){
			//item.product.hasElectric eq '1'  带电
			if("1".equals(item.getProduct().getHasElectric())){
				quantity+=MathUtils.roundUp(item.getQuantity()*1.0d/item.getPackQuantity());
			}
		}
		return quantity;
	}
	
	public void appendFbaId(String newFbaId){
		if(StringUtils.isEmpty(fbaInboundId)){
			fbaInboundId=newFbaId;
		}else{
			fbaInboundId=fbaInboundId+","+newFbaId;
		}
	}

	
	public Float getOtherTaxes() {
		return otherTaxes;
	}

	public void setOtherTaxes(Float otherTaxes) {
		this.otherTaxes = otherTaxes;
	}

	public Float getPayAmount7() {
		return payAmount7;
	}

	public void setPayAmount7(Float payAmount7) {
		this.payAmount7 = payAmount7;
	}

	@ManyToOne()
	@JoinColumn(name = "vendor7")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplier getVendor7() {
		return vendor7;
	}

	public void setVendor7(PsiSupplier vendor7) {
		this.vendor7 = vendor7;
	}

	public String getCurrency7() {
		return currency7;
	}

	public void setCurrency7(String currency7) {
		this.currency7 = currency7;
	}

	public Float getOtherAmount1() {
		return otherAmount1;
	}

	public void setOtherAmount1(Float otherAmount1) {
		this.otherAmount1 = otherAmount1;
	}

	public String getOtherPath1() {
		return otherPath1;
	}

	public void setOtherPath1(String otherPath1) {
		this.otherPath1 = otherPath1;
	}

	
	@Transient
	public String getTranMan() {
		return tranMan;
	}

	public void setTranMan(String tranMan) {
		this.tranMan = tranMan;
	}

	@Transient
	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	@Transient
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Transient
	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	@Transient
	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}

	@Transient
	public String getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	@Transient
	public String getModelStr(){   
		if("0".equals(model)){
			return "空运";
		}else if("1".equals(model)){
			return "海运";
		}else if("2".equals(model)){
			return "快递";
		}else if("3".equals(model)){
			return "铁路";
		}else if("4".equals(model)){
			return "公路";
		}else{
			return "";
		}
	}

	//解决打印出库单异常(反射找不到set方法)
	@Transient
	public void setModelStr(String modelStr){
		
	}

	public String getMixFile() {
		return mixFile;
	}

	public void setMixFile(String mixFile) {
		this.mixFile = mixFile;
	}

	/**
	 * 进口金额总计
	 * @return
	 */
	@Transient
	public Double getTotalImportAmount() {
		BigDecimal totalMoney = new BigDecimal(0);
		for (LcPsiTransportOrderItem item : items) {
			if (item.getImportPrice() != null && item.getQuantity() != null) {
				totalMoney = totalMoney.add(new BigDecimal(item.getImportPrice()).setScale(2,4).multiply(new BigDecimal(item.getQuantity())));
			}
		}
		return totalMoney.setScale(2,4).doubleValue();
	}
}
