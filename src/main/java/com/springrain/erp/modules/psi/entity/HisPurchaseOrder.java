/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 采购订单Entity
 * @author Michael
 * @version 2014-10-29
 */
@Entity
@Table(name = "lc_psi_his_purchase_order")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class HisPurchaseOrder implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer   id; 		                     // id
	private String    orderNo; 	                     // 订单编号
	private String	  orderSta;                      //订单状态
	private String    currencyType;                  //货币类型
	private BigDecimal     totalAmount;                    //总金额
	private Date      createDate;                    //创建日期
	private Date      purchaseDate;                  //下单日期
	private String    receivedStore;                 //收货仓库
	private String    delFlag;
	
	private User      createUser;                    //创建人
	private User      merchandiser;                  //跟单员
	private PsiSupplier  supplier;                   //供应商
	private String    oldItemIds ;                   //老items
	private Integer    deposit;                      //供应商定金
	private String    piFilePath;                    //PI文件路径
	private String    modifyMemo;                    //修改备注
	private String    versionNo;                     //版本号
	
	private Date      updateDate;                    //最后更新日期
	private User      updateUser;                    //最后更新人
	private Date      sureDate;                      //确认日期
	private User      sureUser;                      //确认人
	
	private BigDecimal     depositAmount;                 //已付定金金额
	private Date      cancelDate;                    //取消日期
	private User      cancelUser;                    //取消人
	private BigDecimal     depositPreAmount;              //申请付定金金额
	
	private String    paySta;                        //付款状态
	private BigDecimal     paymentAmount;                 //付款总额
	
	private Integer   tempLessCargoQuantity;         //欠货数量             单品欠货统计
	
	private String    sendEmailFlag  = "0";
	
	@Transient
	public Integer getTempLessCargoQuantity() {
		return tempLessCargoQuantity;
	}

	public void setTempLessCargoQuantity(Integer tempLessCargoQuantity) {
		this.tempLessCargoQuantity = tempLessCargoQuantity;
	}

	private List<HisPurchaseOrderItem> items = Lists.newArrayList();
	
	
	public HisPurchaseOrder() {
		super();
	}

	public HisPurchaseOrder(Integer id){
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

	public String getSendEmailFlag() {
		return sendEmailFlag;
	}

	public void setSendEmailFlag(String sendEmailFlag) {
		this.sendEmailFlag = sendEmailFlag;
	}

	@Transient
	public  BigDecimal getDepositTotal(){
		return this.totalAmount.multiply(new BigDecimal(deposit/100f));
	}
	
	public String getPaySta() {
		return paySta;
	}

	public void setPaySta(String paySta) {
		this.paySta = paySta;
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
		for (HisPurchaseOrderItem item : items) {
			rs+=item.getQuantityReceived();
		}
		return rs;
	}
	
	@Transient
	public Integer getItemsQuantityPreReceived() {
		int rs = 0;
		for (HisPurchaseOrderItem item : items) {
			rs+=item.getQuantityPreReceived();
		}
		return rs;
	}

	@Transient
	public Integer getItemsQuantity() {
		int rs = 0;
		for (HisPurchaseOrderItem item : items) {
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
	
	@OneToMany(mappedBy = "purchaseOrder")
	@OrderBy(value="product,countryCode")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public List<HisPurchaseOrderItem> getItems() {
		return items;
	}
	
	public void setItems(List<HisPurchaseOrderItem> items) {
		this.items = items;
		if(items.size()>0){
			for(HisPurchaseOrderItem item:items){
				item.setPurchaseOrder(this);
			}
		}
	}
	
	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	@Transient
	public Map<String,Map<String,HisPurchaseOrder>> getOrderItemMap(){
		Map<String,Map<String,HisPurchaseOrder>>  rs = Maps.newHashMap();
		for (HisPurchaseOrderItem item : items) {
			String productName = item.getProduct().getName();
			Map<String,HisPurchaseOrder> tempMap = rs.get(productName);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(productName, tempMap);
			}
			String color = item.getColorCode();
			if(StringUtils.isEmpty(color)){
				color = "No Color";
			}
			HisPurchaseOrder order = tempMap.get(color);
			if(order == null){
				order = new HisPurchaseOrder();
				order.setPurchaseDate(purchaseDate);
				tempMap.put(color, order);
			}
			order.getItems().add(item);
		}
		
		for (HisPurchaseOrderItem item : items) {
			String productName = item.getProduct().getName()+"1";
			Map<String,HisPurchaseOrder> tempMap = rs.get(productName);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(productName, tempMap);
			}
			String color = item.getColorCode();
			if(StringUtils.isEmpty(color)){
				color = "No Color";
			}
			HisPurchaseOrder order = tempMap.get(color);
			if(order == null){
				order = new HisPurchaseOrder();
				order.setPurchaseDate(purchaseDate);
				tempMap.put(color, order);
			}
			order.getItems().add(item);
		}
		
		for (HisPurchaseOrderItem item : items) {
			String productName = item.getProduct().getName()+"2";
			Map<String,HisPurchaseOrder> tempMap = rs.get(productName);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(productName, tempMap);
			}
			String color = item.getColorCode();
			if(StringUtils.isEmpty(color)){
				color = "No Color";
			}
			HisPurchaseOrder order = tempMap.get(color);
			if(order == null){
				order = new HisPurchaseOrder();
				order.setPurchaseDate(purchaseDate);
				tempMap.put(color, order);
			}
			order.getItems().add(item);
		}
		return rs;
	}
	
	@Transient
	public Map<String,HisPurchaseOrder> getOrderItemsMap(){
		Map<String,HisPurchaseOrder>  rs = Maps.newLinkedHashMap();
		for (HisPurchaseOrderItem item : items) {
			String name = item.getProductName();
			HisPurchaseOrder temp=rs.get(name);
			if(temp==null){
				temp = new HisPurchaseOrder();
				rs.put(name, temp);
			}
			temp.getItems().add(item);
		}
		return rs;
	}
	
	
	@Transient
	public List<HisPurchaseOrder> getTempOrders(){
		Map<String,HisPurchaseOrder> map = Maps.newLinkedHashMap();
		List<HisPurchaseOrder> orders = Lists.newArrayList();
		for (HisPurchaseOrderItem item : items) {
			HisPurchaseOrder temp=map.get(item.getProductName());
			if(temp==null){
				temp=new HisPurchaseOrder();
				temp.setSupplier(supplier);
				temp.setOrderSta(orderSta);
				temp.setOrderNo(orderNo);
				temp.setCurrencyType(currencyType);
				temp.setId(id);
				temp.setMerchandiser(merchandiser);
				map.put(item.getProductName(), temp);
			}
			temp.getItems().add(item);
		}
		
		for(Map.Entry<String, HisPurchaseOrder> entry:map.entrySet()){
			orders.add(entry.getValue());
		}
		
		return orders;
	}
	
	@Transient
	public String getTempProductName(){
		String rs ="";
		for(HisPurchaseOrderItem item :items){
			rs=item.getProductName();
			break;
		}
		return rs;
	}
	@Transient
	public Float  getVolume(){
		float rs = 0f;
		for (HisPurchaseOrderItem item : items) {
			rs+=item.getVolume();
		}
		return rs;
	}
	
	@Transient
	public Float  getWeight(){
		float rs = 0f;
		for (HisPurchaseOrderItem item : items) {
			rs+=item.getWeight();
		}
		return rs;
	}
	
	@Transient
	public Float  getLessCargoVolume(){
		float rs = 0f;
		for (HisPurchaseOrderItem item : items) {
			rs+=item.getLessCargoVolume();
		}
		return rs;
	}
	
	@Transient
	public Float  getLessCargoWeight(){
		float rs = 0f;
		for (HisPurchaseOrderItem item : items) {
			rs+=item.getLessCargoWeight();
		}
		return rs;
	}
	
	public HisPurchaseOrder(PurchaseOrder order){
		this.createDate     = new Date();
		this.createUser     = order.getCreateUser();
		this.currencyType   = order.getCurrencyType();
		this.delFlag        = order.getDelFlag();
		this.deposit        = order.getDeposit();
		this.depositAmount  = order.getDepositAmount();
		this.depositPreAmount = order.getDepositPreAmount();
		this.merchandiser   = order.getMerchandiser();
		this.modifyMemo     = order.getModifyMemo();
		this.orderNo        = order.getOrderNo();
		this.orderSta       = order.getOrderSta();
		this.paymentAmount  = order.getPaymentAmount();
		this.paySta         = order.getPaySta();
		this.piFilePath     = order.getPiFilePath();
		this.purchaseDate   = order.getPurchaseDate();
		this.receivedStore  = order.getReceivedStore();
		this.supplier       = order.getSupplier();
		this.sureDate       = order.getSureDate();
		this.sureUser       = order.getSureUser();
		this.totalAmount    = order.getTotalAmount();
		this.updateDate     = order.getUpdateDate();
		this.updateUser     = order.getUpdateUser();
		this.versionNo      = order.getVersionNo();
		this.sendEmailFlag  = order.getSendEmailFlag();
		
		if(order.getItems()!=null && order.getItems().size()>0){
			List<HisPurchaseOrderItem> rs = Lists.newArrayList();
			for (PurchaseOrderItem item : order.getItems()) {
				rs.add(new HisPurchaseOrderItem(item, this));
			}
			this.items = rs;
		}
	}
	
	
	@Transient
	public List<HisPurchaseOrder> getTempOrdersByDeliveryDate(){
		Map<String,HisPurchaseOrder> map = Maps.newLinkedHashMap();
		List<HisPurchaseOrder> orders = Lists.newArrayList();
		for (HisPurchaseOrderItem item : items) {
			HisPurchaseOrder temp=map.get(item.getProductName()+","+item.getActualDeliveryDate());
			if(temp==null){
				temp=new HisPurchaseOrder();
				temp.setSupplier(supplier);
				temp.setOrderSta(orderSta);
				temp.setOrderNo(orderNo);
				temp.setCurrencyType(currencyType);
				temp.setId(id);
				temp.setMerchandiser(merchandiser);
				temp.setDepositAmount(depositAmount);
				temp.setDepositPreAmount(depositPreAmount);
				temp.setPaymentAmount(paymentAmount);
				temp.setCreateDate(createDate);
				map.put(item.getProductName()+","+item.getActualDeliveryDate(), temp);
			}
			temp.getItems().add(item);
		}
		
		orders.addAll(map.values());
		
		return orders;
	}
	
	@Transient
	public String getSnCode(){
		String yearLast = this.orderNo.substring(3,4);
		String month = this.orderNo.substring(4, 6);
		String day = this.orderNo.substring(6,8);
		String reSupplier=StringUtils.reverse(orderNo.substring(8, 11));
		return yearLast+changeMonth(month)+day+reSupplier;
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
}


