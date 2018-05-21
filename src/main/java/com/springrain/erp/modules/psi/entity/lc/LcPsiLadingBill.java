/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

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
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.entity.PsiInventoryIn;
import com.springrain.erp.modules.psi.entity.PsiInventoryInItem;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 提单明细Entity
 * @author Michael
 * @version 2014-11-11
 */
@Entity
@Table(name = "lc_psi_lading_bill")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPsiLadingBill implements Serializable {
	
	
	private static final long serialVersionUID = 7297995498024687210L;
	private 	Integer 		id; 		
	private 	String			billNo ;
	private 	PsiSupplier 	supplier;
	private 	String 			billSta;
	private 	String 			delFlag;
	private 	String          oldItemIds;
	private 	String          attchmentPath;         
	private 	String          remark;
	private 	BigDecimal      totalPaymentPreAmount;         //已申请总额
	private 	BigDecimal      totalPaymentAmount;            //已支付总额
	private 	BigDecimal      totalAmount;                   //提单总额
	private 	BigDecimal      noDepositAmount;               //无订金总额
	private 	User            sureUser;
	private 	Date            sureDate;
	private 	User            updateUser;
	private 	Date            updateDate;
	private 	User            cancelUser;
	private 	Date            cancelDate;
	private 	Date 			createDate;
	private 	User 			createUser;
	private 	Integer         tempQuantity;                  // 收货数量（统计收货数量用到）
	private 	Date       		deliveryDate;                  // 可验货日期
	private 	String    		currencyType;                  // 货币类型
	private	    PsiSupplier     tranSupplier;                  // 承运商  （货代）
	private 	List<LcPsiLadingBillItem>     items = Lists.newArrayList() ;
	
	
	private 	Date            testDate;
	private 	User 			testUser;
	
	private     String          tranMan;            // 送货人(非数据库字段)
	private     String          carNo;              // 车牌号(非数据库字段)
	private     String          phone;              // 电话(非数据库字段)
	
	private 	Date       	    actualDeliveryDate; // 实际收货日期
	
	
	public LcPsiLadingBill() {
		super();
	}

	public LcPsiLadingBill(Integer id){
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
	
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	@Transient
	public String getOldItemIds() {
		return oldItemIds;
	}

	public void setOldItemIds(String oldItemIds) {
		this.oldItemIds = oldItemIds;
	}

	public Date getTestDate() {
		return testDate;
	}

	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}

	
	@ManyToOne()
	@JoinColumn(name="test_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getTestUser() {
		return testUser;
	}

	public void setTestUser(User testUser) {
		this.testUser = testUser;
	}

	public String getRemark() {
		return remark;
	}

	@ManyToOne()
	@JoinColumn(name="sure_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getSureUser() {
		return sureUser;
	}

	public void setSureUser(User sureUser) {
		this.sureUser = sureUser;
	}

	@ManyToOne()
	@JoinColumn(name="update_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getUpdateUser() {
		return updateUser;
	}

	@Transient
	public Integer getTempQuantity() {
		return tempQuantity;
	}

	public void setTempQuantity(Integer tempQuantity) {
		this.tempQuantity = tempQuantity;
	}

	@Transient
	public String getStatusName(){
		String returnStr="";
		if("0".equals(billSta)){
			returnStr="申请";
		}else if("1".equals(billSta)){
			returnStr="确认";
		}else if("2".equals(billSta)){
			returnStr="已取消";
		}
		return returnStr;
	}
	
	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	@ManyToOne()
	@JoinColumn(name="cancel_user")
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

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	@Transient
	public BigDecimal getUnPayment(){
		return this.totalAmount.subtract(this.totalPaymentAmount);
	}

	public Date getSureDate() {
		return sureDate;
	}

	public void setSureDate(Date sureDate) {
		this.sureDate = sureDate;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public String getAttchmentPath() {
		return attchmentPath;
	}

	public void setAttchmentPath(String attchmentPath) {
			this.attchmentPath = attchmentPath;
	}

	public void setAttchmentPathAppend(String attchmentPath) {
		if(StringUtils.isBlank(this.attchmentPath)){
			this.attchmentPath = attchmentPath;
		}else{
			this.attchmentPath = this.attchmentPath+","+attchmentPath;
		}
	}
	
	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	@Transient
	public Integer getLadingTotal(){
		Integer rs=0;
		for(LcPsiLadingBillItem item:items){
			rs+=item.getQuantityLading();
		}
		return rs;
	}
	
	@Transient
	public Integer getLadingReceivedTotal(){
		Integer rs=0;
		for(LcPsiLadingBillItem item:items){
			rs+=item.getQuantitySure();
		}
		return rs;
	}
	
	@OneToMany(mappedBy = "ladingBill",fetch=FetchType.EAGER)
	@OrderBy(value="countryCode")
	@Where(clause="del_flag=0")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.ALL)
	public List<LcPsiLadingBillItem> getItems() {
		return items;
	}

	public void setItems(List<LcPsiLadingBillItem> items) {
		this.items = items;
	}
	

//	@OneToMany(mappedBy = "ladingBill")
//	@Fetch(FetchMode.SELECT)
//	@NotFound(action = NotFoundAction.IGNORE)
//	public List<LcPurchasePaymentItem> getPayItems() {
//		return payItems;
//	}
//
//	public void setPayItems(List<LcPurchasePaymentItem> payItems) {
//		this.payItems = payItems;
//	}

	@ManyToOne()
	@JoinColumn(name="supplier_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(PsiSupplier supplier) {
		this.supplier = supplier;
	}


	public BigDecimal getNoDepositAmount() {
		return noDepositAmount;
	}

	public void setNoDepositAmount(BigDecimal noDepositAmount) {
		this.noDepositAmount = noDepositAmount;
	}

	@ManyToOne()
	@JoinColumn(name="tran_supplier_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplier getTranSupplier() {
		return tranSupplier;
	}

	public void setTranSupplier(PsiSupplier tranSupplier) {
		this.tranSupplier = tranSupplier;
	}

	public String getBillSta() {
		return billSta;
	}

	public void setBillSta(String billSta) {
		this.billSta = billSta;
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


	@ManyToOne()
	@JoinColumn(name="create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	@Transient
	public Map<String,List<LcPsiLadingBillItem>> getCountItems(){
		Map<String,LcPsiLadingBillItem> tempMaps = Maps.newLinkedHashMap();
		for (LcPsiLadingBillItem item : items) {
			String key = item.getProductName()+":"+item.getCountry()+":"+item.getColorCode();
			LcPsiLadingBillItem temp = tempMaps.get(key);
			if(temp==null){
				temp = new LcPsiLadingBillItem(item);
				tempMaps.put(key, temp);
			}else{
				temp.setQuantityLading(temp.getQuantityLading()+item.getQuantityLading());
			}
		}
		
		Map<String,List<LcPsiLadingBillItem>> singMap=Maps.newHashMap();
		for(LcPsiLadingBillItem item:tempMaps.values()){
			List<LcPsiLadingBillItem> items=Lists.newArrayList();
			String key= item.getProductName()+","+item.getColorCode();
			if(singMap.get(key)!=null){
				items=singMap.get(key);
			}
			items.add(item);
			singMap.put(key, items);
		}
		
		return singMap;
	}
	
	@Transient
	public List<LcPsiLadingBill> getTempLadingBills(){
		Map<String,LcPsiLadingBill> map = Maps.newLinkedHashMap();
		List<LcPsiLadingBill> ladings = Lists.newArrayList();
		for (LcPsiLadingBillItem item : items) {
			LcPsiLadingBill temp=map.get(item.getProductNameColor());
			if(temp==null){
				temp=new LcPsiLadingBill();
				temp.setSupplier(supplier);
				temp.setBillSta(billSta);
				temp.setBillNo(billNo);
				temp.setId(id);
				temp.setTranSupplier(tranSupplier);
				temp.setCreateDate(createDate);
				temp.setCreateUser(createUser);
				temp.setSureUser(sureUser);
				temp.setTotalPaymentPreAmount(item.getTotalPaymentPreAmount());
				temp.setTotalAmount(item.getTotalAmount());
				temp.setTotalPaymentAmount(item.getTotalPaymentAmount());
				temp.setAttchmentPath(attchmentPath);
				temp.setTestDate(testDate);
				temp.setTestUser(testUser);
				temp.setDeliveryDate(deliveryDate);
				temp.setActualDeliveryDate(actualDeliveryDate);
				map.put(item.getProductNameColor(), temp);
			}else{
				temp.setTotalPaymentPreAmount(item.getTotalPaymentPreAmount().add(item.getTotalPaymentPreAmount()));
				temp.setTotalAmount(item.getTotalAmount().add(item.getTotalAmount()));
				temp.setTotalPaymentAmount(item.getTotalPaymentAmount().add(item.getTotalPaymentAmount()));
				//页面付款状态精确到单品
			}
			temp.getItems().add(item);
		}
		
		ladings.addAll(map.values());
		
		return ladings;
	}
	
	
	
	@Transient
	public BigDecimal  getVolume(){
		BigDecimal rs = BigDecimal.ZERO;
		for (LcPsiLadingBillItem item : items) {
			rs=rs.add(item.getVolume());
		}
		return rs;
	}
	
	@Transient
	public BigDecimal  getWeight(){
		BigDecimal rs = BigDecimal.ZERO;
		for (LcPsiLadingBillItem item : items) {
			rs=rs.add(item.getWeight());
		}
		return rs;
	}
	
	//是否存在不合格产品,1：合格  0：不合格
	@Transient
	public String getIsPass(){
		String rs = "1";
		for (LcPsiLadingBillItem item : items) {
			if (StringUtils.isNotEmpty(item.getIsPass()) && "0".equals(item.getIsPass())) {
				rs = "0";
				break;
			}
		}
		return rs;
	}
	
	
	//这个只能获得大概的定金比例，因为定金比例是以订单为基础
//	@Transient
//    public BigDecimal getDeposit(){
//    	return totalAmount*100/noDepositAmount;
//    }
	
	public PsiInventoryIn  toInventoryIn(){
		PsiInventoryIn in = new PsiInventoryIn();
		in.setAddDate(sureDate);
		in.setAddUser(sureUser);
		in.setTranLocalId(id);
		in.setTranLocalNo(billNo);
		in.setOperationType("Purchase Storing");
		in.setWarehouseId(130);
		in.setWarehouseName("中国本地仓B");
		in.setSource("CN");
		List<PsiInventoryInItem> inItems = Lists.newArrayList();
		for(LcPsiLadingBillItem item:items){
			PsiInventoryInItem inItem = new PsiInventoryInItem();
			inItem.setColorCode(item.getColorCode());
			inItem.setCountryCode(item.getCountryCode());
			inItem.setProductName(item.getProductName());
			inItem.setSku(item.getSku());
			inItem.setTimelyQuantity(null);
			inItem.setQualityType("new");
			inItem.setQuantity(item.getQuantityLading());
			inItem.setInventoryIn(in);
			inItems.add(inItem);
		}
		in.setItems(inItems);
		return in;
	}
	
	
	@Transient
	public String getTempColor(){
		String rs ="";
		for(LcPsiLadingBillItem item :items){
			rs=item.getColorCode();
			break;
		}
		return rs;
	}
	
	@Transient
	public String getTempProductName(){
		String rs ="";
		for(LcPsiLadingBillItem item :items){
			rs=item.getProductName();
			break;
		}
		return rs;
	}
	@Transient
	public String getTempProductNameColor(){
		String rs ="";
		for(LcPsiLadingBillItem item :items){
			rs=item.getProductNameColor();
			break;
		}
		return rs;
	}
	
	@Transient
	public String getTempKey(){
		return billNo+"_"+getTempProductNameColor();
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

	public Date getActualDeliveryDate() {
		return actualDeliveryDate;
	}

	public void setActualDeliveryDate(Date actualDeliveryDate) {
		this.actualDeliveryDate = actualDeliveryDate;
	}
	
}


