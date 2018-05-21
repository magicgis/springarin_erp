/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 产品配件Entity
 * @author Michael
 * @version 2015-06-02
 */
@Entity
@Table(name = "lc_psi_parts_order")
public class LcPsiPartsOrder  implements Serializable{   
	
	private static final long serialVersionUID = -740810877149296804L;
	private 	 Integer 		id; 					// 编号
	private		 String  		partsOrderNo;         	// 配件订单号
	private      Integer   	    purchaseOrderId;		// 采购订单id
	private      String    	    purchaseOrderNo;		// 采购订单号
	private  	 PsiSupplier    supplier;        		// 供应商
	private      Float      	totalAmount;            // 总金额
	private      Float      	paymentAmount;          // 支付总额
	private      Float      	prePaymentAmount;       // 预支付总额
	private      String     	currencyType;           // 货币类型  
	private      Integer    	deposit;                // 定金比例
	private      String     	orderSta;               // 订单状态
	private      String     	paymentSta;             // 支付状态
	private      String     	remark;                 // 备注
	private      Date       	purchaseDate;           // 采购日期
    private      String     	piFilePath;             // PI文件路径
	private      User       	createUser;     		// 创建人
	private      Date       	createDate;             // 创建时间
	private      User       	updateUser;             // 修改人
	private      Date       	updateDate;      		// 修改时间
	private      User       	cancelUser;             // 取消人
	private      Date       	cancelDate;      		// 取消时间
	private      User       	sureUser;             	// 确认人
	private      Date       	sureDate;      			// 确认时间
	private 	 String     	oldItemIds;             // 老items
	private      String     	sendEamil="0";          // 发送邮件状态
	private		 Date       	receiveFinishedDate;    // 收货完成日期
	private		 Float      	depositPreAmount;       // 申请付定金金额
	private		 Float      	depositAmount;          // 已付定金金额
	private      String     	isProductReceive="0";   // 是否产品收货
	
	private		 LcPsiPartsPaymentItem    payItem;         // 付款明细项
	
	private      List<LcPsiPartsOrderItem> items = Lists.newArrayList();
	public  LcPsiPartsOrder(){
		super();
	}
	
	public  LcPsiPartsOrder(Integer id){
		this();
		this.id=id;
	}
	
	public String getIsProductReceive() {
		return isProductReceive;
	}

