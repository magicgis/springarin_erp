/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.parts;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import com.springrain.erp.modules.psi.entity.ProductParts;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 产品配件Entity
 * @author Michael
 * @version 2015-06-01
 */  
@Entity
@Table(name = "psi_parts")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiParts implements Serializable{  
	private static final long serialVersionUID = -5650003616706304971L;
	private	    Integer      		id; 		  	 // id
	private 	String		 		partsName; 	  	 // 名称
	private 	String		 		partsType; 	 	 // 类型
	private 	String		 		remark;    	 	 // 备注
	private     String       		delFlag="0";     // 删除类型
	private     User         		createUser;      // 创建人
	private     Date         		createDate;      // 创建时间
	private     User        		updateUser;      // 编辑人
	private     Date         		updateDate;      // 编辑时间
	private     String       		attchmentPath;   // 合同地址
	private     String       		image;           // 图片路径
	private     String       		description;     // 描述
	private     Integer 	 		producePeriod;   // 生产周期
	private     PsiSupplier         supplier;        // 供应商
	private     Float               price;           // 价格
	private     Float               rmbPrice;        // 人民币价格
	private     Integer             moq;             // 最小下单量
	private     String              deliveryDate;    // 收货日期（不入库）
	private     String              priceChangeLog;
	private     Set<ProductParts>   productParts = Sets.newHashSet();
	
	private     Float               oldPrice;         // 价格
	private     Float               oldRmbPrice;      // 人民币价格
	
	
	
	public PsiParts() {
		super();
	}
	
	public PsiParts(Integer id,String partsName,String deliveryDate) {
		super();
		this.id=id;
		this.partsName=partsName;
		this.deliveryDate=deliveryDate;
	}
	
	public PsiParts(Integer id){
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

	public String getPartsName() {
		return partsName;
	}

	public void setPartsName(String partsName) {
		this.partsName = partsName;
	}

	public String getPartsType() {
		return partsType;
	}

	public void setPartsType(String partsType) {
		this.partsType = partsType;
	}

//	@JsonIgnore
//	@OneToMany(mappedBy = "parts",cascade=javax.persistence.CascadeType.ALL)
//	@NotFound(action = NotFoundAction.IGNORE)
//	public List<PartsSupplier> getPartsSuppliers() {
//		return partsSuppliers;
//	}
//
//	public void setPartsSuppliers(List<PartsSupplier> partsSuppliers) {
//		this.partsSuppliers = partsSuppliers;
//	}
	
	@Transient
	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Integer getMoq() {
		return moq;
	}

	public void setMoq(Integer moq) {
		this.moq = moq;
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

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getRmbPrice() {
		return rmbPrice;
	}

	public void setRmbPrice(Float rmbPrice) {
		this.rmbPrice = rmbPrice;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}


	@ManyToOne()
	@JoinColumn(name="createUser")
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
	@JoinColumn(name="updateUser")
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getProducePeriod() {
		return producePeriod;
	}

	public void setProducePeriod(Integer producePeriod) {
		this.producePeriod = producePeriod;
	}

	@JsonIgnore
	@OneToMany(mappedBy = "parts")
	@NotFound(action = NotFoundAction.IGNORE)
	public Set<ProductParts> getProductParts() {
		return productParts;
	}

	public void setProductParts(Set<ProductParts> productParts) {
		this.productParts = productParts;
	}

	public String getPriceChangeLog() {
		return priceChangeLog;
	}

	public void setPriceChangeLog(String priceChangeLog) {
		//if(StringUtils.isBlank(this.priceChangeLog)){
			this.priceChangeLog=priceChangeLog;
//		}else{
//			this.priceChangeLog = this.priceChangeLog+ priceChangeLog;
//		}
	}

	
	@Transient
	public Float getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(Float oldPrice) {
		this.oldPrice = oldPrice;
	}

	@Transient
	public Float getOldRmbPrice() {
		return oldRmbPrice;
	}

	public void setOldRmbPrice(Float oldRmbPrice) {
		this.oldRmbPrice = oldRmbPrice;
	}
	
	

}


