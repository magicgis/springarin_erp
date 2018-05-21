/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.util.Collections;
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

import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.SkuComparator;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 入库管理Entity
 * @author Michael
 * @version 2015-01-05
 */
@Entity
@Table(name = "psiInventoryIn")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiInventoryIn implements Serializable{
	private static final long serialVersionUID = 6572918137550900322L;
	private	   	  Integer   	id; 		
	private	      String    	billNo;
	private       Integer   	warehouseId;
	private       String    	warehouseName;
	private       Date      	addDate;
	private       Date      	addDateS;    //时间段查询
	private       User      	addUser;
	private       String    	attchmentPath;
	private       String    	remark;
	private       String    	operationType;
	private       String    	source;
	private       Integer   	tranLocalId;
	private       String    	tranLocalNo;
	private       List<PsiInventoryInItem> items=Lists.newArrayList()  ;
	private       String        dataFile;
	private       String        originName;
	private       String        dataType="new";          //数据类型     new  old  broken   renew

	private       Date      	dataDate;                //实际入库日期
	
	private       String        tranMan;            	 //送货人
	private       String        carNo;              	 //车牌号
	private       String        phone;              	 //电话
	private       String        flowNo;             	 //流水号
	
	
	private       String        supplierName;
	private       String        supplierPhone;
	private       String        formatDate;
	
	public PsiInventoryIn() {
		super();
	}

	public PsiInventoryIn(Integer id){
		this();
		this.id = id;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public Integer getTranLocalId() {
		return tranLocalId;
	}

	public void setTranLocalId(Integer tranLocalId) {
		this.tranLocalId = tranLocalId;
	}

	public String getTranLocalNo() {
		return tranLocalNo;
	}

	public void setTranLocalNo(String tranLocalNo) {
		this.tranLocalNo = tranLocalNo;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Date getDataDate() {
		return dataDate;
	}

	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@OneToMany(mappedBy = "inventoryIn")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.ALL)
	public List<PsiInventoryInItem> getItems() {
		return items;
	}
	
	@Transient
	public List<PsiInventoryInItem> getViewItems(){
		List<PsiInventoryInItem> views = Lists.newArrayList();
		 views.addAll(items);
		 Collections.sort(views,new SkuComparator()); 
		 return views;
	}

	public void setItems(List<PsiInventoryInItem> items) {
		this.items = items;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getDataFile() {
		return dataFile;
	}

	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}

	public String getOriginName() {
		return originName;
	}

	public void setOriginName(String originName) {
		this.originName = originName;
	}

	@Transient
	public Date getAddDateS() {
		return addDateS;
	}

	public void setAddDateS(Date addDateS) {
		this.addDateS = addDateS;
	}

	@ManyToOne()
	@JoinColumn(name="add_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getAddUser() {
		return addUser;
	}

	public void setAddUser(User addUser) {
		this.addUser = addUser;
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

	public String getTranMan() {
		return tranMan;
	}

	public void setTranMan(String tranMan) {
		this.tranMan = tranMan;
	}

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	
	@Transient
	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	
	@Transient
	public String getSupplierPhone() {
		return supplierPhone;
	}

	public void setSupplierPhone(String supplierPhone) {
		this.supplierPhone = supplierPhone;
	}

	@Transient
	public String getFormatDate() {
		return formatDate;
	}

	public void setFormatDate(String formatDate) {
		this.formatDate = formatDate;
	}
	
	@Transient
	public boolean getIsNew(){
		boolean flag=false;
		for(PsiInventoryInItem item:items){
			String conSku =item.getProductName();
			if(StringUtils.isNotEmpty(item.getColorCode())){
				conSku=item.getProductName()+"_"+item.getColorCode()+"_"+item.getCountryCode();
			}else{
				conSku=item.getProductName()+"_"+item.getColorCode();
			}
			if(item.getSku().equals(conSku)){
				flag=true;
				break;
			}
		}
		
		return flag;
	}
}