	public void setIsProductReceive(String isProductReceive) {
		this.isProductReceive = isProductReceive;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPartsOrderNo() {
		return partsOrderNo;
	}
	public void setPartsOrderNo(String partsOrderNo) {
		this.partsOrderNo = partsOrderNo;
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
	
	
	@OneToMany(mappedBy = "partsOrder")
	@Where(clause = "del_flag=0")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.ALL)
	public List<LcPsiPartsOrderItem> getItems() {
		return items;
	}
	public void setItems(List<LcPsiPartsOrderItem> items) {
		this.items = items;
	}
	
	
	public Float getPrePaymentAmount() {
		return prePaymentAmount;
	}

	public void setPrePaymentAmount(Float prePaymentAmount) {
		this.prePaymentAmount = prePaymentAmount;
	}

	@Transient
	public Integer getItemsQuantityReceived() {
		int rs = 0;
		for (LcPsiPartsOrderItem item : items) {
			rs+=item.getQuantityReceived();
		}
		return rs;
	}
	
	@Transient
	public Integer getItemsQuantityUnReceived() {
		int rs = 0;
		for (LcPsiPartsOrderItem item : items) {
			rs+=item.getQuantityOrdered()-item.getQuantityReceived();
		}
		return rs;
	}
	
	@Transient
	public Integer getItemsQuantityCanReceived() {
		int rs = 0;
		for (LcPsiPartsOrderItem item : items) {
			rs+=item.getQuantityOrdered()-item.getQuantityReceived()-item.getQuantityPreReceived();
		}
		return rs;
	}
	
	@Transient
	public Integer getItemsQuantityPreReceived() {
		int rs = 0;
		for (LcPsiPartsOrderItem item : items) {
			rs+=item.getQuantityPreReceived();
		}
		return rs;
	}
	
	
	@Transient
	public Integer getItemsQuantity() {
		int rs = 0;
		for (LcPsiPartsOrderItem item : items) {
			rs+=item.getQuantityOrdered();
		}
		return rs;
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
	public Float getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Float getPaymentAmount() {
		return paymentAmount;
	}
	public void setPaymentAmount(Float paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	public String getCurrencyType() {
		return currencyType;
	}
	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}
	public Integer getDeposit() {
		return deposit;
	}
	public void setDeposit(Integer deposit) {
		this.deposit = deposit;
	}
	public String getOrderSta() {
		return orderSta;
	}
	public void setOrderSta(String orderSta) {
		this.orderSta = orderSta;
	}
	public String getPaymentSta() {
		return paymentSta;
	}
	public void setPaymentSta(String paymentSta) {
		this.paymentSta = paymentSta;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getPiFilePath() {
		return piFilePath;
	}

	public void setPiFilePath(String piFilePath) {
		this.piFilePath = piFilePath;
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
	
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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

	public Date getSureDate() {
		return sureDate;
	}

	public void setSureDate(Date sureDate) {
		this.sureDate = sureDate;
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
	
	
	@Transient
	public String getOldItemIds() {
		return oldItemIds;
	}
	public void setOldItemIds(String oldItemIds) {
		this.oldItemIds = oldItemIds;
	}
	public String getSendEamil() {
		return sendEamil;
	}
	public void setSendEamil(String sendEamil) {
		this.sendEamil = sendEamil;
	}
	
	public Float getDepositPreAmount() {
		return depositPreAmount;
	}

	public void setDepositPreAmount(Float depositPreAmount) {
		this.depositPreAmount = depositPreAmount;
	}

	public Float getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(Float depositAmount) {
		this.depositAmount = depositAmount;
	}

	
	public Date getReceiveFinishedDate() {
		return receiveFinishedDate;
	}

	public void setReceiveFinishedDate(Date receiveFinishedDate) {
		this.receiveFinishedDate = receiveFinishedDate;
	}

	
	@OneToOne(fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="pay_item_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPsiPartsPaymentItem getPayItem() {
		return payItem;
	}

	public void setPayItem(LcPsiPartsPaymentItem payItem) {
		this.payItem = payItem;
	}
	
	@Transient
	public Float getTotalPaymentAmount(){
		return paymentAmount+depositAmount;
	}
	
	@Transient
	public Map<String,LcPsiPartsDelivery> getLadingsMap(){
		Map<String,LcPsiPartsDelivery> rs = Maps.newLinkedHashMap();
		for (LcPsiPartsOrderItem item : items) {
			for(LcPsiPartsDeliveryItem billItem:item.getDeliveryItemList()){
				String key= billItem.getPartsDelivery().getBillNo();
				LcPsiPartsDelivery temp = rs.get(key);
				if(temp==null){
					temp= new LcPsiPartsDelivery();
					temp.setBillSta(billItem.getPartsDelivery().getBillSta());
					rs.put(key,temp);
				}
				temp.getItems().add(billItem);
			}
		}
		return rs;
	}
	
	@Transient
	public Map<String,LcPsiPartsPayment> getPaymentsMap(){
		Map<String,LcPsiPartsPayment> rs = Maps.newLinkedHashMap();
		for (LcPsiPartsOrderItem item : items) {
			for(LcPsiPartsDeliveryItem billItem:item.getDeliveryItemList()){
				for(LcPsiPartsPaymentItem payItem:billItem.getPartsDelivery().getPayItems()){
					String key= payItem.getPsiPartsPayment().getPaymentNo();
					LcPsiPartsPayment temp = rs.get(key);
					if(temp==null){
						temp =  payItem.getPsiPartsPayment();
						rs.put(key,temp);
					}
				}
			}
		}
		return rs;
	}
	
	@Transient
	public String getOrderStaName(){
		String staName="";
		if("0".equals(orderSta)){
			staName="草稿";
		}else if("1".equals(orderSta)){
			staName="生产";
		}else if("3".equals(orderSta)){
			staName="部分收货";
		}else if("5".equals(orderSta)){
			staName="已收货";
		}else if("7".equals(orderSta)){
			staName="已完成";
		}else if("8".equals(orderSta)){
			staName="已取消";
		}
		return staName;
	}

	public  LcPsiPartsOrder(Integer id,String partsOrderNo,Float totalAmount,Float paymentAmount,Float prePaymentAmount,Integer deposit,String currencyType,Float depositAmount){
		this();
		this.id=id;
		this.partsOrderNo=partsOrderNo;
		this.totalAmount=totalAmount;
		this.paymentAmount=paymentAmount;
		this.prePaymentAmount=prePaymentAmount;
		this.deposit=deposit;
		this.currencyType=currencyType;
		this.depositAmount=depositAmount;
	}
	
}


