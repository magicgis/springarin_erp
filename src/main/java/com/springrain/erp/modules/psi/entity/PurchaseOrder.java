/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrderItem;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 采购订单Entity
 * @author Michael
 * @version 2014-10-29
 */
@Entity
@Table(name = "psi_purchase_order")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PurchaseOrder implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private		 Integer   		 id; 		          	 // id
	private		 String   		 orderNo; 	          	 // 订单编号
	private 	 String	         orderSta;            	 // 订单状态 0, 1:草稿；2：生产；3：部分收货；4：已收货；5：已完成；6：已取消
	private		 String   		 currencyType;        	 // 货币类型
	private		 BigDecimal		 totalAmount;         	 // 总金额
	private		 Date     		 purchaseDate;       	 // 下单日期
	private		 String   		 receivedStore;          // 收货仓库
	private		 String   		 delFlag;
	private		 User     		 merchandiser;           // 跟单员
	private		 PsiSupplier     supplier;               // 供应商
	private		 String    		 oldItemIds ;            // 老items
	private		 Integer   		 deposit;                // 供应商定金
	private		 String   		 piFilePath;             // PI文件路径
	private		 String   		 modifyMemo;             // 修改备注
	private		 String   		 versionNo;              // 版本号
	private		 Date     		 updateDate;             // 最后更新日期
	private		 User     		 updateUser;             // 最后更新人
	private		 Date     		 sureDate;               // 确认日期
	private		 User     		 sureUser;               // 确认人
	private		 Date      		 cancelDate;             // 取消日期
	private		 User            cancelUser;             // 取消人
	private		 User      		 createUser;             // 创建人
	private		 Date     		 createDate;             // 创建日期
	
	
	private		 Date      		 receiveFinishedDate;    //	收货完成日期
	private		 BigDecimal    	 depositAmount;          //	已付定金金额
	private		 BigDecimal    	 depositPreAmount;       //	申请付定金金额
	private		 String    		 paySta;                 //	付款状态
	private		 BigDecimal 	 paymentAmount;          //	付款总额
	private		 Integer  		 tempLessCargoQuantity;  //	欠货数量             单品欠货统计
	private		 Integer  		 tempNoSureCargoQuantity;//	收货未确认数量           单品欠货统计
	private		 Integer  		 oldDeposit ;            //	原来定金比例
	
	private		 String   		 sendEmailFlag  = "0";
	private		 String   		 toReview="0";           //	申请审核
	private      String   		 toPartsOrder="0";       //	是否进行过配件下单操作
	private      String          isOverInventory;        // 是否超最大库存
	private      String          overRemark;             // 超出备份
    
	private		 PurchasePaymentItem    payItem;         // 付款明细项
	
	private 	 List<PurchaseOrderItem> items = Lists.newArrayList();
	private      boolean         needParts;              // 非字段
	private		 String   		 piReviewSta;        //	申请审核
	
	public PurchaseOrder() {
		super();
	}

	public PurchaseOrder(Integer id){
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

	
	@OneToOne(fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="pay_item_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PurchasePaymentItem getPayItem() {
		return payItem;
	}

	public void setPayItem(PurchasePaymentItem payItem) {
		this.payItem = payItem;
	}
	
	@Transient
	public  BigDecimal getDepositTotal(){
		return this.totalAmount.multiply(new BigDecimal(deposit/100f));
	}
	
	@Transient
	public String getCanEditDeposit() {
		String rs = "1";
		if("0,1,6".contains(orderSta)){
			return rs;
		}else if("5".equals(orderSta)){
			return "0";
		}else if("2,3,4".contains(orderSta)){
			if(deposit>0){
				if(depositAmount.add(depositPreAmount).compareTo(new BigDecimal("0"))>=0){
					return "0";
				}
			}
			if(getTotalPaymentAmount().compareTo(BigDecimal.ZERO)>0){
				return "0";
			}
			for (PurchaseOrderItem item : items) {
				for (PsiLadingBillItem bitem : item.getBillItemList()) {
					if(bitem.getLadingBill().getTotalPaymentAmount().compareTo(BigDecimal.ZERO)>0){
						return "0";
					}
				}
			}
		}
		return rs;
	}
	
	public String getToReview() {
		return toReview;
	}

	public void setToReview(String toReview) {
		this.toReview = toReview;
	}

	@Transient
	public Integer getOldDeposit() {
		return oldDeposit;
	}

	public void setOldDeposit(Integer oldDeposit) {
		this.oldDeposit = oldDeposit;
	}

	@Transient
	public Integer getTempLessCargoQuantity() {
		return tempLessCargoQuantity;
	}

	public void setTempLessCargoQuantity(Integer tempLessCargoQuantity) {
		this.tempLessCargoQuantity = tempLessCargoQuantity;
	}

	@Transient
	public Integer getTempNoSureCargoQuantity() {
		return tempNoSureCargoQuantity;
	}

	public void setTempNoSureCargoQuantity(Integer tempNoSureCargoQuantity) {
		this.tempNoSureCargoQuantity = tempNoSureCargoQuantity;
	}

	
	public String getPaySta() {
		return paySta;
	}

	public void setPaySta(String paySta) {
		this.paySta = paySta;
	}
	
	public String getSendEmailFlag() {
		return sendEmailFlag;
	}

	public void setSendEmailFlag(String sendEmailFlag) {
		this.sendEmailFlag = sendEmailFlag;
	}

	@Transient
	public String getCanEditCurrencyType() {
		String rs = "1";
		if("1,6".contains(orderSta)){
			return rs;
		}else if("5,3,4".contains(orderSta)){
			return "0";
		}else if("2".equals(orderSta)){
			for (PurchaseOrderItem item : items) {
				if(item.getBillItemList().size()>0){
					return "0";
				}
			}
		}
		return rs;
	}
	
	
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	@Transient
	public String getStatusName(){
		String returnStr="";
		if("1".equals(orderSta)){
			returnStr="草稿";
		}else if("2".equals(orderSta)){
			returnStr="生产";
		}else if("3".equals(orderSta)){
			returnStr="部分收货";
		}else if("4".equals(orderSta)){
			returnStr="已收货";
		}else if("5".equals(orderSta)){
			returnStr="已完成";
		}else if("6".equals(orderSta)){
			returnStr="已取消";
		}
		return returnStr;
	}
	
	@Transient
	public boolean getNeedParts(){
		return this.needParts;
	}
	
	public  void setNeedParts(boolean needParts){
		 this.needParts=needParts;
	}
	
	
	
	public BigDecimal getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(BigDecimal depositAmount) {
		this.depositAmount = depositAmount;
	}

	public BigDecimal getDepositPreAmount() {
		return depositPreAmount;
	}

	public void setDepositPreAmount(BigDecimal depositPreAmount) {
		this.depositPreAmount = depositPreAmount;
	}

	@ManyToOne()
	@JoinColumn(name="update_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}

	public Date getSureDate() {
		return sureDate;
	}

	public void setSureDate(Date sureDate) {
		this.sureDate = sureDate;
	}

	public String getToPartsOrder() {
		return toPartsOrder;
	}

	public void setToPartsOrder(String toPartsOrder) {
		this.toPartsOrder = toPartsOrder;
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

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
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

	public String getModifyMemo() {
		return modifyMemo;
	}

	public void setModifyMemo(String modifyMemo) {
		this.modifyMemo = modifyMemo;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	
	public Integer getDeposit() {
		return deposit;
	}

	public void setDeposit(Integer deposit) {
		this.deposit = deposit;
	}
	
	
	@Transient
	public String getSnCode(){
		String yearLast = this.orderNo.substring(3,4);
		String month = this.orderNo.substring(4, 6);
		String day = this.orderNo.substring(6,8);
		String reSupplier=StringUtils.reverse(orderNo.substring(8, 11));
		return yearLast+changeMonth(month)+day+reSupplier;
	}
	
	@Transient
	public String getOrderNoBySnCode(String snCode){
		String orderNo ="";
		if(StringUtils.isNotEmpty(snCode)&&snCode.length()>6){
			snCode=snCode.substring(0,7);
			orderNo =snCode.substring(0,1)+unChangeMonth(snCode.substring(1,2))+snCode.substring(2, 4)+StringUtils.reverse(snCode.substring(4, 7));
		}
		
		return orderNo ;
	}
	public  String changeMonth(String month){
		String res ="";
		if("01".equals(month)){
			res="A";
		}else if("02".equals(month)){
			res="B";
		}else if("03".equals(month)){
			res="C";
		}else if("04".equals(month)){
			res="D";
		}else if("05".equals(month)){
			res="E";
		}else if("06".equals(month)){
			res="F";
		}else if("07".equals(month)){
			res="G";
		}else if("08".equals(month)){
			res="H";
		}else if("09".equals(month)){
			res="I";
		}else if("10".equals(month)){
			res="J";
		}else if("11".equals(month)){
			res="K";
		}else if("12".equals(month)){
			res="L";
		}
		return res;
	}
	
	public  String unChangeMonth(String month){
		String res ="";
		if("A".equals(month)){
			res="01";
		}else if("B".equals(month)){
			res="02";
		}else if("C".equals(month)){
			res="03";
		}else if("D".equals(month)){
			res="04";
		}else if("E".equals(month)){
			res="05";
		}else if("F".equals(month)){
			res="06";
		}else if("G".equals(month)){
			res="07";
		}else if("H".equals(month)){
			res="08";
		}else if("I".equals(month)){
			res="09";
		}else if("J".equals(month)){
			res="10";
		}else if("K".equals(month)){
			res="11";
		}else if("L".equals(month)){
			res="12";
		}
		return res;
	}
	

	@Transient
	@JsonIgnore
	public String getOldItemIds() {
		return oldItemIds;
	}

	public void setOldItemIds(String oldItemIds) {
		this.oldItemIds = oldItemIds;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}


	public String getPiFilePath() {
		return piFilePath;
	}

	public void setPiFilePath(String piFilePath) {
		this.piFilePath = piFilePath;
	}
	
	@Transient
	public BigDecimal getUnPayment(){
		return this.totalAmount.subtract(this.getTotalPaymentAmount());
	}

	@Transient
	public BigDecimal getTotalPaymentAmount(){
		return paymentAmount.add(depositAmount);
	}
	
	
	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	
	@ManyToOne()
	@JoinColumn(name="merchandiser")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getMerchandiser() {
		return merchandiser;
	}

	public void setMerchandiser(User merchandiser) {
		this.merchandiser = merchandiser;
	}

	public String getReceivedStore() {
		return receivedStore;
	}

	public void setReceivedStore(String receivedStore) {
		this.receivedStore = receivedStore;
	}

	public String getOrderSta() {
		return orderSta;
	}

	@Transient
	public Integer getItemsQuantityReceived() {
		int rs = 0;
		for (PurchaseOrderItem item : items) {
			rs+=item.getQuantityReceived();
		}
		return rs;
	}
	
	@Transient
	public BigDecimal getItemsAmount() {
		BigDecimal rs =  new BigDecimal("0");
		BigDecimal rate = new BigDecimal("1");
		if("2,3,4,5,".contains(orderSta+",")){
			if("CNY".equals(currencyType)){
				rate = new BigDecimal(AmazonProduct2Service.getRateConfig().get("USD/CNY")+"");
			}
			for (PurchaseOrderItem item : items) {
				rs=rs.add(new BigDecimal(item.getQuantityOrdered()).multiply(item.getItemPrice()).divide(rate, 2, BigDecimal.ROUND_HALF_UP));
			}
		}
		
		return rs;
	}
	
	@Transient
	public Integer getItemsQuantityUnReceived() {
		int rs = 0;
		for (PurchaseOrderItem item : items) {
			rs+=item.getQuantityOrdered()-item.getQuantityReceived();
		}
		return rs;
	}
	
	@Transient
	public Integer getItemsQuantityCanReceived() {
		int rs = 0;
		for (PurchaseOrderItem item : items) {
			rs+=item.getQuantityOrdered()-item.getQuantityReceived()-item.getQuantityPreReceived();
		}
		return rs;
	}
	
	@Transient
	public Integer getItemsQuantityPreReceived() {
		int rs = 0;
		for (PurchaseOrderItem item : items) {
			rs+=item.getQuantityPreReceived();
		}
		return rs;
	}
	

	@Transient
	public Integer getItemsQuantity() {
		int rs = 0;
		for (PurchaseOrderItem item : items) {
			rs+=item.getQuantityOrdered();
		}
		return rs;
	}

	public void setOrderSta(String orderSta) {
		this.orderSta = orderSta;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}


	@Column(name="order_total")
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getReceiveFinishedDate() {
		return receiveFinishedDate;
	}

	public void setReceiveFinishedDate(Date receiveFinishedDate) {
		this.receiveFinishedDate = receiveFinishedDate;
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
	

	@ManyToOne()
	@JoinColumn(name="supplier_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(PsiSupplier supplier) {
		this.supplier = supplier;
	}
	
	@OneToMany(mappedBy = "purchaseOrder",fetch=FetchType.EAGER)
	@OrderBy(value="product,colorCode,countryCode")
	@Where(clause="del_flag=0")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public List<PurchaseOrderItem> getItems() {
		return items;
	}
	
	@Transient
	public List<PurchaseOrderItem> getShowItems() {
		if(items!=null&&items.size()>0){
			Collections.sort(items);
		}
		return items;
	}
	
	
	public void setItems(List<PurchaseOrderItem> items) {
		this.items = items;
		/*if(items.size()>0){
			for(PurchaseOrderItem item:items){
				item.setPurchaseOrder(this);
			}
		}*/
	}
	
	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	@Transient
	public Map<String,Map<String,PurchaseOrder>> getOrderItemMap(){
		Map<String,Map<String,PurchaseOrder>>  rs = Maps.newLinkedHashMap();
		for (PurchaseOrderItem item : items) {
			String productName = item.getProduct().getName();
			Map<String,PurchaseOrder> tempMap = rs.get(productName);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(productName, tempMap);
			}
			String color = item.getColorCode();
			if(StringUtils.isEmpty(color)){
				color = "No Color";
			}
			PurchaseOrder order = tempMap.get(color);
			if(order == null){
				order = new PurchaseOrder();
				order.setPurchaseDate(purchaseDate);
				tempMap.put(color, order);
			}
			order.getItems().add(item);
		}
		return rs;
	}
	
	@Transient
	public Map<String,PurchaseOrder> getOrderItemsMap(){
		Map<String,PurchaseOrder>  rs = Maps.newLinkedHashMap();
		for (PurchaseOrderItem item : getShowItems()) {
			String name = item.getProductName();
			PurchaseOrder temp=rs.get(name);
			if(temp==null){
				temp = new PurchaseOrder();
				rs.put(name, temp);
			}
			temp.getItems().add(item);
		}
		return rs;
	}
	
	
	@Transient
	public Map<String,PsiLadingBill> getLadingsMap(){
		Map<String,PsiLadingBill> rs = Maps.newLinkedHashMap();
		for (PurchaseOrderItem item : items) {
			for(PsiLadingBillItem billItem:item.getBillItemList()){
				String key= billItem.getLadingBill().getBillNo();
				PsiLadingBill temp = rs.get(key);
				if(temp==null){
					temp= new PsiLadingBill();
					temp.setBillSta(billItem.getLadingBill().getBillSta());
					rs.put(key,temp);
				}
				temp.getItems().add(billItem);
			}
		}
		return rs;
	}
	
	@Transient
	public Map<String,PurchasePayment> getPaymentsMap(){
		Map<String,PurchasePayment> rs = Maps.newLinkedHashMap();
		if(this.getPayItem()!=null){
			String key= payItem.getPurchasePayment().getPaymentNo();
			PurchasePayment temp = rs.get(key);
			if(temp==null){
				temp =  payItem.getPurchasePayment();
				rs.put(key,temp);
			}
		}
		for (PurchaseOrderItem item : items) {
			for(PsiLadingBillItem billItem:item.getBillItemList()){
				for(PurchasePaymentItem payItem:billItem.getPayItems()){
					String key= payItem.getPurchasePayment().getPaymentNo();
					PurchasePayment temp = rs.get(key);
					if(temp==null){
						temp =  payItem.getPurchasePayment();
						rs.put(key,temp);
					}
				}
			}
		}
		return rs;
	}
	
	@Transient
	public List<PurchaseOrder> getTempOrders(){
		Map<String,PurchaseOrder> map = Maps.newLinkedHashMap();
		List<PurchaseOrder> orders = Lists.newArrayList();
		for (PurchaseOrderItem item : items) {
			PurchaseOrder temp=map.get(item.getProductName());
			if(temp==null){
				temp=new PurchaseOrder();
				temp.setSupplier(supplier);
				temp.setOrderSta(orderSta);
				temp.setToReview(toReview);
				temp.setToPartsOrder(toPartsOrder);
				temp.setOrderNo(orderNo);
				temp.setCurrencyType(currencyType);
				temp.setId(id);
				temp.setMerchandiser(merchandiser);
				temp.setDepositAmount(depositAmount);
				temp.setDepositPreAmount(depositPreAmount);
				temp.setPaymentAmount(paymentAmount);
				temp.setToPartsOrder(toPartsOrder);
				temp.setCreateDate(createDate);
				temp.setIsOverInventory(isOverInventory);
				temp.setOverRemark(overRemark);
				map.put(item.getProductName(), temp);
			}
			temp.getItems().add(item);
		}
		
		for(Map.Entry<String,PurchaseOrder> entry :map.entrySet()){
			PurchaseOrder order = entry.getValue();
			boolean flag =false;
			for(PurchaseOrderItem item:this.items){
				if(item!=null&&item.getProduct()!=null){
					Set<ProductParts> set =item.getProduct().getProductParts();
					if(set!=null&&set.size()>0){
						flag=true;
						break;
					}
				}
				
			}
			order.setNeedParts(flag);
			orders.add(order);
		}
		
		return orders;
	}
	
	@Transient
	public List<PurchaseOrder> getTempOrdersByDeliveryDate(){
		Map<String,PurchaseOrder> map = Maps.newLinkedHashMap();
		List<PurchaseOrder> orders = Lists.newArrayList();
		for (PurchaseOrderItem item : items) {
			PurchaseOrder temp=map.get(item.getProductName()+","+item.getActualDeliveryDate());
			if(temp==null){
				temp=new PurchaseOrder();
				temp.setSupplier(supplier);
				temp.setOrderSta(orderSta);
				temp.setToReview(toReview);
				temp.setToPartsOrder(toPartsOrder);
				temp.setOrderNo(orderNo);
				temp.setCurrencyType(currencyType);
				temp.setId(id);
				temp.setMerchandiser(merchandiser);
				temp.setDepositAmount(depositAmount);
				temp.setDepositPreAmount(depositPreAmount);
				temp.setPaymentAmount(paymentAmount);
				temp.setToPartsOrder(toPartsOrder);
				temp.setCreateDate(createDate);
				temp.setIsOverInventory(isOverInventory);
				temp.setOverRemark(overRemark);
				map.put(item.getProductName()+","+item.getActualDeliveryDate(), temp);
			}
			temp.getItems().add(item);
		}
		
		orders.addAll(map.values());
		
		return orders;
	}
	
	@Transient
	public String getTempProductName(){
		String rs ="";
		for(PurchaseOrderItem item :items){
			rs=item.getProductName();
			break;
		}
		return rs;
	}
	@Transient
	public Float  getVolume(){
		float rs = 0f;
		for (PurchaseOrderItem item : items){
			if("3".equals(orderSta)){
				rs+=item.getVolume()*item.getQuantityUnReceived()/item.getQuantityOrdered();
			}else{
				rs+=item.getVolume();
			}
		}
		return rs;
	}
	
	@Transient
	public Float  getWeight(){
		float rs = 0f;
		for (PurchaseOrderItem item : items) {
			if("3".equals(orderSta)){
				rs+=item.getWeight()*item.getQuantityUnReceived()/item.getQuantityOrdered();
			}else{
				rs+=item.getWeight();
			}
		}
		return rs;
	}
	
	@Transient
	public Float  getLessCargoVolume(){
		float rs = 0f;
		for (PurchaseOrderItem item : items) {
			rs+=item.getLessCargoVolume();
		}
		return rs;
	}
	
	@Transient
	public Float  getLessCargoWeight(){
		float rs = 0f;
		for (PurchaseOrderItem item : items) {
			rs+=item.getLessCargoWeight();
		}
		return rs;
	}

	
	/***
	 *是否可编辑订单map
	 * 
	 */
	@Transient
	public Map<String,String>  getNotEditMap() {
		Map<String,String> map = Maps.newHashMap();
		for (PurchaseOrderItem item : items) {
			//查询原始订单的items里有无已经付款或已申请付款的提单
			if(map.get(this.orderNo)==null){
				if(this.depositAmount.add(this.depositPreAmount).add(this.paymentAmount).compareTo(BigDecimal.ZERO)>0){
					map.put(orderNo, "1");
				}else{
					BigDecimal ladingPay = BigDecimal.ZERO;
					for(PsiLadingBillItem billItem:item.getBillItemList()){
						ladingPay=ladingPay.add(billItem.getLadingBill().getTotalPaymentPreAmount()).add(billItem.getLadingBill().getTotalPaymentPreAmount());
						if(ladingPay.compareTo(BigDecimal.ZERO)>0){
							map.put(orderNo, "1");
							break;
						}
					}
				}
			}
		}	
		return map;
	}
	//不分国家名字  算出产品、颜色 对应的未收货数
	@Transient
    public Map<String,Integer>   getProColorUnReceivedQuantity(){
		Map<String,Integer> proColorMap = Maps.newHashMap();
		for(PurchaseOrderItem item :items){
			String key = item.getProduct().getId()+","+item.getColorCode();
			Integer unReceivedQuantity =item.getQuantityUnReceived();
			if(proColorMap.get(key)!=null){
				unReceivedQuantity+=proColorMap.get(key);
			}
			proColorMap.put(key, unReceivedQuantity);
		}
		return proColorMap;
	}

	public String getIsOverInventory() {
		return isOverInventory;
	}

	public void setIsOverInventory(String isOverInventory) {
		this.isOverInventory = isOverInventory;
	}

	public String getOverRemark() {
		return overRemark;
	}

	public void setOverRemark(String overRemark) {
		this.overRemark = overRemark;
	}
	
	@Transient
	public String getOfflineSta(){
		String off="0";
		for(PurchaseOrderItem item:items){
			if(item.getQuantityOffOrdered()>0){
				off="1";
				break;
			}
		}
		return off;
	}

	public String getPiReviewSta() {
		return piReviewSta;
	}

	public void setPiReviewSta(String piReviewSta) {
		this.piReviewSta = piReviewSta;
	}


	@Transient
	public BigDecimal getQuantityAmount(){
		BigDecimal total = new BigDecimal(0);
		for(PurchaseOrderItem item:items){
			if(item.getItemPrice()!=null){
				total=total.add(item.getItemPrice().multiply(new BigDecimal(item.getQuantityOrdered())));
			}
		}
		return total;
	}
	
	@Transient
	public boolean getDiffPrice(){
		boolean returnFlag=false;
		BigDecimal b = BigDecimal.ZERO;
		for(int i=0;i<items.size();i++){
			if(i==0){
				b=items.get(i).getItemPrice();
			}else{
				if(b!=null&&!(b.compareTo(items.get(i).getItemPrice())==0)){
					returnFlag=true;
					break;
				}
			}
		}
		return returnFlag;
	}
	@Transient
	public Map<String,BigDecimal> getCountryPrice(){
		Map<String,BigDecimal> rs = Maps.newHashMap();
		for(int i=0;i<items.size();i++){
			PurchaseOrderItem item = items.get(i);
			String country = item.getCountryCode();
			rs.put(country, item.getItemPrice());
		}
		return rs;
	}
	
}


