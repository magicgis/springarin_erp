/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Length;

import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.DataEntity;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 配件库存盘点Entity
 * @author Michael
 * @version 2015-07-31
 */
@Entity
@Table(name = "lc_psi_parts_inventory_taking")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPsiPartsInventoryTaking implements Serializable{
	private static final long serialVersionUID = -9042000024905683364L;
	private		 Integer	 id; 			// ID
	private      String      takingNo;  	// NO
	private      String      takingType;	// 盘点类型（盘入或盘出） 
	private      String      operateType;   // 操作类型
	private      String      remark;        // 备注
	private      String      dataFile;      // 数据文件 （预留）
	private      String      originName;    // 原始文件名
	private      User        createUser;    // 创建人
	private      Date        createDate;    // 创建时间
	private      List<LcPsiPartsInventoryTakingItem>  items = Lists.newArrayList();

	public LcPsiPartsInventoryTaking() {
		super();
	}

	public LcPsiPartsInventoryTaking(Integer id){
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

	public String getTakingNo() {
		return takingNo;
	}

	public void setTakingNo(String takingNo) {
		this.takingNo = takingNo;
	}

	public String getTakingType() {
		return takingType;
	}

	public void setTakingType(String takingType) {
		this.takingType = takingType;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	@OneToMany(mappedBy = "partsTaking",fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public List<LcPsiPartsInventoryTakingItem> getItems() {
		return items;
	}

	public void setItems(List<LcPsiPartsInventoryTakingItem> items) {
		this.items = items;
	}
	

}


