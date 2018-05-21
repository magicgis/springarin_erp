/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.modules.sys.entity.User;

/**
 * 采购付款明细Entity
 * @author Michael
 * @version 2014-12-24
 */
@Entity
@Table(name = "psi_inventory_revision_log")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiInventoryRevisionLog {
	
	private 	Integer			 id; 		   			  //id
	private     Integer          productId;     		  //产品id
	private     String           productName;   		  //产品名称
	private     String           colorCode;     		  //产品颜色
	private     String           countryCode;  		 	  //国家
	private     Integer          warehouseId;             //仓库id
	private     Integer          quantity;     			  //操作数量
	private     Integer          terminiWarehouseId;      //目的仓库id
	private     String           terminiWarehouseName;    //目的仓库名称
	private     User             operationUser;           //操作人
	private     String           operationType;           //操作类型
	private     String           operationSta="0";        //操作状态
	private     Date             operatinDate;            //操作时间
	private     String           dataType="new";          //数据类型     new  old  broken   renew
	private     String           isNewOperation;          //是新品的操作
	private     String           showFlag="0";            //是查看出入库名字:0，还是库内调整明细:1
	private     String           remark;                  //备注
	private     String           relativeNumber;          //相关单号
	private     String           sku;                     //sku
	private     String           lotFileName;             //批量出库文件名
	private     String           lotFileUrl;              //批量出库地址
	private     Integer          timelyQuantity;          //及时库存
	public PsiInventoryRevisionLog() {
		super();
	}

	
	public PsiInventoryRevisionLog(Integer id){
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

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Date getOperatinDate() {
		return operatinDate;
	}

	public void setOperatinDate(Date operatinDate) {
		this.operatinDate = operatinDate;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getTimelyQuantity() {
		return timelyQuantity;
	}

	public void setTimelyQuantity(Integer timelyQuantity) {
		this.timelyQuantity = timelyQuantity;
	}

	public String getRelativeNumber() {
		return relativeNumber;
	}

	public void setRelativeNumber(String relativeNumber) {
		this.relativeNumber = relativeNumber;
	}

	public String getColorCode() {
		return colorCode;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Transient
	public String getShowFlag() {
		return showFlag;
	}

	public void setShowFlag(String showFlag) {
		this.showFlag = showFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Transient
	public String getIsNewOperation() {
		return isNewOperation;
	}

	public void setIsNewOperation(String isNewOperation) {
		this.isNewOperation = isNewOperation;
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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getTerminiWarehouseId() {
		return terminiWarehouseId;
	}

	public void setTerminiWarehouseId(Integer terminiWarehouseId) {
		this.terminiWarehouseId = terminiWarehouseId;
	}

	public String getTerminiWarehouseName() {
		return terminiWarehouseName;
	}

	public void setTerminiWarehouseName(String terminiWarehouseName) {
		this.terminiWarehouseName = terminiWarehouseName;
	}

	@ManyToOne()
	@JoinColumn(name="operation_user_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getOperationUser() {
		return operationUser;
	}

	public void setOperationUser(User operationUser) {
		this.operationUser = operationUser;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getOperationSta() {
		return operationSta;
	}

	public void setOperationSta(String operationSta) {
		this.operationSta = operationSta;
	}

	@Transient
	public String getLotFileName() {
		return lotFileName;
	}

	public void setLotFileName(String lotFileName) {
		this.lotFileName = lotFileName;
	}

	
	@Transient
	public String getLotFileUrl() {
		return lotFileUrl;
	}

	public void setLotFileUrl(String lotFileUrl) {
		this.lotFileUrl = lotFileUrl;
	}
	
	
	@Transient
	public String getProductColorCountry(){
		if(StringUtils.isEmpty(colorCode)){
			return productName+"_"+countryCode;
		}else{
			return productName+"_"+colorCode+"_"+countryCode;
		}
	}
	
	
	

}


