/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.modules.sys.utils.DictUtils;

/**
 * 提单明细Entity
 * @author Michael
 * @version 2014-11-11
 */

@Entity
@Table(name = "lc_psi_lading_bill_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPsiLadingBillItem implements Serializable{
	private static final long serialVersionUID = -3449075149427165130L;
	private 	Integer              id; 		
	private 	String 				 productName;        // 产品名
	private 	String 				 colorCode;          // 颜色
	private 	String 				 countryCode;        // 国家
	private 	String     			 delFlag="0";        // 删除标志
	private 	String     			 remark;             // 备注
	private 	Integer              canLadingTotal;     // 可提货总数
	private 	String               productConName;     // 产品名
	private 	Integer              oldQuantityLading;  // 原提单总数量
	private     BigDecimal           itemPrice;          // 单价    
	private     String               sku;                // sku
	private     String               partsTimelyInfo;    // 实时配件信息
	
	private 	Integer 			 quantityLading;     // 提货数量
	private 	Integer 			 quantitySure;       // 确认数量
	private 	Integer 			 quantitySureTemp;   // 本次确认数量（非字段）
	private 	Integer 			 quantitySpares;     // 备品数量
	private 	LcPsiLadingBill		 ladingBill;         // 提单主
	private 	LcPurchaseOrderItem  purchaseOrderItem;  // 采购订单项     
	
	private     String               isPass;             // 合格：1 不合格为：0 
	private 	Integer 			 quantityOffLading;  // 线下数量
	private 	Integer              oldQuantityOffLading;// 原线下数量
	
	private 	BigDecimal           totalAmount;                //提单总额
	private 	BigDecimal           totalPaymentPreAmount;      //已申请总额
	private 	BigDecimal           totalPaymentAmount;         //已支付总额
	private 	Integer 			 balanceRate1;       		 //尾款首次付款比例
	private 	Integer 			 balanceDelay1;      		 //第一次付款延迟几天
	private 	Integer 			 balanceRate2;       		 //尾款第二次付款比例
	private 	Integer 			 balanceDelay2;      		 //第二次付款延迟几天
	private 	List<LcPurchasePaymentItem>   payItems;   
	
	private String hisRecord; // 验货记录
	private Integer quantityGoods; // 已验货总数
	private String qualityDate; // 验货时间(非数据库字段)
	private Integer quantityActual; // 当次验货数量(非数据库字段)
	
	
	public LcPsiLadingBillItem() {
		super();
	}

	public LcPsiLadingBillItem(Integer id){
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

	@Transient
	public Integer getOldQuantityLading() {
		return oldQuantityLading;
	}

	public void setOldQuantityLading(Integer oldQuantityLading) {
		this.oldQuantityLading = oldQuantityLading;
	}

	@OneToMany(mappedBy = "ladingBillItem")
	@Fetch(FetchMode.SELECT)
	@Where(clause="del_flag=0")
	@NotFound(action = NotFoundAction.IGNORE)
	public List<LcPurchasePaymentItem> getPayItems() {
		return payItems;
	}

	public void setPayItems(List<LcPurchasePaymentItem> payItems) {
		this.payItems = payItems;
	}
	
	@Transient
	public Integer getDeposit() {
		return this.purchaseOrderItem.getPurchaseOrder().getDeposit();
	}


	@Transient
	public BigDecimal getPayDepositAmount() {
		int deposit = this.getDeposit();
		if(deposit>0&&this.purchaseOrderItem.getPurchaseOrder().getDepositAmount().compareTo(BigDecimal.ZERO)>0){
			return new BigDecimal(quantityLading).multiply(this.itemPrice).multiply(new BigDecimal(deposit/100));
		}
		return BigDecimal.ZERO;
	}


	@Transient
	public BigDecimal getCanPayAmount(){
		return this.totalAmount.subtract(this.totalPaymentAmount).subtract(this.totalPaymentPreAmount);
	}
	
	public String getPartsTimelyInfo() {
		return partsTimelyInfo;
	}

	public void setPartsTimelyInfo(String partsTimelyInfo) {
		this.partsTimelyInfo = partsTimelyInfo;
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

	@Transient
	@JsonIgnore
	public Integer getCanLadingTotal() {
		return canLadingTotal;
	}

	public void setCanLadingTotal(Integer canLadingTotal) {
		this.canLadingTotal = canLadingTotal;
	}

	@ManyToOne()
	@JoinColumn(name="lading_bill_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPsiLadingBill getLadingBill() {
		return ladingBill;
	}

	public void setLadingBill(LcPsiLadingBill ladingBill) {
		this.ladingBill = ladingBill;
	}
	@Transient
	public String getProductConName() {
		return productConName;
	}

	public void setProductConName(String productConName) {
		this.productConName = productConName;
	}
	

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Transient
	public Integer getOldQuantityOffLading() {
		return oldQuantityOffLading;
	}

	public void setOldQuantityOffLading(Integer oldQuantityOffLading) {
		this.oldQuantityOffLading = oldQuantityOffLading;
	}

	public String getIsPass() {
		return isPass;
	}

	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}

	@ManyToOne()
	@JoinColumn(name="purchase_order_item_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPurchaseOrderItem getPurchaseOrderItem() {
		return purchaseOrderItem;
	}

	public void setPurchaseOrderItem(LcPurchaseOrderItem purchaseOrderItem) {
		this.purchaseOrderItem = purchaseOrderItem;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Integer getQuantityLading() {
		return quantityLading;
	}

	public void setQuantityLading(Integer quantityLading) {
		this.quantityLading = quantityLading;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Transient
	public String getCountry() {
		return DictUtils.getDictLabel(countryCode,"platform", "");
	}
	
	@Transient
	public String getCountryStr() {
		String rs = countryCode;
		if("com".equals(countryCode)){
			rs = "us";
		}
		return rs.toUpperCase();
	}
	
	@Transient
	public String getBarcode() {
		return purchaseOrderItem.getBarcode(); 
	}
	
//	@Transient
//	public BigDecimal getTotalAmount() {
//		BigDecimal totalAmount = 0f;
//		if(this.itemPrice!=null){
//			totalAmount=Math.round(this.itemPrice*this.quantityLading*(100-LcPurchaseOrderItem.getPurchaseOrder().getDeposit()))*0.01f;
//		}
//		return totalAmount;
//	}

	public LcPsiLadingBillItem(LcPsiLadingBillItem item) {
		super();
		this.productName = item.getProductName();
		this.colorCode =  item.getColorCode();
		this.countryCode =  item.getCountryCode();
		this.quantityLading =  item.getQuantityLading();
		this.remark =  item.getRemark();
		this.delFlag = item.getPurchaseOrderItem().getBarcodeNoCountry();
		this.sku=item.getSku();
	}
	
	@Transient
	public BigDecimal  getVolume(){
		if(this.purchaseOrderItem.getProduct().getPackQuantity()>0){
			return this.purchaseOrderItem.getProduct().getBoxVolume().multiply(new BigDecimal(this.quantityLading/this.purchaseOrderItem.getProduct().getPackQuantity()));
		}else{
			return BigDecimal.ZERO;
		}	
	}
	
	@Transient
	public BigDecimal  getWeight(){
		if(this.purchaseOrderItem.getProduct().getPackQuantity()>0){
			return this.purchaseOrderItem.getProduct().getGw().multiply(new BigDecimal(this.quantityLading/this.purchaseOrderItem.getProduct().getPackQuantity()));
		}else{
			return BigDecimal.ZERO;
		}	
	}

	public Integer getQuantitySure() {
		return quantitySure;
	}

	public void setQuantitySure(Integer quantitySure) {
		this.quantitySure = quantitySure;
	}

	
	@Transient
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
	
	
	@Transient
	public String getProductNameColor(){
		String productName = this.productName;
		if(StringUtils.isNotEmpty(this.colorCode)){
			productName=productName+"_"+this.colorCode;
		}
		return productName;
	}

	public Integer getQuantityOffLading() {
		return quantityOffLading;
	}

	public void setQuantityOffLading(Integer quantityOffLading) {
		this.quantityOffLading = quantityOffLading;
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

	@Transient
	public String getQualityDate() {
		return qualityDate;
	}

	public void setQualityDate(String qualityDate) {
		this.qualityDate = qualityDate;
	}

	@Transient
	public Integer getQuantityActual() {
		return quantityActual;
	}

	public void setQuantityActual(Integer quantityActual) {
		this.quantityActual = quantityActual;
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

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	@Transient
	public  String getSkuVersion(){
		if(StringUtils.isNotEmpty(sku)){
			Pattern pattern = Pattern.compile("new[0-9]*", Pattern.CASE_INSENSITIVE);   
			 Matcher matcher = pattern.matcher(sku);                            
			 if(matcher.find(0)) {  
				 String res =matcher.group();
			     if("NEW".equals(res.toUpperCase())){
			    	return "NEW1"; 
			     }else{
			    	return  res.toUpperCase();
			     }                       
			 }
		}
		 return "无";
	}
	
	@Transient
	public BigDecimal getNoDepositTotalAmount(){
		return this.itemPrice.multiply(new BigDecimal(this.quantityLading));
	}
	
	@Transient
	public BigDecimal getNoDepositCanPayAmount(){
		BigDecimal payDepositAmount = getPayDepositAmount();
		return this.totalAmount.subtract(this.totalPaymentAmount).subtract(this.totalPaymentPreAmount).subtract(payDepositAmount);
	}


//	public String getIsTestOver() {
//		return isTestOver;
//	}
//
//	public void setIsTestOver(String isTestOver) {
//		this.isTestOver = isTestOver;
//	}

}


