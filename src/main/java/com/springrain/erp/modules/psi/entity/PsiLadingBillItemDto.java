/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.math.BigDecimal;

import javax.persistence.Transient;

import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.entity.lc.LcPsiLadingBillItem;



/**
 * 提单明细Entity
 * @author Michael
 * @version 2014-11-11
 */
public class PsiLadingBillItemDto{
	
	private 	Integer              id; 		
	private 	String 				 productName;        // 产品名
	private 	String 				 colorCode;          // 颜色
	private 	String 				 countryCode;        // 国家
	private 	String     			 remark;             // 备注
	private 	Integer              canLadingTotal;     // 可提货总数
	private 	String               productConName;     // 产品名
	private 	Integer              oldQuantityLading;  // 原提单数量
	private     BigDecimal           itemPrice;          // 单价    
	private     String               sku;                // sku
	private     String               partsTimelyInfo;    // 实时配件信息
	
	private 	Integer 			 quantityLading;     // 提货数量
	private 	Integer 			 quantitySure;       // 确认数量
	private 	Integer 			 quantitySureTemp;   // 本次确认数量
	private 	Integer 			 quantitySpares;     // 备品数量
	
	private 	Integer 			 purchaseOrderItemId; //订单itemId
	private     Integer              purchaseOrderId;     // 订单id
	private     String               purchaseOrderNo;     // 订单no
	
	private     Integer              packQuantity;        // 装箱数
	private     String               isPass;              // 通过
	private 	Integer 			 quantityOffLading;   // 提货数量
	private 	BigDecimal           totalPaymentPreAmount; //已申请总额
	private 	BigDecimal           totalPaymentAmount;    //已支付总额
	private 	BigDecimal           totalAmount;           //提单总额
	private 	Integer 			 balanceRate1;       	//尾款首次付款比例
	private 	Integer 			 balanceDelay1;      	//第一次付款延迟几天
	private 	Integer 			 balanceRate2;       	//尾款第二次付款比例
	private 	Integer 			 balanceDelay2;      	//第二次付款延迟几天
	private     Integer              deposit;               //定金比例
	private     BigDecimal           payDepositAmount;      //支付定金金额
	
	private 	String 				 hisRecord; 			// 验货记录
	private 	Integer 			 quantityGoods; 		// 已验货总数
	private     String               poDeliveryDate;        //po收货日期
	private     String               isTestOver;            //是否品检通过，通过为1，暂时未通过为0，未品检为null   5为产品经理通过
	private     Float                volume;                //大箱体积

	public PsiLadingBillItemDto() {
		super();
	}

