/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.parts;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 配件收货付款详情Entity
 * @author Michael
 * @version 2015-07-03
 */
@Entity
@Table(name = "psiPartsDelivery")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiPartsDelivery implements Serializable {
	private static final long serialVersionUID = 3674298140244962623L;
	private 	Integer 		id; 		
	private 	String			billNo ;
	private 	PsiSupplier 	supplier;
	private 	String 			billSta;
	private 	String          attchmentPath;         
	private 	String          remark;
	private 	Float           totalPaymentPreAmount;         //已申请总额
	private 	Float           totalPaymentAmount;            //已支付总额
	private 	Float           totalAmount;                   //提单总额
	private 	User            sureUser;
	private 	Date            sureDate;
	private 	User            updateUser;
	private 	Date            updateDate;
	private 	User            cancelUser;
	private 	Date            cancelDate;
	private 	Date 			createDate;
	private 	User 			createUser;
	private 	Date       		deliveryDate;
	private 	String    		currencyType;                  //货币类型
	
	
	private     PsiSupplier     tranSupplier;               //承运供应商
	private 	List<PsiPartsDeliveryItem>     items = Lists.newArrayList() ;
	private 	List<PsiPartsPaymentItem>      payItems;  
	
	private 	String          oldItemIds;
	
	public PsiPartsDelivery() {
		super();
	}

	public PsiPartsDelivery(Integer id){
		this();
		this.id = id;
	}
	
	public PsiPartsDelivery(Integer id,String billNo,Float totalAmount,Float totalPaymentAmount,String currencyType){
		super();
		this.id = id;
		this.billNo=billNo;
		this.totalAmount=totalAmount;
		this.totalPaymentAmount = totalPaymentAmount;
		this.currencyType = currencyType;
	}
	
	
	@OneToMany(mappedBy = "ladingBill")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<PsiPartsPaymentItem> getPayItems() {
		return payItems;
	}

	public void setPayItems(List<PsiPartsPaymentItem> payItems) {
		this.payItems = payItems;
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


	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
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
	public String getBillSta() {
		return billSta;
	}
	public void setBillSta(String billSta) {
		this.billSta = billSta;
	}
	
	@Transient
	public String getOldItemIds() {
		return oldItemIds;
	}
	public void setOldItemIds(String oldItemIds) {
		this.oldItemIds = oldItemIds;
	}
	
	public void setAttchmentPathAppend(String attchmentPath) {
		if(StringUtils.isBlank(this.attchmentPath)){
			this.attchmentPath = attchmentPath;
		}else{
			this.attchmentPath = this.attchmentPath+","+attchmentPath;
		}
	}
	
	public String getAttchmentPath() {
		return attchmentPath;
	}
	public void setAttchmentPath(String attchmentPath) {
		this.attchmentPath = attchmentPath;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Float getTotalPaymentPreAmount() {
		return totalPaymentPreAmount;
	}
	public void setTotalPaymentPreAmount(Float totalPaymentPreAmount) {
		this.totalPaymentPreAmount = totalPaymentPreAmount;
	}
	public Float getTotalPaymentAmount() {
		return totalPaymentAmount;
	}
	public void setTotalPaymentAmount(Float totalPaymentAmount) {
		this.totalPaymentAmount = totalPaymentAmount;
	}
	public Float getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
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
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	@Transient
	public Integer getLadingTotal(){
		Integer ladingQuantity=0;
		for(PsiPartsDeliveryItem item:items){
			ladingQuantity+=item.getQuantityLading();
		}
		return ladingQuantity;
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
	
	public String getCurrencyType() {
		return currencyType;
	}
	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}
	
	@OneToMany(mappedBy = "partsDelivery",fetch=FetchType.LAZY)
	@Where(clause="del_flag=0")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.ALL)
	public List<PsiPartsDeliveryItem> getItems() {
		return items;
	}
	public void setItems(List<PsiPartsDeliveryItem> items) {
		this.items = items;
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
	
	@Transient
	public String getStaName(){
		String staStr = "";
		if("0".equals(billSta)){
			staStr="申请";
		}else if("1".equals(billSta)){
			staStr="确认";
		}else if("2".equals(billSta)){	
			staStr="已取消";
		}
		return staStr;
	}
	
}


