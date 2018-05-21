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
import com.springrain.erp.modules.sys.entity.User;

/**
 * 出库管理Entity
 * @author Michael
 * @version 2015-01-05
 */
@Entity
@Table(name = "psiInventoryOut")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiInventoryOut  implements Serializable{
	private static final long serialVersionUID = -5209155521466057616L;
	private   	 Integer 			 id; 	
	private	   	 String   		 billNo;
	private   	 Integer  		 warehouseId;
	private      String   		 warehouseName;
	private      Date     		 addDate;
	private      Date     		 addDateS;             //时间段查询
	private      User     		 addUser;
	private      String   		 attchmentPath;
	private      String    		 remark;
	private      String    		 operationType;
	private      String   		 whereabouts;
	private      String   		 tranFbaNo;
	private      String  		 tranLocalId;
	private      String          tranLocalIds;
	private      String   		 tranLocalNo;
	private      String          dataFile;
	private      String          originName;
	private      String          dataType="new";        //数据类型     new  old  broken   renew
	private      Integer         palletQuantity;        //托盘数
	private      Date            ladingDate;            //提货日期
	private      String          trackBarcode;          //跟踪条码
	private      Float           tranWeight;            //运输重量
	private      Float           tranVolume;            //运输体积
	private      String          supplier;        		// 供应商
	private      String          pdfFile;				//pdf凭证
	private       Date      	 dataDate;              //实际出库日期
	private       String         tranMan;            	//提货人名字
	private       String         carNo;             	//车牌号
	private       String         phone;              	//电话
	private       String         idCard;             	//身份证
	private       String         boxNo;              	//海运柜号
	private       String         flowNo;             	//流水号
	private 	  Integer		 quantity1;             //30kg (其他包裹数也放这个字段)(非数据库字段)
	private 	  Integer		 quantity2;             //15kg (非数据库字段)
	private 	  Integer		 quantity3;             //15kg (非数据库字段)
	private 	  Float			 fee;                   //费用
	
	private      List<PsiInventoryOutItem> items=Lists.newArrayList();

	public Date getDataDate() {
		return dataDate;
	}

	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public Integer getPalletQuantity() {
		return palletQuantity;
	}

	public void setPalletQuantity(Integer palletQuantity) {
		this.palletQuantity = palletQuantity;
	}

	public Date getLadingDate() {
		return ladingDate;
	}

	public void setLadingDate(Date ladingDate) {
		this.ladingDate = ladingDate;
	}

	public String getTrackBarcode() {
		return trackBarcode;
	}

	public void setTrackBarcode(String trackBarcode) {
		this.trackBarcode = trackBarcode;
	}

	public PsiInventoryOut() {
		super();
	}

	public PsiInventoryOut(Integer id){
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

	@OneToMany(mappedBy = "inventoryOut")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.ALL)
	public List<PsiInventoryOutItem> getItems() {
		return items;
	}
	
	public void setItems(List<PsiInventoryOutItem> items) {
		this.items = items;
	}
	
	@Transient
	public List<PsiInventoryOutItem> getViewItems(){
		List<PsiInventoryOutItem> views = Lists.newArrayList();
		 views.addAll(items);
		 Collections.sort(views,new SkuComparator()); 
		 return views;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	
	@Transient
	public String getTranLocalIds() {
		return tranLocalIds;
	}

	public void setTranLocalIds(String tranLocalIds) {
		this.tranLocalIds = tranLocalIds;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

//	public Integer getTranFbaId() {
//		return tranFbaId;
//	}
//
//	public void setTranFbaId(Integer tranFbaId) {
//		this.tranFbaId = tranFbaId;
//	}

	public String getTranFbaNo() {
		return tranFbaNo;
	}

	public void setTranFbaNo(String tranFbaNo) {
		this.tranFbaNo = tranFbaNo;
	}

	public String getTranLocalId() {
		return tranLocalId;
	}

	public void setTranLocalId(String tranLocalId) {
		this.tranLocalId = tranLocalId;
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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getTranLocalNo() {
		return tranLocalNo;
	}

	public void setTranLocalNo(String tranLocalNo) {
		this.tranLocalNo = tranLocalNo;
	}

	public String getWhereabouts() {
		return whereabouts;
	}

	public void setWhereabouts(String whereabouts) {
		this.whereabouts = whereabouts;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
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
	
    public String getPdfFile() {
		return pdfFile;
	}

	public void setPdfFile(String pdfFile) {
		this.pdfFile = pdfFile;
	}

	@Transient
	public Float getTranWeight() {
		return tranWeight;
	}

	public void setTranWeight(Float tranWeight) {
		this.tranWeight = tranWeight;
	}
    @Transient
	public Float getTranVolume() {
		return tranVolume;
	}

	public void setTranVolume(Float tranVolume) {
		this.tranVolume = tranVolume;
	}

	@Transient
	public Integer getTotalQuantity() {
		int rs = 0;
		for (PsiInventoryOutItem item : this.items) {
			rs+=(item.getQuantity()==null?0:item.getQuantity());
		}
		return rs;
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

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}

	public String getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	@Transient
	public Integer getQuantity1() {
		return quantity1;
	}

	public void setQuantity1(Integer quantity1) {
		this.quantity1 = quantity1;
	}

	
	@Transient
	public Integer getQuantity2() {
		return quantity2;
	}

	public void setQuantity2(Integer quantity2) {
		this.quantity2 = quantity2;
	}

	@Transient
	public Integer getQuantity3() {
		return quantity3;
	}

	public void setQuantity3(Integer quantity3) {
		this.quantity3 = quantity3;
	}

	@Transient
	public Float getFee() {
		return fee;
	}

	public void setFee(Float fee) {
		this.fee = fee;
	}
	
}