	public PsiLadingBillItemDto(Integer id){
		this();
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public Float getVolume() {
		return volume;
	}

	public void setVolume(Float volume) {
		this.volume = volume;
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

	public String getColorCode() {
		return colorCode;
	}

	public String getPoDeliveryDate() {
		return poDeliveryDate;
	}

	public void setPoDeliveryDate(String poDeliveryDate) {
		this.poDeliveryDate = poDeliveryDate;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	
	public Integer getDeposit() {
		return deposit;
	}

	public void setDeposit(Integer deposit) {
		this.deposit = deposit;
	}

	public BigDecimal getPayDepositAmount() {
		return payDepositAmount;
	}

	public void setPayDepositAmount(BigDecimal payDepositAmount) {
		this.payDepositAmount = payDepositAmount;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Integer getQuantityOffLading() {
		return quantityOffLading;
	}

	public void setQuantityOffLading(Integer quantityOffLading) {
		this.quantityOffLading = quantityOffLading;
	}

	public Integer getPackQuantity() {
		return packQuantity;
	}

	public void setPackQuantity(Integer packQuantity) {
		this.packQuantity = packQuantity;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getCanLadingTotal() {
		return canLadingTotal;
	}

	public void setCanLadingTotal(Integer canLadingTotal) {
		this.canLadingTotal = canLadingTotal;
	}

	public String getProductConName() {
		return productConName;
	}

	public String getIsPass() {
		return isPass;
	}

	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}

	public void setProductConName(String productConName) {
		this.productConName = productConName;
	}

	public Integer getOldQuantityLading() {
		return oldQuantityLading;
	}

	public void setOldQuantityLading(Integer oldQuantityLading) {
		this.oldQuantityLading = oldQuantityLading;
	}

	public BigDecimal getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getPartsTimelyInfo() {
		return partsTimelyInfo;
	}

	public void setPartsTimelyInfo(String partsTimelyInfo) {
		this.partsTimelyInfo = partsTimelyInfo;
	}

	public Integer getQuantityLading() {
		return quantityLading;
	}

	public void setQuantityLading(Integer quantityLading) {
		this.quantityLading = quantityLading;
	}

	public Integer getQuantitySure() {
		return quantitySure;
	}

	public void setQuantitySure(Integer quantitySure) {
		this.quantitySure = quantitySure;
	}

	public Integer getQuantitySureTemp() {
		return quantitySureTemp;
	}

	public void setQuantitySureTemp(Integer quantitySureTemp) {
		this.quantitySureTemp = quantitySureTemp;
	}

	public Integer getQuantitySpares() {
		return quantitySpares;
	}

	public void setQuantitySpares(Integer quantitySpares) {
		this.quantitySpares = quantitySpares;
	}

	
	public Integer getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(Integer purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	public Integer getPurchaseOrderItemId() {
		return purchaseOrderItemId;
	}

	public void setPurchaseOrderItemId(Integer purchaseOrderItemId) {
		this.purchaseOrderItemId = purchaseOrderItemId;
	}

	public Integer getCanSureQuantity() {
		if(this.quantitySure!=null){
			return this.quantityLading-this.quantitySure;
		}else{
			return this.quantityLading;
		}
		
	}

	public String getHisRecord() {
		return hisRecord;
	}

	public void setHisRecord(String hisRecord) {
		this.hisRecord = hisRecord;
	}

	public Integer getQuantityGoods() {
		return quantityGoods;
	}

	public void setQuantityGoods(Integer quantityGoods) {
		this.quantityGoods = quantityGoods;
	}

	
//	public BigDecimal getTotalAmount(){
//		return Math.round(this.quantityLading*this.itemPrice*100)*0.01f;
//	}

	public PsiLadingBillItemDto(PsiLadingBillItem item) {
		super();
		this.id = item.getId();
		this.productName = item.getProductName();
		this.colorCode = item.getColorCode();
		this.countryCode = item.getColorCode();
		this.remark = item.getRemark();
		this.canLadingTotal = item.getCanLadingTotal();
		this.productConName = item.getProductConName();
		this.oldQuantityLading = item.getOldQuantityLading();
		this.itemPrice = item.getItemPrice();
		this.sku = item.getSku();
		this.partsTimelyInfo = item.getPartsTimelyInfo();
		this.quantityLading = item.getQuantityLading();
		this.quantitySure = item.getQuantitySure();
		this.quantitySureTemp = item.getQuantitySureTemp();
		this.quantitySpares = item.getQuantitySpares();
		this.purchaseOrderItemId=item.getPurchaseOrderItem().getId();
		this.purchaseOrderId=item.getPurchaseOrderItem().getPurchaseOrder().getId();
		this.purchaseOrderNo=item.getPurchaseOrderItem().getPurchaseOrder().getOrderNo();
		this.isPass=item.getIsPass();
		this.quantityOffLading = item.getQuantityOffLading();
		this.balanceDelay1=item.getBalanceDelay1();
		this.balanceDelay2=item.getBalanceDelay2();
		this.balanceRate1=item.getBalanceRate1();
		this.balanceRate2=item.getBalanceRate2();
		this.totalAmount=item.getTotalAmount();
		this.totalPaymentAmount=item.getTotalPaymentAmount();
		this.totalPaymentPreAmount=item.getTotalPaymentPreAmount();
		this.hisRecord = item.getHisRecord();
		this.quantityGoods = item.getQuantityGoods()==null?0:item.getQuantityGoods();
		this.deposit= item.getDeposit();
		this.payDepositAmount=item.getPayDepositAmount();
	}

	
	public PsiLadingBillItemDto(LcPsiLadingBillItem item) {
		super();
		this.id = item.getId();
		this.productName = item.getProductName();
		this.colorCode = item.getColorCode();
		this.countryCode = item.getColorCode();
		this.remark = item.getRemark();
		this.canLadingTotal = item.getCanLadingTotal();
		this.productConName = item.getProductConName();
		this.oldQuantityLading = item.getOldQuantityLading();
		this.itemPrice = item.getItemPrice();
		this.sku = item.getSku();
		this.partsTimelyInfo = item.getPartsTimelyInfo();
		this.quantityLading = item.getQuantityLading();
		this.quantitySure = item.getQuantitySure();
		this.quantitySureTemp = item.getQuantitySureTemp();
		this.quantitySpares = item.getQuantitySpares();
		this.purchaseOrderItemId=item.getPurchaseOrderItem().getId();
		this.purchaseOrderId=item.getPurchaseOrderItem().getPurchaseOrder().getId();
		this.purchaseOrderNo=item.getPurchaseOrderItem().getPurchaseOrder().getOrderNo();
		this.isPass=item.getIsPass();
		this.quantityOffLading = item.getQuantityOffLading();
		this.balanceDelay1=item.getBalanceDelay1();
		this.balanceDelay2=item.getBalanceDelay2();
		this.balanceRate1=item.getBalanceRate1();
		this.balanceRate2=item.getBalanceRate2();
		this.totalAmount=item.getTotalAmount();
		this.totalPaymentAmount=item.getTotalPaymentAmount();
		this.totalPaymentPreAmount=item.getTotalPaymentPreAmount();
		this.hisRecord = item.getHisRecord();
		this.quantityGoods = item.getQuantityGoods()==null?0:item.getQuantityGoods();
		this.deposit= item.getDeposit();
		this.payDepositAmount=item.getPayDepositAmount();
	}
	
	public PsiLadingBillItemDto(BigDecimal itemPrice,Integer purchaseOrderItemId,Integer purchaseOrderId, String purchaseOrderNo,Integer packQuantity,
			Integer quantityLading,String isPass,Integer quantityOffLading,String poDeliveryDate,Float volume){
		super();
		this.itemPrice 				= itemPrice;
		this.purchaseOrderItemId 	= purchaseOrderItemId;
		this.purchaseOrderId 		= purchaseOrderId;
		this.purchaseOrderNo 		= purchaseOrderNo;
		this.packQuantity		    = packQuantity;
		this.quantityLading         = quantityLading;
		this.isPass                 = isPass;
		this.quantityOffLading      = quantityOffLading;
		this.poDeliveryDate         = poDeliveryDate;
		this.volume                 = volume;
	}

	
	public BigDecimal getTotalPaymentPreAmount() {
		return totalPaymentPreAmount;
	}

	public void setTotalPaymentPreAmount(BigDecimal totalPaymentPreAmount) {
		this.totalPaymentPreAmount = totalPaymentPreAmount;
	}

	public BigDecimal getTotalPaymentAmount() {
		return totalPaymentAmount;
	}

	public void setTotalPaymentAmount(BigDecimal totalPaymentAmount) {
		this.totalPaymentAmount = totalPaymentAmount;
	}

	public Integer getBalanceRate1() {
		return balanceRate1;
	}

	public void setBalanceRate1(Integer balanceRate1) {
		this.balanceRate1 = balanceRate1;
	}

	public Integer getBalanceDelay1() {
		return balanceDelay1;
	}

	public void setBalanceDelay1(Integer balanceDelay1) {
		this.balanceDelay1 = balanceDelay1;
	}

	public Integer getBalanceRate2() {
		return balanceRate2;
	}

	public void setBalanceRate2(Integer balanceRate2) {
		this.balanceRate2 = balanceRate2;
	}

	public Integer getBalanceDelay2() {
		return balanceDelay2;
	}

	public void setBalanceDelay2(Integer balanceDelay2) {
		this.balanceDelay2 = balanceDelay2;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public String getIsTestOver() {
		return isTestOver;
	}

	public void setIsTestOver(String isTestOver) {
		this.isTestOver = isTestOver;
	}
	
	@Transient
	public String getProductColor(){
		if(StringUtils.isEmpty(colorCode)){
			return productName;
		}else{
			return productName+"_"+colorCode;
		}
	}
}


